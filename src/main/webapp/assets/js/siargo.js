/* 快捷新增弹窗：内容加载完成后聚焦首个可编辑输入框。 */
(function (root) {
    'use strict';
    root.SiargoDialogForm = {
        focusFirstInput: function (container) {
            var $ = root.jQuery;
            function focus() {
                var page = $(container);
                if (!page.length || !page[0].isConnected) return;
                page.find('input:not([type="hidden"]), textarea')
                    .filter(':visible:enabled').not('[readonly]').first().trigger('focus');
            }
            // 平台随后会给输入框包装清空按钮，等本轮组件初始化结束再设置焦点。
            function afterInit() { root.setTimeout(focus, 0); }
            if (root.document.readyState === 'complete') afterInit();
            else $(root).one('load', afterInit);
        }
    };
}(window));

/* DMS 技术通知单：确认覆盖后上传；保存时由服务端复核确认凭据。 */
(function (root) {
    'use strict';

    // 流程与 DOM 分离，取消确认不改变 files/tokens，也不调用上传或删除。
    function createFlow(deps, editingId) {
        var files = [], tokens = Object.create(null), busy = false, saved = false;
        function changed() { deps.changed(files.slice(), Object.assign({}, tokens)); }
        function setBusy(value) { busy = value; deps.busy(value); }
        async function select(selection) {
            if (busy || saved) return;
            var category = deps.category();
            if (!category) { deps.alert('请选择文件类别'); return; }
            var names = Object.create(null);
            if (!editingId) files.forEach(function (file) { names[file.name.toLowerCase()] = true; });
            if ((!editingId && files.length + selection.length > 10) || (editingId && selection.length > 1)) {
                deps.alert(editingId ? '编辑时只能选择一个文件' : '最多上传10个文件'); return;
            }
            for (var file of selection) {
                if (!/^[^.]+\.pdf$/i.test(file.name) || file.size > 102400 * 1024) {
                    deps.alert('仅支持100MB以内的PDF，文件名除扩展名外不能包含点号'); return;
                }
                if (names[file.name.toLowerCase()]) {
                    deps.alert('本次已选择同名文件，请先移除：' + file.name); return;
                }
                names[file.name.toLowerCase()] = true;
            }
            setBusy(true);
            try {
                for (var selected of selection) {
                    var check = await deps.check(category, selected.name);
                    if (check.state !== 'ok') { deps.alert(check.msg || '无法检查当前文件'); break; }
                    if (check.requiresOverwrite && !await deps.confirm(check, editingId)) continue;
                    // 用户确认期间类别可能被其他代码改变，旧确认不得用于新类别。
                    if (category !== deps.category()) { deps.alert('文件类别已变化，请重新选择文件'); break; }
                    var uploaded = await deps.upload(selected);
                    if (uploaded.state !== 'ok' || !uploaded.data) {
                        deps.alert(uploaded.msg || selected.name + ' 上传失败'); continue;
                    }
                    var previous = editingId ? files.slice() : [];
                    if (editingId) { files = []; tokens = Object.create(null); }
                    files.push({name: selected.name, path: uploaded.data});
                    if (check.requiresOverwrite) tokens[selected.name] = check.overwriteToken;
                    changed();
                    for (var old of previous) await deps.remove(old.path);
                }
            } catch (error) {
                deps.alert(error.message || '上传失败，请重试');
            } finally { setBusy(false); }
        }
        async function remove(index) {
            if (busy || saved || !files[index]) return;
            setBusy(true);
            try {
                var file = files[index];
                await deps.remove(file.path);
                files.splice(index, 1); delete tokens[file.name]; changed();
            } catch (error) { deps.alert(error.message || '临时文件清理失败'); }
            finally { setBusy(false); }
        }
        async function submit(send) {
            if (busy || saved) return null;
            if (!editingId && !files.length) { deps.alert('请上传文件'); return null; }
            setBusy(true);
            try {
                while (true) {
                    var category = deps.category();
                    var ret = await send();
                    if (ret.state === 'ok' || ret.state === 'warn') { saved = true; return ret; }
                    if (!ret.requiresOverwrite || !ret.conflicts || !ret.conflicts.length) {
                        if (ret.tempFileDeleted) { files = []; tokens = Object.create(null); changed(); }
                        return ret;
                    }
                    // 全部确认后才一起更新 token；任意“否”仅关闭提示，保留原状态。
                    var confirmed = Object.create(null);
                    for (var conflict of ret.conflicts) {
                        if (!await deps.confirm(conflict, editingId)) return null;
                        confirmed[conflict.fileName] = conflict.overwriteToken;
                    }
                    if (category !== deps.category()) {
                        deps.alert('文件类别已变化，请重新保存并确认'); return null;
                    }
                    Object.assign(tokens, confirmed); changed();
                }
            } finally { setBusy(false); }
        }
        return {
            select: select, remove: remove, submit: submit,
            categoryChanged: function () { tokens = Object.create(null); changed(); },
            pathsToClean: function () { return saved ? [] : files.map(function (file) { return file.path; }); },
            isBusy: function () { return busy; }
        };
    }

    function init(formId) {
        var $ = root.jQuery, form = $('#' + formId);
        if (!form.length || form.data('dmsInitialized')) return;
        form.data('dmsInitialized', true);
        root.needPjax = false;
        function field(name) { return form.find('[name="' + name + '"]'); }
        function role(name) { return form.find('[data-dms="' + name + '"]'); }
        function alertText(message) { root.LayerMsgBox.alert($('<div>').text(message).html(), 2); }
        function request(options) {
            return new Promise(function (resolve, reject) {
                $.ajax(Object.assign({dataType: 'json', success: resolve,
                    error: function () { reject(new Error('网络请求失败，请重试')); }}, options));
            });
        }
        var editingId = field('dmsFile.id').val(), cleaned = false;
        var flow = createFlow({
            category: function () { return field('dmsFile.categoryId').val() || ''; },
            alert: alertText,
            busy: function (busy) {
                role('choose').add(role('input')).prop('disabled', busy);
                role('list').find('button').prop('disabled', busy);
                // 类别属于提交字段，保存序列化前不能 disabled；拦截用户更改而保留值。
                field('dmsFile.categoryId').attr('aria-disabled', busy ? 'true' : 'false');
            },
            check: function (category, name) {
                return request({url: root.actionUrl('admin/siargo/dms/file/checkOverwrite'),
                    type: 'GET', data: {categoryId: category, fileName: name}, cache: false});
            },
            confirm: function (conflict, id) {
                return new Promise(function (resolve) {
                    var message = '文件“' + conflict.fileName + '”已存在，是否覆盖当前文件？';
                    message += '\n选择“是”继续上传，点击保存后才替换。';
                    if (id && conflict.targetId && String(conflict.targetId) !== String(id)) {
                        message += '\n将覆盖同名记录，正在编辑的其他记录保持不变。';
                    }
                    var settled = false;
                    function finish(value) { if (!settled) { settled = true; resolve(value); } }
                    root.layer.confirm($('<div>').text(message).html().replace(/\n/g, '<br>'),
                        {icon: 3, title: '覆盖确认', btn: ['是', '否'],
                            cancel: function () { finish(false); }, end: function () { finish(false); }},
                        function (index) { finish(true); root.layer.close(index); },
                        function (index) { finish(false); root.layer.close(index); });
                });
            },
            upload: function (file) {
                var data = new FormData(); data.append('file', file);
                return request({url: root.actionUrl('admin/siargo/dms/file/uploadFile'), type: 'POST',
                    data: data, processData: false, contentType: false});
            },
            remove: async function (path) {
                var ret = await request({url: root.actionUrl('admin/siargo/dms/file/deleteTempFile'),
                    type: 'POST', data: {filePath: path}});
                if (ret.state !== 'ok') throw new Error(ret.msg || '临时文件清理失败');
            },
            changed: function (files, tokens) {
                field('tempFilePath').val(files.map(function (file) { return file.path; }).join(','));
                field('overwriteTokens').val(JSON.stringify(tokens));
                role('list').empty();
                files.forEach(function (file, index) {
                    var item = $('<div class="d-flex align-items-center mb-1">');
                    item.append($('<i class="fa fa-file mr-2">'));
                    item.append($('<span class="mr-2">').text(file.name));
                    item.append($('<button type="button" class="btn btn-sm btn-outline-danger" title="移除暂存文件">')
                        .attr('data-index', index).append('<i class="fa fa-times"></i>'));
                    role('list').append(item);
                });
                role('progress').text(files.length ? '已上传 ' + files.length + ' 个文件，保存后生效' : '');
            }
        }, editingId);
        form.data('dmsFlow', flow);
        role('choose').on('click.dms', function () { if (!flow.isBusy()) role('input').trigger('click'); });
        role('input').on('change.dms', function () {
            var files = Array.prototype.slice.call(this.files || []);
            this.value = ''; if (files.length) flow.select(files);
        });
        role('list').on('click.dms', 'button[data-index]', function () { flow.remove(Number($(this).attr('data-index'))); });
        field('dmsFile.categoryId').on('select2:opening.dms', function (event) {
            if (flow.isBusy()) event.preventDefault();
        }).on('change.dms', function () { flow.categoryChanged(); });

        var date = field('dmsFile.activeDate');
        if (!$.trim(date.val())) {
            var today = new Date();
            date.val(today.getFullYear() + '-' + ('0' + (today.getMonth() + 1)).slice(-2)
                + '-' + ('0' + today.getDate()).slice(-2));
        }
        var keywords = [];
        function renderKeywords() {
            role('tags').empty();
            keywords.forEach(function (keyword, index) {
                role('tags').append($('<span class="badge badge-primary mr-1 mb-1 keyword-tag">')
                    .append($('<span>').text(keyword))
                    .append($('<button type="button" class="btn btn-link btn-sm p-0 ml-1 text-white" title="删除关键字">')
                        .attr('data-index', index).text('×')));
            });
            field('keywords').val(keywords.join(','));
        }
        function addKeyword(keyword) {
            keyword = $.trim(keyword);
            if (keyword && keywords.indexOf(keyword) < 0) keywords.push(keyword);
        }
        (field('keywords').val() || '').split(',').forEach(addKeyword); renderKeywords();
        role('keyword').on('keydown.dms', function (event) {
            if (event.keyCode === 13) { event.preventDefault(); addKeyword($(this).val()); $(this).val(''); renderKeywords(); }
        });
        role('tags').on('click.dms', 'button', function () {
            keywords.splice(Number($(this).attr('data-index')), 1); renderKeywords();
        });

        function submitButtons(busy) {
            if (busy) {
                root.changeParentLayerDialogOkBtnStateToSubmiting();
                root.changeParentLayerAllDialogBtnStateToDisabled();
                root.changeLayerDialogFormSubmiting(form);
            } else {
                root.cancelLayerDialogFormSubmiting(form);
                root.cancelParentLayerDialogOkBtnStateToSubmiting();
                root.cancelParentLayerAllDialogBtnStateToDisabled();
            }
        }
        var submitting = false;
        root.submitThisForm = async function (successCallback) {
            if (submitting || flow.isBusy()) return false;
            if (!root.FormChecker.check(form)) return false;
            submitting = true;
            try {
                var ret = await flow.submit(function () {
                    submitButtons(true);
                    root.LayerMsgBox.loading('正在提交...', 10000);
                    return new Promise(function (resolve, reject) {
                        form.ajaxSubmit({type: 'post', dataType: 'json', url: root.actionUrl(form.attr('action')),
                            success: function (result) { root.LayerMsgBox.closeLoadingNow(); resolve(result); },
                            error: function () { root.LayerMsgBox.closeLoadingNow(); reject(new Error('提交请求失败，请核对保存结果后重试')); }});
                    });
                });
                if (!ret) return false;
                if (ret.state === 'ok' || ret.state === 'warn') {
                    var done = function () { if (successCallback) successCallback(); };
                    if (ret.state === 'warn') root.LayerMsgBox.warning(ret.msg || '操作完成，请留意提示', done);
                    else root.LayerMsgBox.success(ret.msg || '保存成功', 500, done);
                } else if (ret.msg === 'jbolt_system_locked') root.showJboltLockSystem();
                else if (ret.msg === 'jbolt_nologin') root.showReloginDialog(true, true, function () {});
                else alertText((ret.msg || '保存失败') + (ret.tempFileDeleted ? '，请重新上传文件后再保存。' : ''));
            } catch (error) { alertText(error.message || '保存失败'); }
            finally { submitting = false; submitButtons(false); }
            return false;
        };

        function cleanup() {
            // 正在提交时由后端持有临时文件，不能让关闭请求与发布/回滚竞争。
            if (cleaned || submitting || flow.isBusy()) return;
            cleaned = true;
            flow.pathsToClean().forEach(function (path) {
                var url = root.actionUrl('admin/siargo/dms/file/deleteTempFile?filePath=' + encodeURIComponent(path));
                if (root.navigator.sendBeacon) root.navigator.sendBeacon(url);
                else $.ajax({url: url, type: 'POST', async: false});
            });
        }
        form.data('dmsCleanup', cleanup);
        $(root).on('beforeunload.dmsFileForm', cleanup);
        form.closest('.jbolt_page').attr('data-close-handler', 'SiargoDmsFileForm.close');
        try {
            var index = root.parent.layer.getFrameIndex(root.name);
            root.parent.$('#layui-layer' + index).find('.layui-layer-close, .layui-layer-btn1')
                .on('mousedown.dmsFileForm', cleanup);
        } catch (ignored) { /* 非 iframe 场景由页面关闭处理器负责清理。 */ }
    }
    root.SiargoDmsFileForm = {
        init: init, createFlow: createFlow,
        close: function () {
            root.jQuery('form[data-dms-form]').each(function () {
                var cleanup = root.jQuery(this).data('dmsCleanup'); if (cleanup) cleanup();
            });
            root.jQuery(root).off('beforeunload.dmsFileForm');
        }
    };
}(window));

/* 产品型号列表：按真实段位区分固定编码与参数，保留原文及全部选型规则。 */
(function (root) {
    'use strict';
    function escapeHtml(value) {
        return value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
    }
    function validParts(text, parts) {
        return Array.isArray(parts) && parts.length > 0 && parts.every(function (part) {
            return part && (part.kind === 'lit' || part.kind === 'param') && typeof part.text === 'string';
        }) && parts.map(function (part) { return part.text; }).join('') === text;
    }
    function expression(text, parts) {
        // 只采用与原描述完全一致的服务端片段，旧接口或异常片段退回原文。
        if (!validParts(text, parts)) return escapeHtml(text);
        return parts.map(function (part) {
            return '<span class="pm-desc-token-' + (part.kind === 'lit' ? 'literal' : 'param') + '">'
                + escapeHtml(part.text) + '</span>';
        }).join('');
    }
    function render(description, branches, series, parts) {
        var source = description == null ? '' : String(description);
        var exact = Array.isArray(branches) && branches.length > 0
            && branches.every(function (branch) { return typeof branch === 'string'; });
        if (!exact && !source.trim()) return '<span class="pm-desc-empty">—</span>';
        // 旧摘要中的“ / ”也可能属于型号固定文字；服务端数组只表示当前记录的选型规则。
        var descriptions = exact ? branches : [source];
        var hasParts = exact && Array.isArray(parts) && parts.length === descriptions.length;
        var expressions = descriptions.map(function (text, index) { return expression(text, hasParts ? parts[index] : null); });
        var fixedOnly = hasParts && parts.every(function (branch, index) {
            return validParts(descriptions[index], branch) && branch.every(function (part) { return part.kind === 'lit'; });
        });
        var countLabel = fixedOnly ? (descriptions.length === 1 ? '固定型号' : descriptions.length + ' 个固定型号') : (descriptions.length === 1 ? '选型规则' : descriptions.length + ' 套选型规则');
        var seriesName = series == null ? '' : String(series);
        var title = (seriesName ? seriesName + ' · ' : '') + '完整型号描述';
        var content = '<div class="pm-desc-dialog">';
        if (exact) {
            content += '<p class="pm-desc-dialog-count">' + (fixedOnly || descriptions.length === 1 ? countLabel : '共 ' + countLabel) + '</p><ol class="pm-desc-branches">';
            expressions.forEach(function (branch) {
                content += '<li class="pm-desc-branch"><div class="pm-desc-expression">' + branch + '</div></li>';
            });
            content += '</ol>';
        } else {
            content += '<div class="pm-desc-expression">' + escapeHtml(source) + '</div>';
        }
        content += '</div>';
        return '<div class="pm-desc-list"><div class="pm-desc-summary">' + expressions[0] + '</div>'
            // 内容先转义动态文字，再转义属性；Layer 读取属性后仍只收到安全 HTML。
            + '<button type="button" class="btn btn-link btn-xs pm-desc-open" data-dialogbtn data-area="94%,94%"'
            + ' data-success-handler="SiargoProdModelDialogs.fit" data-btn="no" data-title="' + escapeHtml(escapeHtml(title)) + '"'
            + ' data-content="' + escapeHtml(content) + '" aria-label="' + escapeHtml('查看' + (seriesName ? ' ' + seriesName : '') + '的完整型号描述')
            + '">' + (exact ? '<span class="pm-desc-count">' + countLabel + '</span>' : '')
            + '<span class="pm-desc-open-label">查看 <i class="fa fa-angle-right" aria-hidden="true"></i></span></button></div>';
    }
    root.SiargoProdModelDescription = {render: render};
    if (root.juicer) root.juicer.register('siargo_model_desc', render);
}(window));

/* JBolt 原生自动合并列的兼容入口：当前资源缺少 tableMergeCells，保持平台相邻文本合并与隐藏占位格规则。 */
(function(root){
    "use strict";
    if(typeof root.tableMergeCells==="function"){return;}
    root.tableMergeCells=function(table,columns){
        var $=root.jQuery;
        (Array.isArray(columns)?columns:[columns]).forEach(function(column){
            column=Number(column);
            if(!Number.isInteger(column)||column<1){return;}
            var first=null,span=1;
            // 列号从 1 开始，包含原生 prepend 列；重新计算前清除旧状态，固定列由 JBolt 随后克隆。
            $(table).find("tbody>tr>td:nth-child("+column+")").removeAttr("rowspan").show().each(function(){
                var cell=$(this),text=cell.text();
                if(first&&text!==""&&first.text()===text){
                    span++;cell.hide();first.attr("rowspan",span);
                }else{first=cell;span=1;}
            });
        });
    };
}(window));

/* 产品系列：列表编辑入口与弹窗关闭清理。 */
(function(root){
    "use strict";
    root.SiargoProdModel={
        close:function(container){
            var page=root.jQuery(container);
            var matArea=page.find(".pm-inputer-materials");
            if(matArea.length){root.SiargoProdModelMaterials.close(matArea[0]);}
            page.off();page.find("*").off();page.removeData("prodModelInitialized");
        },
        edit:function(btn){
            var id=root.jboltTableGetCheckedId(btn);
            if(id===false||id===null||id===undefined){return;}
            root.DialogUtil.openNewDialog({ele:root.jQuery(btn),url:"admin/siargo/prodmodel/edit/"+encodeURIComponent(id),title:"编辑产品型号",width:"94%",height:"94%",handler:"refreshJBoltTable",successHandler:"SiargoProdModelDialogs.fit"});
        }
    };
}(window));

/* 产品系列弹窗：百分比 area 配合模块尺寸上限，窗口变化沿用 Layer 原生 offset/auto。 */
(function(root){
    "use strict";
    root.SiargoProdModelDialogs={fit:function(){
        var $=root.jQuery,matched=false;
        $(".layui-layer").each(function(){
            var dialog=$(this),url=dialog.find("iframe").first().attr("src")||"";
            var route=url.match(/(?:^|\/)admin\/siargo\/prodmodel\/(add|edit|materials|selection)(?:[\/?#-]|$)/);
            if(!route&&!dialog.find(".pm-desc-dialog").length){return;}
            dialog.addClass("pm-responsive-dialog").toggleClass("pm-responsive-dialog-materials",!!route&&route[1]==="materials")
                .toggleClass("pm-responsive-dialog-selection",!!route&&route[1]==="selection");
            matched=true;
        });
        // 限制尺寸后立即让原生处理重新居中，并同步 iframe 或 content 的可用高度。
        if(matched){$(root).triggerHandler("resize");}
    }};
}(window));

/* 产品选型目录：同系列分支整合为 PDF 式折线图，条件关联仍保留原始范围。 */
(function (root) {
    'use strict';
    function text(value) { return value == null ? '' : String(value); }
    function list(value) { return Array.isArray(value) ? value : []; }
    function unique(values, key) {
        var seen = Object.create(null);
        return values.filter(function (value) {
            var identity = key(value);
            if (seen[identity]) return false;
            seen[identity] = true; return true;
        });
    }
    function rules(selection) {
        return JSON.stringify([selection.optional === true, selection.selection || '', selection.prefix || '',
            selection.minSelect, selection.maxSelect, selection.groups || null, selection.allowedCombinations || null]);
    }
    function signature(selection) {
        return JSON.stringify([selection.typeId, selection.typeName, list(selection.values), rules(selection)]);
    }
    function shape(variant) {
        // 不同结构不能合并；已有 formatKey 还保留了流量单位等业务归组规则。
        return variant.formatKey || JSON.stringify(list(variant.tokens).map(function (token) {
            return token.kind === 'lit' ? ['lit', token.text] : ['param', token.typeId, token.typeName];
        }));
    }
    function buildDiagrams(data) {
        var diagrams = [];
        list(data.branches).forEach(function (branch) {
            var groups = [], byShape = Object.create(null);
            list(branch.variants).forEach(function (variant) {
                var key = shape(variant);
                if (!byShape[key]) { byShape[key] = []; groups.push(byShape[key]); }
                byShape[key].push(variant);
            });
            if (!groups.length) diagrams.push({label: text(branch.branchLabel), tokens: [], selections: [],
                sources: [], conditions: [], message: text(branch.message) || '暂无产品选型数据'});
            groups.forEach(function (sources, groupIndex) {
                var first = sources[0], varying = [], selections = [];
                list(first.selections).forEach(function (selection, index) {
                    var originals = sources.map(function (source) { return list(source.selections)[index] || {}; });
                    var different = unique(originals, signature);
                    if (different.length > 1) varying.push(index);
                    var names = unique(originals.map(function (item) { return text(item.typeName); }), text);
                    var name = names.length === 2 && names.indexOf('最大流量') >= 0 && names.indexOf('最大体积流量') >= 0
                        ? '最大流量' : names.join(' / ');
                    selections.push({position: selection.position, typeName: name,
                        values: unique([].concat.apply([], originals.map(function (item) { return list(item.values); })),
                            function (value) { return JSON.stringify([value.value, value.paramValue, value.description]); }),
                        originals: different, rulesVary: unique(originals, rules).length > 1});
                });
                // 两个及以上变化段位存在配对关系；规则不同也必须逐项保留，不能暗示任意组合。
                var needsConditions = varying.length > 1 || selections.some(function (selection) { return selection.rulesVary; });
                var conditions = needsConditions ? sources.map(function (source) {
                    return {name: text(source.name), selections: varying.map(function (index) { return source.selections[index]; })};
                }) : [];
                var label = text(branch.branchLabel);
                if (groups.length > 1) label += (label ? ' · ' : '') + (text(first.name) || '选型 ' + (groupIndex + 1));
                diagrams.push({label: label, tokens: list(first.tokens), selections: selections,
                    sources: sources, conditions: conditions, message: text(branch.message)});
            });
        });
        return diagrams;
    }
    function code(value) { return text(value) === '' ? '空白' : text(value); }
    function optionText(value) {
        var valueCode = code(value.value), description = text(value.description), dictionaryValue = text(value.paramValue);
        if (dictionaryValue && dictionaryValue !== text(value.value) && description.indexOf(dictionaryValue) < 0) {
            description = dictionaryValue + (description ? '，' + description : '');
        }
        return valueCode + (description && description !== text(value.value) ? '－' + description : '');
    }
    function ruleText(selection) {
        var notes = [], combinations = selection.allowedCombinations;
        if (Array.isArray(combinations) && combinations.length) {
            notes.push('允许组合：' + combinations.map(function (combination) { return code(list(combination).join('')); }).join('、'));
        } else if (selection.selection === 'multiple') notes.push('可多选');
        if (selection.minSelect != null || selection.maxSelect != null) {
            var min = selection.minSelect == null ? (selection.optional ? 0 : 1) : selection.minSelect;
            var max = selection.maxSelect;
            if (max != null) notes.push('选 ' + min + '～' + max + ' 项');
            else notes.push('至少选 ' + min + ' 项');
        }
        if (selection.optional) notes.push('可留空');
        list(selection.groups).forEach(function (group) {
            notes.push(list(group.values).map(code).join('、') + ' 最多选 ' + (group.max == null ? 1 : group.max) + ' 项');
        });
        if (selection.prefix && selection.optional) notes.push('非空时前缀为 ' + selection.prefix);
        return notes.join('；');
    }
    function selectionText(selection) {
        return text(selection.typeName) + '（' + (list(selection.values).length
            ? list(selection.values).map(optionText).join('；') : '待补充参数值') + '）';
    }
    function element(tag, className, value) {
        var node = root.document.createElement(tag);
        if (className) node.className = className;
        if (value != null) node.textContent = value;
        return node;
    }
    function appendText(parent, className, value) {
        if (value) parent.appendChild(element('span', className, value));
    }
    function makeDiagram(diagram) {
        var section = element('section', 'pm-catalog-selection-branch');
        var figure = element('div', 'pm-catalog-selection-figure');
        var model = element('div', 'pm-catalog-selection-model');
        var label = element('span', 'pm-catalog-selection-label', diagram.label ? diagram.label + '：' : '');
        model.appendChild(label);
        var expression = element('div', 'pm-catalog-selection-expression');
        list(diagram.tokens).forEach(function (token) {
            if (token.kind === 'lit') expression.appendChild(element('span', 'pm-catalog-selection-literal', text(token.text)));
            else {
                var box = element('span', 'pm-catalog-selection-box');
                box.setAttribute('data-position', text(token.position));
                box.setAttribute('role', 'img'); box.setAttribute('aria-label', text(token.typeName) + '参数位');
                expression.appendChild(box);
            }
        });
        model.appendChild(expression); figure.appendChild(model);
        var descriptions = element('div', 'pm-catalog-selection-descriptions');
        diagram.selections.slice().reverse().forEach(function (selection) {
            var row = element('div', 'pm-catalog-selection-description');
            row.setAttribute('data-position', text(selection.position));
            row.appendChild(element('span', 'pm-catalog-selection-description-text', selectionText(selection)));
            if (!selection.rulesVary) appendText(row, 'pm-catalog-selection-rule', ruleText(selection.originals[0] || {}));
            descriptions.appendChild(row);
        });
        figure.appendChild(descriptions);
        var svg = root.document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('class', 'pm-catalog-selection-lines'); svg.setAttribute('aria-hidden', 'true');
        figure.appendChild(svg); section.appendChild(figure);
        if (diagram.message) section.appendChild(element('p', 'pm-catalog-selection-note', diagram.message));
        if (diagram.conditions.length) {
            var notes = element('div', 'pm-catalog-selection-conditions');
            notes.appendChild(element('p', 'pm-catalog-selection-condition-title', '以下参数按所列条件搭配：'));
            diagram.conditions.forEach(function (condition, index) {
                var value = (index + 1) + '. ' + condition.selections.map(function (selection) {
                    var rule = ruleText(selection);
                    return selectionText(selection) + (rule ? '，' + rule : '');
                }).join('；');
                notes.appendChild(element('p', 'pm-catalog-selection-condition', value));
            });
            section.appendChild(notes);
        }
        return section;
    }
    function layout(page) {
        page.querySelectorAll('.pm-catalog-selection-figure').forEach(function (figure) {
            var expression = figure.querySelector('.pm-catalog-selection-expression');
            var model = figure.querySelector('.pm-catalog-selection-model');
            var descriptions = figure.querySelector('.pm-catalog-selection-descriptions');
            var svg = figure.querySelector('.pm-catalog-selection-lines');
            var base = figure.getBoundingClientRect(), expressionRect = expression.getBoundingClientRect();
            var lineEnd = Math.max(320, Math.ceil(expressionRect.right - base.left) + 24);
            descriptions.style.marginLeft = lineEnd + 'px';
            // 每行高度由原文自动撑开；整张图固定最小宽度，窄窗口通过原生横向滚动浏览。
            figure.style.minWidth = (lineEnd + 370) + 'px';
            descriptions.style.paddingTop = '4px';
            var height = Math.max(model.offsetHeight, model.offsetHeight + descriptions.offsetHeight);
            svg.setAttribute('width', figure.scrollWidth); svg.setAttribute('height', height);
            svg.replaceChildren();
            base = figure.getBoundingClientRect();
            descriptions.querySelectorAll('.pm-catalog-selection-description').forEach(function (description) {
                var position = description.getAttribute('data-position');
                var box = Array.prototype.find.call(expression.querySelectorAll('.pm-catalog-selection-box'), function (item) {
                    return item.getAttribute('data-position') === position;
                });
                if (!box) return;
                var boxRect = box.getBoundingClientRect(), textRect = description.getBoundingClientRect();
                var x = boxRect.left - base.left + boxRect.width / 2;
                var y = textRect.top - base.top + 10;
                var path = root.document.createElementNS('http://www.w3.org/2000/svg', 'path');
                path.setAttribute('d', 'M ' + x + ' ' + (boxRect.bottom - base.top) + ' V ' + y + ' H ' + (lineEnd - 6));
                svg.appendChild(path);
            });
        });
    }
    function pageNode(container) {
        var node = container && container.jquery ? container[0] : container;
        if (typeof node === 'string') node = root.document.querySelector(node);
        if (node && node.matches && node.matches('.pm-catalog-selection-page')) return node;
        return (node && node.querySelector ? node : root.document).querySelector('.pm-catalog-selection-page');
    }
    function close(container) {
        var page = pageNode(container);
        if (!page || !page._pmCatalogSelection) return;
        var state = page._pmCatalogSelection;
        root.removeEventListener('resize', state.redraw);
        if (state.observer) state.observer.disconnect();
        if (state.frame) root.cancelAnimationFrame(state.frame);
        delete page._pmCatalogSelection;
    }
    function init(container) {
        var page = pageNode(container);
        if (!page) return;
        close(page);
        var content = page.querySelector('.pm-catalog-selection-content'), data;
        try { data = JSON.parse(page.querySelector('.pm-catalog-selection-data').textContent); }
        catch (error) { content.textContent = '产品选型数据读取失败，请刷新重试。'; return; }
        content.replaceChildren();
        var diagrams = buildDiagrams(data);
        if (!diagrams.length) content.appendChild(element('p', 'pm-catalog-selection-note', '暂无产品选型数据'));
        diagrams.forEach(function (diagram) { content.appendChild(makeDiagram(diagram)); });
        var state = {frame: 0}; page._pmCatalogSelection = state;
        state.redraw = function () {
            if (state.frame) root.cancelAnimationFrame(state.frame);
            state.frame = root.requestAnimationFrame(function () { state.frame = 0; if (page.isConnected) layout(page); });
        };
        root.addEventListener('resize', state.redraw);
        if (root.ResizeObserver) { state.observer = new root.ResizeObserver(state.redraw); state.observer.observe(page); }
        if (root.document.fonts && root.document.fonts.ready) root.document.fonts.ready.then(function () {
            if (page._pmCatalogSelection === state) state.redraw();
        });
        state.redraw();
    }
    root.SiargoProdModelSelection = {init: init, close: close, buildDiagrams: buildDiagrams};
}(window));

/* 产品选型 v2：以 tokens 数组保存，按实际产品分支维护字典段位。 */
(function(root){
    "use strict";
    function pmv2Clone(value){return JSON.parse(JSON.stringify(value));}
    function pmv2Esc(value){return String(value==null?"":value).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;").replace(/'/g,"&#39;");}
    function pmv2DictionaryTokens(tokens){
        var result=[];
        (tokens||[]).forEach(function(original){
            if(typeof original==="string"){result.push(original);return;}
            if(!original||Array.isArray(original)||typeof original!=="object"){throw new Error("参数段位结构异常");}
            if(original.t==null){
                if(original.input==="text"||original.input==="decimal"){return;}
                throw new Error("参数必须选择种类管理中的种类");
            }
            var token=pmv2Clone(original);
            ["input","allowCustom","customInput","maxLength","min","max","freeText","descriptions"].forEach(function(name){delete token[name];});
            result.push(token);
        });
        return result;
    }
    function pmv2Compact(tokens){
        var result=[];
        pmv2DictionaryTokens(tokens).forEach(function(token){
            if(typeof token==="string"){
                if(typeof result[result.length-1]==="string"){result[result.length-1]+=token;}
                else if(token){result.push(token);}
            }else{result.push(pmv2Clone(token));}
        });
        return result;
    }
    function pmv2Expand(tokens){
        var result=[];
        pmv2DictionaryTokens(tokens).forEach(function(token){
            if(typeof token==="string"){token.split("").forEach(function(character){result.push(character);});}
            else{result.push(pmv2Clone(token));}
        });
        return result;
    }
    function pmv2SlotTitle(token,catalog){
        var type=token.t==null?null:(catalog||{})[String(token.t)];
        return String(type&&type.typeName||"待配置种类");
    }
    function pmv2SyncTypeNames(branches,catalog){
        (branches||[]).forEach(function(branch){branch.tokens.forEach(function(token){
            var type=typeof token==="string"||token.t==null?null:(catalog||{})[String(token.t)];
            if(type&&type.typeName){token.label=String(type.typeName);}
        });});
    }
    function pmv2BranchTitle(tokens,catalog,slots){
        var text="",html="",position=0;
        function literal(value){text+=value;html+="<span class='pm-v2-title-literal'>"+pmv2Esc(value)+"</span>";}
        pmv2Compact(tokens).forEach(function(token){
            if(typeof token==="string"){literal(token);return;}
            position++;
            var title=slots&&slots[position-1]?slots[position-1].title:pmv2SlotTitle(token,catalog);
            if(token.prefix){literal(String(token.prefix));}
            text+=title;html+="<span class='pm-v2-title-param'>"+pmv2Esc(title)+"</span>";
        });
        return {text:text||"待配置型号",html:html||"<span class='pm-v2-title-empty'>待配置型号</span>"};
    }
    function pmv2Read(editData){
        editData=editData||{};
        var descriptor=editData.modelDesc,segments=editData.segments||[],byType={};
        segments.forEach(function(segment){byType[String(segment.paramTypeId)]=segment;});
        try{
            if(typeof descriptor==="string"&&descriptor.trim()){descriptor=JSON.parse(descriptor);}
            var groups;
            if(descriptor&&descriptor.version===2&&Array.isArray(descriptor.variants)){
                groups=descriptor.variants.map(function(variant){return variant.tokens;});
            }else if(Array.isArray(descriptor)&&descriptor.length){
                groups=descriptor.every(Array.isArray)?descriptor:[descriptor];
            }else if(!descriptor||Array.isArray(descriptor)){
                var fallback=[editData.series||""];
                segments.forEach(function(segment){fallback.push("-",{t:String(segment.paramTypeId)});});
                groups=[fallback];
            }else{throw new Error("无法识别型号参数结构");}
            if(!groups.length){throw new Error("型号参数结构不能为空");}
            var restored={},schema={variants:groups.map(function(tokens,index){
                if(!Array.isArray(tokens)){throw new Error("选型参数结构异常");}
                var keys={},position=0;
                return {key:"branch_"+index,tokens:pmv2DictionaryTokens(tokens).map(function(original){
                    if(typeof original==="string"){return original;}
                    if(!original||Array.isArray(original)||typeof original!=="object"){throw new Error("参数段位结构异常");}
                    var token=pmv2Clone(original);position++;
                    token.key=token.key||"slot_"+position;
                    if(keys[token.key]){throw new Error("参数位置标识重复");}keys[token.key]=true;
                    if(token.t!=null){
                        token.t=String(token.t);var segment=byType[token.t];
                        if(!segment){throw new Error("参数类型未加载，请重新打开型号编辑页");}
                        restored[token.t]=true;
                        token.valueIds=(token.valueIds||segment.valueIds||[]).map(String);
                        token.label=segment.typeName||"待配置种类";
                    }
                    return token;
                })};
            })};
            segments.forEach(function(segment){if(!restored[String(segment.paramTypeId)]){throw new Error("型号结构与参数关联不一致，请核对资料");}});
            return {schema:schema,error:""};
        }catch(error){return {schema:null,error:error.message||"产品型号数据加载异常，请重新打开编辑页"};}
    }
    function pmv2Pack(schema){
        var groups=schema.variants.map(function(variant){return pmv2Compact(variant.tokens);});
        return groups.length===1?groups[0]:groups;
    }
    function pmv2Segments(schema){
        var result=[],byType={};
        schema.variants.forEach(function(variant){variant.tokens.forEach(function(token){
            if(typeof token==="string"||token.t==null){return;}
            var typeId=String(token.t),segment=byType[typeId];
            if(!segment){segment={paramTypeId:typeId,valueIds:[]};byType[typeId]=segment;result.push(segment);}
            (token.valueIds||[]).forEach(function(valueId){valueId=String(valueId);if(segment.valueIds.indexOf(valueId)<0){segment.valueIds.push(valueId);}});
        });});
        return result;
    }
    function pmv2ValueMap(catalog,typeId){
        var map={},type=catalog[String(typeId)];
        ((type&&type.values)||[]).forEach(function(value){map[String(value.id)]=value;});
        return map;
    }
    function pmv2Code(value,token){
        var codes=token&&token.codes,id=String(value.id);
        if(codes&&Object.prototype.hasOwnProperty.call(codes,id)){return String(codes[id]);}
        return String(value.paramValue==null?(value.value==null?"":value.value):value.paramValue);
    }
    function pmv2Describe(value){
        // 参数说明以种类管理中的当前描述为准。
        return String(value.description||"");
    }
    function pmv2ValueDetails(token,value){
        var code=pmv2Code(value,token),dictionaryCode=pmv2Code(value),description=pmv2Describe(value);
        if(code===dictionaryCode){return {code:code,dictionaryCode:dictionaryCode,description:description,title:description};}
        return {code:code,dictionaryCode:dictionaryCode,description:description,title:dictionaryCode+(description?" "+description:"")};
    }
    function pmv2ParameterOption(value){
        return {parameter:pmv2Code(value),remark:pmv2Describe(value)};
    }
    function pmv2Branches(schema){return schema.variants.map(function(variant){var tokens=pmv2Expand(variant.tokens);return {key:variant.key,tokens:tokens,cursor:tokens.length,focus:false};});}
    function pmv2CommitBranches(schema,branches){
        schema.variants.forEach(function(variant){var branch=branches.filter(function(item){return item.key===variant.key;})[0];if(!branch){throw new Error("选型规则数据缺失，请重新打开编辑页");}variant.tokens=pmv2Compact(branch.tokens);});
        return schema;
    }
    function pmv2DictionarySlot(old,key,typeId,ids,typeName,dictionary){
        var token=old?pmv2Clone(old):{key:key},sameType=old&&String(old.t)===String(typeId);
        if(!typeId||!ids.length){throw new Error("请选择参数类型及至少一个参数值");}
        ids=ids.map(String);
        // 字典值删除后保留待配置段位，重新补选时移除已经失效的组合约束。
        if(sameType&&(!(old.valueIds||[]).length||Array.isArray(old.allowedCombinations)&&!old.allowedCombinations.length)){
            ["selection","minSelect","maxSelect","groups","allowedCombinations"].forEach(function(name){delete token[name];});
        }
        if(!sameType){
            ["selection","minSelect","maxSelect","groups","allowedCombinations","codes","optional"].forEach(function(name){delete token[name];});
        }else if((old.valueIds||[]).length!==ids.length||(old.valueIds||[]).some(function(id){return ids.indexOf(String(id))<0;})){
            // 用户调整可选值时，编码及分组只能继续引用仍然选中的值。
            if(token.codes){Object.keys(token.codes).forEach(function(id){if(ids.indexOf(id)<0){delete token.codes[id];}});}
            var codes;
            if(token.allowedCombinations!=null||token.groups&&token.groups.length){
                codes=ids.map(function(id){if(!dictionary||!dictionary[id]){throw new Error("参数字典未加载，请重新选择参数类型");}return pmv2Code(dictionary[id],token);});
            }
            if(token.allowedCombinations!=null){
                if(!Array.isArray(token.allowedCombinations)){throw new Error("允许组合数据异常，请重新打开型号编辑页");}
                // 组合整体保留或移除；CV 不能因取消 C 被改写成 V。
                token.allowedCombinations=token.allowedCombinations.filter(function(combination){return combination.every(function(code){return codes.indexOf(code)>=0;});});
                if(!token.allowedCombinations.length){throw new Error("当前选择会移除全部允许组合，请保留至少一个完整组合");}
                var uncovered=codes.filter(function(code){return !token.allowedCombinations.some(function(combination){return combination.indexOf(code)>=0;});});
                if(uncovered.length){throw new Error("参数值 "+uncovered.join("、")+" 不在保留的允许组合中，请取消勾选");}
                var lengths=token.allowedCombinations.map(function(combination){return combination.length;});
                token.minSelect=Math.min.apply(Math,lengths);token.maxSelect=Math.max.apply(Math,lengths);
            }
            if(token.groups&&token.groups.length){
                token.groups=token.groups.filter(function(group){
                    group.values=(group.values||[]).filter(function(code,index,values){return codes.indexOf(code)>=0&&values.indexOf(code)===index;});
                    return group.values.length>0;
                });
            }
            if(token.allowedCombinations==null){
                var maximum=token.selection==="multiple"?ids.length:1;
                if(token.maxSelect!=null){token.maxSelect=Math.max(1,Math.min(token.maxSelect,maximum));maximum=token.maxSelect;}
                if(token.minSelect!=null){token.minSelect=Math.max(0,Math.min(token.minSelect,maximum));}
            }
        }
        ["input","allowCustom","customInput","maxLength","min","max","freeText","descriptions"].forEach(function(name){delete token[name];});
        token.t=String(typeId);token.label=typeName;token.valueIds=ids;
        return token;
    }
    function pmv2AvailableValues(available){
        var result=[],seen={};
        (available||[]).forEach(function(value){var id=String(value.id);if(!seen[id]){seen[id]=true;var copy=pmv2Clone(value);copy.id=id;result.push(copy);}});
        return result;
    }
    function pmv2FormatKey(tokens,types){
        return JSON.stringify(pmv2Compact(tokens).map(function(token){
            if(typeof token==="string"){return ["literal",token];}
            var typeId=token.t==null?null:String(token.t),typeName=(types||{})[typeId];
            var parameter=token.key==="maxFlow"&&(typeName==="最大流量"||typeName==="最大体积流量")?"role:maxFlow":typeId;
            return ["slot",parameter,token.prefix||""];
        }));
    }
    function pmv2SlotAtPosition(branch,position){
        var current=0,result=null;
        pmv2Expand(branch.tokens).some(function(token,index){
            if(typeof token!=="string"&&current++===position){result={token:token,index:index};return true;}
            return false;
        });
        return result;
    }
    function pmv2SourceCondition(branch,group,catalog){
        var parts=[];
        group.slots.forEach(function(slot){
            var sources=group.members.map(function(member){return pmv2SlotAtPosition(member,slot.position).token;});
            function signature(token){var copy=pmv2Clone(token);delete copy.key;if(copy.t!=null){delete copy.label;}return JSON.stringify(copy);}
            var first=signature(sources[0]);
            if(sources.every(function(token){return signature(token)===first;})){return;}
            var token=pmv2SlotAtPosition(branch,slot.position).token,map=pmv2ValueMap(catalog,token.t),ids=token.valueIds||[];
            var values=ids.slice(0,3).map(function(id){var value=map[String(id)]||{id:id,paramValue:"[缺失值]"},code=pmv2Code(value),description=pmv2Describe(value);return (code===""?"空白":code)+(ids.length===1&&description&&description!==code?"（"+description+"）":"");});
            parts.push(pmv2SlotTitle(token,catalog)+"："+(values.join("、")||"待补充参数值")+(ids.length>3?"等 "+ids.length+" 项":""));
        });
        return parts.join("；")||"默认规格";
    }
    function pmv2FormatGroups(branches,catalog){
        catalog=catalog||{};
        var groups=[],byFormat={},formatTypes={};
        Object.keys(catalog).forEach(function(typeId){formatTypes[typeId]=catalog[typeId].typeName;});
        (branches||[]).forEach(function(branch){
            var signature=pmv2FormatKey(branch.tokens,formatTypes),group=byFormat[signature];
            if(!group){group={key:"format_"+branch.key,signature:signature,formatTypes:formatTypes,branchKeys:[],members:[],tokens:pmv2Expand(branch.tokens),slots:[]};byFormat[signature]=group;groups.push(group);}
            group.branchKeys.push(branch.key);group.members.push(branch);
        });
        groups.forEach(function(group){
            group.tokens.forEach(function(token,index){
                if(typeof token==="string"){return;}
                var position=group.slots.length,typeId=token.t==null?null:String(token.t),typeName=(catalog[typeId]||{}).typeName;
                var sources=group.members.map(function(branch){return pmv2SlotAtPosition(branch,position).token;}),typeIds=[];
                var names=sources.map(function(source){var id=source.t==null?null:String(source.t);if(typeIds.indexOf(id)<0){typeIds.push(id);}return pmv2SlotTitle(source,catalog);});
                var sameName=names.every(function(name){return name===names[0];}),mixedTypes=typeIds.length>1;
                var title=sameName?names[0]:(typeName||names[0]);
                if(mixedTypes&&(title==="最大流量"||title==="最大体积流量")){title="最大流量";}
                var slot={position:position,tokenIndex:index,typeId:typeId,typeIds:typeIds,mixedTypes:mixedTypes,title:title,valueIds:[],values:[]},seen={};
                group.members.forEach(function(branch){
                    var source=pmv2SlotAtPosition(branch,position).token,map=pmv2ValueMap(catalog,source.t);
                    (source.valueIds||[]).forEach(function(id){
                        id=String(id);if(slot.valueIds.indexOf(id)<0){slot.valueIds.push(id);}
                        var value=map[id]||{id:id,paramValue:"[缺失值]",description:"参数字典未加载"},details=pmv2ValueDetails(source,value),description=pmv2Describe(value),signature=JSON.stringify([source.t,id,details.code,description]);
                        var item=seen[signature];
                        if(!item){item={id:id,code:details.code,dictionaryCode:details.dictionaryCode,description:description,title:details.title,branchKeys:[]};seen[signature]=item;slot.values.push(item);}
                        item.branchKeys.push(branch.key);
                    });
                });
                // 此 token 只用于展示；来源的编码映射始终留在各自原分支。
                token.label=title;token.valueIds=slot.valueIds.slice();group.slots.push(slot);
            });
            group.sources=group.members.map(function(branch,index){return {key:branch.key,label:"规格 "+(index<9?"0":"")+(index+1)+" · "+pmv2SourceCondition(branch,group,catalog)};});
            delete group.members;
        });
        return groups;
    }
    function pmv2GroupTargets(branches,group,keys){
        keys=keys==null?group.branchKeys:keys;
        if(!Array.isArray(keys)||!keys.length){throw new Error("请选择适用规格条件");}
        var seen={},result=[];
        keys.forEach(function(key){
            if(seen[key]||group.branchKeys.indexOf(key)<0){throw new Error("适用规格条件已变化，请重新打开参数面板");}
            seen[key]=true;
            var branch=branches.filter(function(item){return item.key===key;})[0];
            if(!branch||pmv2FormatKey(branch.tokens,group.formatTypes)!==group.signature){throw new Error("型号格式已变化，请重新打开参数面板");}
            result.push(branch);
        });
        return result;
    }
    function pmv2ApplyGroupSlot(branches,group,position,patch,catalog){
        var adding=position==null,targets=pmv2GroupTargets(branches,group,adding?group.branchKeys:patch.branchKeys),typeId=String(patch.typeId||""),ids=[],baseline=[];
        if(!adding&&group.slots[position]&&group.slots[position].mixedTypes&&targets.length!==1){throw new Error("此段位包含不同单位的参数，请选择一个具体规格条件后编辑");}
        (patch.valueIds||[]).forEach(function(id){id=String(id);if(ids.indexOf(id)<0){ids.push(id);}});
        if(!typeId||!ids.length){throw new Error("请选择参数类型及至少一个参数值");}
        if(!catalog||!catalog[typeId]||!catalog[typeId].typeName){throw new Error("请选择种类管理中已有的种类");}
        var dictionary=pmv2ValueMap(catalog||{},typeId);
        ids.forEach(function(id){if(!dictionary[id]){throw new Error("参数字典未加载，请重新选择参数类型");}});
        targets.forEach(function(branch){
            if(adding){return;}
            var source=pmv2SlotAtPosition(branch,position);
            if(!source){throw new Error("参数位置已变化，请重新打开参数面板");}
            if(String(source.token.t)===typeId){(source.token.valueIds||[]).forEach(function(id){id=String(id);if(baseline.indexOf(id)<0){baseline.push(id);}});}
        });
        var added=ids.filter(function(id){return baseline.indexOf(id)<0;}),removed=baseline.filter(function(id){return ids.indexOf(id)<0;}),targetKeys=targets.map(function(branch){return branch.key;}),result=pmv2Clone(branches);
        result.forEach(function(branch){
            if(targetKeys.indexOf(branch.key)<0){return;}
            branch.tokens=pmv2Expand(branch.tokens);
            var source=adding?null:pmv2SlotAtPosition(branch,position),old=source&&source.token,nextIds=ids;
            // 待配置空槽采用本次完整选择；非空槽只应用增减量，保留原有规格范围。
            if(old&&String(old.t)===typeId&&(old.valueIds||[]).length){nextIds=old.valueIds.map(String).filter(function(id){return removed.indexOf(id)<0;});added.forEach(function(id){if(nextIds.indexOf(id)<0){nextIds.push(id);}});}
            var keyNumber=branch.tokens.length+1,newKey="slot_"+keyNumber;
            while(branch.tokens.some(function(token){return typeof token!=="string"&&token.key===newKey;})){newKey="slot_"+(++keyNumber);}
            var token;
            try{token=pmv2DictionarySlot(old,newKey,typeId,nextIds,catalog[typeId].typeName,dictionary);}catch(error){
                var label=(group.sources||[]).filter(function(item){return item.key===branch.key;})[0];
                throw new Error((label?label.label+"：":"")+error.message);
            }
            if(source){branch.tokens[source.index]=token;}
            else{
                var index=Number(patch.tokenIndex);
                if(!Number.isInteger(index)||index<0||index>branch.tokens.length){throw new Error("添加位置已变化，请重新打开参数面板");}
                branch.tokens.splice(index,0,token);branch.cursor=index+1;
            }
        });
        return result;
    }
    function pmv2EditGroupStructure(branches,group,change){
        var targets=pmv2GroupTargets(branches,group),keys=targets.map(function(branch){return branch.key;}),index=Number(change.index),text=String(change.text||"");
        if(!Number.isInteger(index)||index<0){throw new Error("型号编辑位置无效");}
        if(change.kind==="insertText"&&!/^[A-Za-z0-9 ._\/+²³µμ-]+$/.test(text)){throw new Error("固定字符只支持字母、数字、普通空格和 - . _ / + ² ³ µ μ");}
        if(change.kind!=="insertText"&&change.kind!=="removeToken"){throw new Error("型号编辑操作无效");}
        var result=pmv2Clone(branches);
        result.forEach(function(branch){
            if(keys.indexOf(branch.key)<0){return;}
            branch.tokens=pmv2Expand(branch.tokens);
            if(index>branch.tokens.length||change.kind==="removeToken"&&index===branch.tokens.length){throw new Error("型号编辑位置已变化");}
            if(change.kind==="insertText"){var characters=text.split("");Array.prototype.splice.apply(branch.tokens,[index,0].concat(characters));branch.cursor=index+characters.length;}
            else{branch.tokens.splice(index,1);branch.cursor=Math.min(index,branch.tokens.length);}
        });
        return result;
    }
    root.SiargoProdModelSchema={read:pmv2Read,pack:pmv2Pack,segments:pmv2Segments,branches:pmv2Branches,commitBranches:pmv2CommitBranches,dictionarySlot:pmv2DictionarySlot,availableValues:pmv2AvailableValues,branchTitle:pmv2BranchTitle,formatGroups:pmv2FormatGroups,applyGroupSlot:pmv2ApplyGroupSlot,editGroupStructure:pmv2EditGroupStructure,parameterOption:pmv2ParameterOption,syncTypeNames:pmv2SyncTypeNames};
    function init(container){
        var $=root.jQuery,page=$(container);
        if(!page.length||page.data("prodModelInitialized")){return;}
        page.data("prodModelInitialized",true);
        var sid=page.attr("data-page-id"),editData={},loadError="",catalog={},types=[],panelValues=[],panel=null,panelRequest=0,typeRequest=0,disposed=false,submitting=false,select2Loading=false;
        var panelElement=$(),panelDialog=$(),panelIndex=null,panelSequence=0,eventNamespace=".pmParameter_"+sid;
        function el(name){var selector="#"+name+"_"+sid;return panelElement.is(selector)?panelElement:panelElement.find(selector).add(page.find(selector));}
        try{editData=JSON.parse(el("pmEditDataTxt").val()||"{}");}catch(ignored){loadError="产品型号数据加载异常，请重新打开编辑页";}
        (editData.segments||[]).forEach(function(segment){catalog[String(segment.paramTypeId)]={typeName:segment.typeName,values:pmv2Clone(segment.values||[])};});
        var parsed=pmv2Read(editData),state={schema:parsed.schema||{variants:[{key:"branch_0",tokens:[]}]}};
        loadError=loadError||parsed.error;
        state.branches=pmv2Branches(state.schema);
        function branchAt(key){return (state.groups||[]).filter(function(branch){return branch.key===key;})[0];}
        function branchOf(element){return branchAt($(element).closest("[data-v2-branch]").attr("data-v2-branch"));}
        function branchEl(branch){return el("pmBranches").children("[data-v2-branch]").filter(function(){return $(this).attr("data-v2-branch")===branch.key;});}
        function panelTargets(){
            if(!panel){return [];}
            var group=branchAt(panel.groupKey),key=panelField("scope").val();
            if(panel.position!=null&&group.slots[panel.position].mixedTypes&&(!key||key==="all")){throw new Error("请选择一个具体规格条件后编辑此参数");}
            return pmv2GroupTargets(state.branches,group,key&&key!=="all"?[key]:group.branchKeys);
        }
        function panelTokens(typeId){
            if(!panel||panel.position==null){return [];}
            return panelTargets().map(function(branch){return {branch:branch,token:pmv2SlotAtPosition(branch,panel.position).token};}).filter(function(item){return String(item.token.t)===String(typeId);});
        }
        function panelUnion(typeId){var ids=[];panelTokens(typeId).forEach(function(item){(item.token.valueIds||[]).forEach(function(id){id=String(id);if(ids.indexOf(id)<0){ids.push(id);}});});return ids;}

        function renderBranch(branch){
            branch.cursor=Math.max(0,Math.min(branch.tokens.length,branch.cursor));
            var stage="",cards="",position=0;
            for(var index=0;index<=branch.tokens.length;index++){
                if(index===branch.cursor){stage+="<span class='pm-cursor"+(branch.focus?" pm-cursor-on":"")+"'></span>";}
                if(index===branch.tokens.length){break;}
                var token=branch.tokens[index];
                if(typeof token==="string"){
                    stage+="<span class='"+(token===" "?"pm-ch-space":token==="-"?"pm-ch-dash":"pm-ch-char")+"' data-token-index='"+index+"'"+(token===" "?" title='空格'":"")+">"+pmv2Esc(token)+"</span>";
                }else{
                    var slot=branch.slots[position],accent="pm-seg-accent-"+(position%6),title=slot.title,key=String(position);
                    if(token.prefix){stage+="<span class='pm-v2-optional-prefix'>"+pmv2Esc(token.prefix)+"</span>";}
                    stage+="<button type='button' class='pm-preview-slot "+accent+"' data-token-index='"+index+"' data-v2-slot='"+key+"' title='"+pmv2Esc(title)+"'>"+(position+1)+"</button>";
                    var valueRows=[],seenValues={};
                    slot.values.forEach(function(value){if(!seenValues[value.id]){seenValues[value.id]=true;valueRows.push(value);}});
                    var values=valueRows.map(function(value){
                        var note=value.description;
                        return "<span class='pm-seg-value'><span>"+pmv2Esc(value.dictionaryCode===""?"空白":value.dictionaryCode)+"</span>"+(note?"<small class='pm-group-value-note'>("+pmv2Esc(note)+")</small>":"")+"</span>";
                    }).join("")||"<span class='pm-seg-value'>待补充参数值</span>";
                    cards+="<article class='pm-seg-card "+accent+"' data-v2-card='"+key+"'><div class='pm-seg-card-index'>"+(position+1)+"</div><div class='pm-seg-card-body'><h4 class='pm-seg-name'>"+pmv2Esc(title)+"</h4><div class='pm-seg-values'>"+values+"</div></div><div class='pm-seg-card-actions'><button type='button' class='btn btn-outline-primary btn-sm' data-v2-slot='"+key+"'>编辑</button><button type='button' class='btn btn-link btn-sm pm-seg-del' data-v2-delete='"+key+"' aria-label='删除"+pmv2Esc(title)+"'><i class='fa fa-times-circle'></i></button></div></article>";
                    position++;
                }
            }
            if(!branch.tokens.length){stage+="<span class='pm-input-placeholder'>点击输入固定字符，或添加参数</span>";}
            var block=branchEl(branch),heading=pmv2BranchTitle(branch.tokens,catalog,branch.slots);
            block.find(".pm-stage").html(stage).attr("aria-label",heading.text+" 型号输入区");
            block.find(".pm-preview-status").text(position+" 个参数段位"+(branch.branchKeys.length>1?" · "+branch.branchKeys.length+" 项规格条件":""));
            block.find(".pm-selection-cards").html(cards||"<div class='pm-empty-tip'>暂无参数段位</div>");
        }
        function render(){
            var prior={};
            (state.groups||[]).forEach(function(group){group.branchKeys.forEach(function(key){prior[key]={open:branchEl(group).find(".pm-selection-details").prop("open"),cursor:group.cursor,focus:group.focus};});});
            state.groups=pmv2FormatGroups(state.branches,catalog);
            state.groups.forEach(function(group){var old=prior[group.branchKeys[0]];group.cursor=old?old.cursor:group.tokens.length;group.focus=old?old.focus:false;group.open=old?old.open:false;});
            el("pmBranches").html(state.groups.map(function(branch,index){
                return "<section class='pm-v2-branch' data-v2-branch='"+pmv2Esc(branch.key)+"'>"
                    +"<h3 class='pm-format-heading'>"+(branch.slots.length?"选型规则"+(state.groups.length>1?" "+(index<9?"0":"")+(index+1):""):"固定型号")+"</h3>"
                    +"<div class='pm-selection-workspace'><div class='pm-model-preview'><div class='pm-model-preview-head'><p class='pm-model-preview-help'>点击输入固定字符，在光标处添加参数段位。结构修改适用于本选型规则的全部规格条件。</p><span class='pm-preview-status'></span></div><div class='pm-stage-wrap'><div class='pm-stage' tabindex='0' role='textbox' aria-label='型号输入区' title='支持字母、数字、普通空格和 - . _ / + ² ³ µ μ；可粘贴固定字符；左右键移动光标，Backspace / Delete 删除'></div></div></div>"
                    +"<details class='pm-selection-details'><summary class='pm-selection-cards-head'><span class='pm-selection-cards-title'>参数段位</span><span class='pm-selection-cards-toggle'><span class='pm-selection-expand'>展开</span><span class='pm-selection-collapse'>收起</span> <i class='fa fa-chevron-down' aria-hidden='true'></i></span></summary>"
                    +"<div class='pm-selection-cards-toolbar'><button type='button' class='btn btn-primary btn-sm' data-v2-add-slot><i class='fa fa-plus'></i> 添加参数</button></div><div class='pm-selection-cards'></div></details></div></section>";
            }).join(""));
            state.groups.forEach(function(group){renderBranch(group);branchEl(group).find(".pm-selection-details").prop("open",!!group.open);});
        }
        function setExpanded(section,expanded){
            var button=section.find(".pm-section-toggle").first(),body=section.children(".pm-section-body");
            var title=section.find(".pm-section-title").first().text()||section.find(".pm-eyebrow").first().text();
            button.attr("aria-expanded",String(expanded)).attr("aria-label",(expanded?"收起":"展开")+title);
            button.find("span").text(expanded?"收起":"展开");button.find("i").toggleClass("fa-chevron-up",expanded).toggleClass("fa-chevron-down",!expanded);
            body.prop("hidden",!expanded);section.toggleClass("pm-section-collapsed",!expanded);
            if(expanded){body.find("table[data-jbolttable]").each(function(){var table=$(this),instance=table.jboltTable?table.jboltTable("inst"):null;if(instance){instance.me.resize(instance);instance.me.processTableColWidthAfterResize(instance);}});}
        }
        function destroyPanelTypeSelect(){var select=panelField("type");if(select.data("select2")){select.select2("destroy");}}
        function discardPanel(context){
            if(!context||panel!==context){return;}
            panelRequest++;destroyPanelTypeSelect();panelElement.off(eventNamespace).remove();panel=null;panelValues=[];panelElement=$();panelDialog=$();panelIndex=null;
            if(!disposed&&context.trigger&&root.document.documentElement.contains(context.trigger)){$(context.trigger).trigger("focus");}
        }
        function closePanel(){var context=panel,index=panelIndex;discardPanel(context);if(index!==null){root.layer.close(index);}}
        function fitPanelDialog(){
            if(panelIndex===null){return;}
            var width=Math.max(280,Math.min(860,$(root).width()-32)),height=Math.max(260,Math.min(680,$(root).height()-32));
            root.layer.style(panelIndex,{width:width+"px",height:height+"px",left:Math.max(0,($(root).width()-width)/2)+"px",top:Math.max(0,($(root).height()-height)/2)+"px"});
            panelDialog.find(".layui-layer-content").height(Math.max(0,height-panelDialog.find(".layui-layer-title").outerHeight()));
        }
        function panelKeydown(event){
            if(panelIndex===null){return;}
            var ownZ=parseInt(panelDialog.css("z-index"),10)||0,covered=false;
            $(".layui-layer:visible").each(function(){if(this!==panelDialog[0]&&(parseInt($(this).css("z-index"),10)||0)>ownZ){covered=true;}});
            if(covered||panelDialog.find(".select2-container--open").length){return;}
            if(event.key==="Escape"){event.preventDefault();event.stopImmediatePropagation();closePanel();}
            else if(event.key==="Tab"){
                var focusable=panelDialog.find("button,input,select,a[href],[tabindex='0']").filter(":visible").not(":disabled"),first=focusable[0],last=focusable[focusable.length-1],active=root.document.activeElement;
                if(first&&(!panelDialog[0].contains(active)||(event.shiftKey&&active===first)||(!event.shiftKey&&active===last))){event.preventDefault();$(event.shiftKey?last:first).trigger("focus");}
            }
        }
        function panelField(name){return el("pmPanel").find("[data-v2-field='"+name+"']");}
        function initPanelTypeSelect(context){
            if(disposed||panel!==context){return;}
            var select=panelField("type");
            if(select.data("select2")){return;}
            if(!$.fn.select2){
                select.prop("disabled",true);
                if(select2Loading){return;}
                select2Loading=true;
                root.loadJBoltPlugin(["select2"],function(){
                    select2Loading=false;
                    if(disposed||!panel){return;}
                    if(!$.fn.select2){LayerMsgBox.alert("参数类型搜索组件加载失败，请重新打开编辑页",2);return;}
                    initPanelTypeSelect(panel);
                });
                return;
            }
            root.processGlobalSelect2();
            select.prop("disabled",false);
            // 候选值由当前编辑器加载，搜索下拉显式挂到所属弹窗。
            select.select2({theme:"bootstrap",allowClear:true,closeOnSelect:true,placeholder:"请选择参数类型",width:"100%",dropdownParent:panelDialog});
        }
        function refreshPanelTypeOptions(selected){
            var select=panelField("type");
            select.html(typeOptions(selected)).val(selected==null?"":String(selected));
            // 只刷新 Select2 显示，不能触发类型 change 而清掉面板中尚未保存的勾选。
            select.trigger("change.select2");
            return select.val()||"";
        }
        function selectedIds(){var ids=[];el("pmPanel").find("[data-v2-value]:checked").each(function(){ids.push(String($(this).val()));});return ids;}
        function typeOptions(selected){
            var list=types.slice(),html="<option value=''>请选择参数类型</option>";
            list.forEach(function(type){html+="<option value='"+pmv2Esc(type.id)+"'"+(String(selected)===String(type.id)?" selected":"")+">"+pmv2Esc(type.typeName)+"</option>";});
            return html;
        }
        function refreshTypes(done){
            var request=++typeRequest;
            Ajax.get("admin/siargo/prodparam/options",function(ret){
                if(disposed||request!==typeRequest){return;}
                types=ret.data||[];
                Object.keys(catalog).forEach(function(typeId){if(!types.some(function(type){return String(type.id)===typeId;})){delete catalog[typeId];}});
                types.forEach(function(type){var typeId=String(type.id);catalog[typeId]=catalog[typeId]||{values:[]};catalog[typeId].typeName=type.typeName;});
                pmv2SyncTypeNames(state.branches,catalog);render();
                if(done){done();}
                updatePanelTitle();
            });
        }
        function updatePanelTitle(){
            if(!panel){return;}
            var title=panel.position==null?"添加参数":"编辑参数 · "+(panelField("type").find("option:selected").text()||branchAt(panel.groupKey).slots[panel.position].title);
            panelDialog.attr("aria-label",title).find(".layui-layer-title").text(title);
        }
        function filterPanelValues(){
            if(!panel){return;}
            var query=String(el("pmPanel").find("[data-v2-search]").val()||"").trim().toLowerCase(),onlySelected=el("pmPanel").find("[data-v2-selected-only]").prop("checked"),visible=0;
            var options=el("pmPanelValues").find("[data-v2-option]");
            options.each(function(){var option=$(this),matches=option.text().toLowerCase().indexOf(query)>=0&&(!onlySelected||option.find("[data-v2-value]").prop("checked"));option.prop("hidden",!matches);if(matches){visible++;}});
            el("pmPanel").find("[data-v2-selected-count]").text("已选 "+selectedIds().length+" / "+options.length);
            el("pmPanel").find("[data-v2-filter-empty]").text(onlySelected&&!query?"暂无已选参数":"没有匹配的参数").prop("hidden",!options.length||visible>0);
        }
        function renderPanelValues(ids){
            var html="";
            panelValues.forEach(function(value){
                var id=String(value.id),option=pmv2ParameterOption(value);
                html+="<label class='pm-val-item' data-v2-option><input type='checkbox' data-v2-value value='"+pmv2Esc(id)+"'"+(ids.indexOf(id)>=0?" checked":"")+"><span class='pm-param-label'><b class='pm-param-value'>"+pmv2Esc(option.parameter===""?"空白":option.parameter)+"</b>"+(option.remark?"<span class='pm-val-desc'>("+pmv2Esc(option.remark)+")</span>":"")+"</span></label>";
            });
            el("pmPanelValues").html(html||"<p class='pm-val-empty'>暂无参数</p>");
            filterPanelValues();
        }
        function loadPanelValues(ids){
            if(!panel){return;}
            updatePanelTitle();
            var typeId=panelField("type").val(),request=++panelRequest,context=panel;
            panelValues=[];context.loading=!!typeId;
            el("pmPanelValues").html(typeId?"<p class='pm-val-empty'>正在加载参数值…</p>":"<p class='pm-val-empty'>请先选择参数类型。</p>");
            filterPanelValues();
            if(!typeId){return;}
            if(!Array.isArray(ids)){ids=panelUnion(typeId);}
            Ajax.get("admin/siargo/prodparam/value/options/"+encodeURIComponent(typeId),function(ret){
                if(disposed||request!==panelRequest||panel!==context){return;}
                var available=ret.data||[],map={},ordered=[];
                pmv2AvailableValues(available).forEach(function(value){map[value.id]=value;});
                ids.forEach(function(id){if(map[id]){ordered.push(map[id]);delete map[id];}});
                Object.keys(map).forEach(function(id){ordered.push(map[id]);});
                panelValues=ordered;catalog[String(typeId)]={typeName:panelField("type").find("option:selected").text(),values:ordered};context.loading=false;
                render();
                renderPanelValues(ids);
            },function(){if(!disposed&&request===panelRequest&&panel===context){context.loading=true;el("pmPanelValues").html("<p class='pm-val-empty'>参数值加载失败，请重新选择类型或适用范围。</p>");}});
        }
        function openPanel(branch,key,trigger){
            if(!branch){return;}
            closePanel();setExpanded(page.find(".pm-selection-section"),true);
            var position=key==null?null:Number(key),slot=position==null?null:branch.slots[position];
            panel={groupKey:branch.key,position:position,cursor:branch.cursor,trigger:trigger||root.document.activeElement};
            var token=slot?branch.tokens[slot.tokenIndex]:{},context=panel,scopes=slot&&slot.mixedTypes?"":"<option value='all'>全部规格</option>",title=slot?"编辑参数 · "+slot.title:"添加参数";
            if(slot){branch.sources.forEach(function(source){scopes+="<option value='"+pmv2Esc(source.key)+"'>"+pmv2Esc(source.label)+"</option>";});}
            var html="<div id='pmPanel_"+sid+"' class='pm-parameter-editor'><div class='pm-panel-body'><div class='pm-param-fields'>"
                +"<div class='pm-panel-scope'"+(!slot||branch.branchKeys.length===1?" hidden":"")+"><label for='pmScope_"+sid+"'>适用规格</label><select class='form-control' id='pmScope_"+sid+"' data-v2-field='scope'"+(!slot||branch.branchKeys.length===1?" disabled":"")+">"+scopes+"</select></div>"
                +"<div class='pm-dictionary-type'><label for='pmDictionaryType_"+sid+"'>参数类型</label>"
                +"<select class='form-control' id='pmDictionaryType_"+sid+"' data-v2-field='type' data-select-type='select2' data-text='请选择参数类型' data-value-attr='id' data-text-attr='typeName' data-url='admin/siargo/prodparam/options'>"+typeOptions(token.t)+"</select></div>"
                +"</div><div class='pm-dictionary-values'><div class='pm-param-toolbar'><input type='search' class='form-control pm-panel-val-search' id='pmDictionarySearch_"+sid+"' data-v2-search placeholder='搜索参数或备注' aria-label='搜索参数或备注' autocomplete='off'>"
                +"<label class='pm-param-selected-filter'><input type='checkbox' data-v2-selected-only> 仅看已选</label><span class='pm-param-selected-count' data-v2-selected-count aria-live='polite'></span></div>"
                +"<div class='pm-dictionary-value-list' id='pmPanelValues_"+sid+"'></div><p class='pm-param-filter-empty' data-v2-filter-empty hidden></p></div></div>"
                +"<div class='pm-panel-ft'><button type='button' class='btn btn-outline-secondary btn-sm' data-v2-panel-close>取消</button><button type='button' class='btn btn-primary btn-sm' data-v2-panel-save>确定</button></div></div>";
            branchEl(branch).find(".pm-selection-details").prop("open",true);
            root.DialogUtil.openNewDialog({id:"pm_parameter_dialog_"+sid+"_"+(++panelSequence),ele:$(trigger||root.document.activeElement),title:title,content:html,width:"860",height:"680",btn:"no",shadeClose:false,
                successHandler:function(){
                    if(disposed||panel!==context){return;}
                    panelElement=$("#pmPanel_"+sid);panelDialog=panelElement.closest(".layui-layer");panelIndex=Number(panelDialog.attr("times"));
                    panelDialog.addClass("pm-param-dialog").attr({role:"dialog","aria-modal":"true","aria-label":title});
                    // 原生 content 包装层只保留全高容器，编辑器由模块样式控制留白。
                    panelElement.parent().removeClass("p-3 text-break").css("height","100%");
                    panelDialog.find(".layui-layer-min,.layui-layer-max").hide();
                    panelDialog.find(".layui-layer-close").on("click"+eventNamespace,closePanel);
                    panelElement.on("click"+eventNamespace,"[data-v2-panel-close]",closePanel).on("click"+eventNamespace,"[data-v2-panel-save]",savePanel)
                        .on("change"+eventNamespace,"[data-v2-field='type']",function(){loadPanelValues();}).on("change"+eventNamespace,"[data-v2-field='scope']",changePanelScope)
                        .on("input"+eventNamespace,"[data-v2-search]",filterPanelValues).on("change"+eventNamespace,"[data-v2-value],[data-v2-selected-only]",filterPanelValues);
                    fitPanelDialog();initPanelTypeSelect(context);loadPanelValues();el("pmDictionarySearch").trigger("focus");
                    refreshTypes(function(){if(panel===context){var selected=panelField("type").val();if(refreshPanelTypeOptions(selected)!==String(selected||"")){loadPanelValues([]);}}});
                },closeHandler:function(){discardPanel(context);}
            });
        }
        function changePanelScope(){
            if(!panel){return;}
            var group=branchAt(panel.groupKey),key=panelField("scope").val();
            if(panel.position!=null){
                var source=pmv2GroupTargets(state.branches,group,key&&key!=="all"?[key]:group.branchKeys)[0];
                refreshPanelTypeOptions(pmv2SlotAtPosition(source,panel.position).token.t);
            }
            loadPanelValues();
        }
        function savePanel(){
            if(!panel||panel.loading){return;}
            var branch=branchAt(panel.groupKey),typeId=panelField("type").val(),next,cursor=panel.position==null?panel.cursor+1:branch.cursor;
            try{next=pmv2ApplyGroupSlot(state.branches,branch,panel.position,{branchKeys:panelTargets().map(function(source){return source.key;}),tokenIndex:panel.cursor,typeId:typeId,valueIds:selectedIds()},catalog);}catch(error){LayerMsgBox.alert(pmv2Esc(error.message),2);return;}
            applyChanges(next,branch,cursor);
        }
        function applyChanges(next,branch,cursor){
            var sourceKey=branch.branchKeys[0],focused=branch.focus;
            closePanel();branch.cursor=cursor;state.branches=next;render();
            var current=state.groups.filter(function(group){return group.branchKeys.indexOf(sourceKey)>=0;})[0];
            if(current){current.cursor=cursor;current.focus=focused;renderBranch(current);if(focused){branchEl(current).find(".pm-stage").trigger("focus");}}
        }
        function removeSlot(branch,index){
            var next;
            try{next=pmv2EditGroupStructure(state.branches,branch,{kind:"removeToken",index:index});}catch(error){LayerMsgBox.alert(error.message,2);return false;}
            applyChanges(next,branch,Math.min(index,branch.tokens.length-1));return true;
        }
        function insertText(branch,text){
            var next;
            try{next=pmv2EditGroupStructure(state.branches,branch,{kind:"insertText",index:branch.cursor,text:text});}catch(error){LayerMsgBox.alert(error.message,2);return;}
            applyChanges(next,branch,branch.cursor+text.length);
        }
        function submit(successCallback){
            if(loadError){LayerMsgBox.alert(loadError,2);return false;}
            if(submitting){return false;}pmv2SyncTypeNames(state.branches,catalog);pmv2CommitBranches(state.schema,state.branches);
            var series=String(el("pmSeries").val()||"").trim(),prodType=Number(el("pmProdType").val());
            if(!/^[A-Za-z0-9][A-Za-z0-9\/-]{0,49}$/.test(series)){LayerMsgBox.alert("产品系列须以字母或数字开头，仅允许字母、数字、横杠（-）和斜杠（/），长度不超过50个字符",2);return false;}
            if(!prodType){LayerMsgBox.alert("请选择产品类型",2);return false;}
            var branchLabel=String(el("pmBranchLabel").val()||"").trim(),catalogSort=String(el("pmCatalogSort").val()||"").trim();
            if(branchLabel.length>100||/[\x00-\x1f\x7f-\x9f]/.test(branchLabel)){LayerMsgBox.alert("目录分支名称不超过100字，不能包含控制字符",2);return false;}
            if(catalogSort&&!/^[0-9]{1,9}$/.test(catalogSort)){LayerMsgBox.alert("展示顺序须为不超过9位的非负整数",2);return false;}
            var payload={series:series,prodType:prodType,isActive:Number(el("pmIsActive").val()),modelDesc:JSON.stringify(pmv2Pack(state.schema)),segments:pmv2Segments(state.schema),productIntro:String(el("pmProductIntro").val()||"").trim(),productFeatures:String(el("pmProductFeatures").val()||"").trim(),remark:String(el("pmRemark").val()||"").trim()};
            payload.branchLabel=branchLabel;payload.catalogSort=catalogSort===""?null:Number(catalogSort);
            if(editData.id){payload.id=String(editData.id);}
            submitting=true;LayerMsgBox.loading("正在保存型号与选型参数…",10000);
            Ajax.post(actionUrl(page.attr("data-submit-url")),JSON.stringify(payload),function(ret){submitting=false;LayerMsgBox.closeLoadingNow();LayerMsgBox.success(ret.msg||"保存成功",500,function(){if(successCallback){successCallback();}});},function(){submitting=false;LayerMsgBox.closeLoadingNow();});return false;
        }
        root.submitThisForm=submit;
        page.data("prodModelV2Close",function(){disposed=true;closePanel();typeRequest++;$(root).off(eventNamespace);root.document.removeEventListener("keydown",panelKeydown,true);if(root.submitThisForm===submit){delete root.submitThisForm;}});
        $(root).on("resize"+eventNamespace,fitPanelDialog);
        root.document.addEventListener("keydown",panelKeydown,true);
        page.on("click.pmv2",".pm-section-toggle",function(){setExpanded($(this).closest(".pm-form-section"),$(this).attr("aria-expanded")!=="true");});
        page.on("click.pmv2","[data-v2-add-slot]",function(){openPanel(branchOf(this),null,this);});
        page.on("click.pmv2","[data-v2-slot]",function(event){event.stopPropagation();openPanel(branchOf(this),$(this).attr("data-v2-slot"),this);});
        page.on("click.pmv2","[data-v2-delete]",function(){var branch=branchOf(this),slot=branch.slots[Number($(this).attr("data-v2-delete"))];LayerMsgBox.confirm("确认从本选型规则"+(branch.branchKeys.length>1?"全部 "+branch.branchKeys.length+" 项规格条件":"")+"中删除“"+pmv2Esc(slot.title)+"”参数段位？",function(){removeSlot(branch,slot.tokenIndex);});});
        page.on("focusin.pmv2 focusout.pmv2",".pm-stage",function(event){var branch=branchOf(this);branch.focus=event.type==="focusin";$(this).find(".pm-cursor").toggleClass("pm-cursor-on",branch.focus);});
        page.on("keydown.pmv2",".pm-stage",function(event){
            if($(event.target).is("button,input,select,textarea")||event.ctrlKey||event.metaKey||event.altKey){return;}
            var branch=branchOf(this),key=event.key;
            if(key==="ArrowLeft"||key==="ArrowRight"){event.preventDefault();branch.cursor+=key==="ArrowLeft"?-1:1;renderBranch(branch);}
            else if(key==="Backspace"||key==="Delete"){event.preventDefault();var index=key==="Backspace"?branch.cursor-1:branch.cursor;if(index>=0&&index<branch.tokens.length){removeSlot(branch,index);}}
            else if(key==="□"||key==="☐"){event.preventDefault();openPanel(branch);}
            else if(/^[A-Za-z0-9 ._\/+²³µμ-]$/.test(key)){event.preventDefault();insertText(branch,key);}
        });
        page.on("paste.pmv2",".pm-stage",function(event){var clipboard=event.originalEvent.clipboardData;if(clipboard){event.preventDefault();insertText(branchOf(this),clipboard.getData("text/plain"));}});
        page.on("click.pmv2",".pm-model-preview",function(event){
            if($(event.target).closest("button,input,select,textarea").length){return;}
            var branch=branchOf(this),target=$(event.target).closest("[data-token-index]");branch.cursor=branch.tokens.length;
            if(target.length){var rect=target[0].getBoundingClientRect();branch.cursor=Number(target.attr("data-token-index"))+(event.clientX>rect.left+rect.width/2?1:0);}
            branch.focus=true;renderBranch(branch);branchEl(branch).find(".pm-stage").trigger("focus");
        });
        ["Series","ProdType","IsActive","BranchLabel","CatalogSort","ProductIntro","ProductFeatures","Remark"].forEach(function(name){var key=name.charAt(0).toLowerCase()+name.slice(1);if(editData[key]!=null){el("pm"+name).val(String(editData[key]));}});
        setExpanded(page.find(".pm-basic-section"),false);
        render();if(loadError){LayerMsgBox.alert(loadError,2);}refreshTypes();
        var materials=page.find(".pm-inputer-materials");if(materials.length){root.SiargoProdModelMaterials.init(materials[0],{technicalParams:editData.technicalParams});}
    }

    var legacyClose=root.SiargoProdModel.close;
    root.SiargoProdModel.init=init;
    root.SiargoProdModel.close=function(container){var page=root.jQuery(container),close=page.data("prodModelV2Close");if(close){close();page.removeData("prodModelV2Close");}legacyClose(container);};
}(window));

/* 产品资料：机械尺寸图与技术参数维护弹窗。 */
(function(root){
    "use strict";
    function init(container,options){
        var $=root.jQuery,page=$(container);
        if(!page.length||page.data("prodMaterialsInitialized")){return;}
        page.data("prodMaterialsInitialized",true);
        var sid=page.attr("data-page-id");
        //雪花ID超出JS安全整数范围，全程按字符串传递，由服务端转Long
        var modelId=String(page.attr("data-model-id")||"");
        var dimTable=$("#pmDimTable_"+sid),techTable=$("#pmTechTable_"+sid);
        var dimPanel=$("#pmDimPanel_"+sid),techPanel=$("#pmTechPanel_"+sid);
        var panels=dimPanel.add(techPanel),editorStore=page.find(".pm-mat-editor-store");
        var dimFile=$("#pmDimImage_"+sid),dimPreview=$("#pmDimPreview_"+sid);
        var tempUrl="",publishedUrl="",editingId="",editingKind="",busy=false,disposed=false;
        var dialogIndex=null,dialogElement=$(),closePending=false,parentCloseButtons=$();
        var eventNamespace=".pmMaterials_"+sid;
        var technicalSeed=options&&Array.isArray(options.technicalParams)?options.technicalParams:[],technicalRows=[],technicalColumns=[];

        function technicalField(name){return $("#pmTech"+name+"_"+sid);}
        function currentTechnicalRows(){
            // 原生表格的成功回调包括空结果；删除末行后不能重新使用编辑页的初始数据。
            var loaded=techTable.data("pmTechnicalRows");
            if(Array.isArray(loaded)){return loaded;}
            var rows=[];
            techTable.find("tbody tr[data-id]").each(function(){
                var tr=$(this),record={};
                [["id","id"],["paramName","name"],["catalogRowKey","row-key"],["catalogCellKey","cell-key"],["catalogColumnKey","column-key"],["catalogColumnLabel","column-label"],["catalogColumnSort","column-sort"]].forEach(function(pair){record[pair[0]]=tr.attr("data-"+pair[1])||"";});
                rows.push(record);
            });
            return rows.length?rows:technicalSeed;
        }
        function selectedTechnicalColumn(){
            var index=technicalField("Column").val();
            return index!=null&&index!==""?technicalColumns[Number(index)]:null;
        }
        function alignTechnicalRow(){
            if(!editingId){
                var name=$.trim(technicalField("Name").val()||""),keys=[];
                technicalRows.forEach(function(record){
                    var key=record.catalogRowKey||"";
                    if(record.paramName===name&&key&&keys.indexOf(key)<0){keys.push(key);}
                });
                technicalField("RowKey").val(keys.length===1?keys[0]:"");
            }
            var column=selectedTechnicalColumn(),allowBlank=!!(column&&column.key&&technicalField("RowKey").val());
            technicalField("Value").attr("aria-required",allowBlank?"false":"true");
            technicalField("ValueHint").text(allowBlank?"此目录参数可留空，表示当前规格不适用。":"请输入当前规格的参数值；不同规格分别维护。");
        }
        function chooseTechnicalColumn(){
            var column=selectedTechnicalColumn();
            technicalField("ColumnLabel").val(column?column.label:"");
            technicalField("ColumnSort").val(column?column.sort:"");
            alignTechnicalRow();
        }
        function loadTechnicalColumns(row){
            technicalRows=currentTechnicalRows();technicalColumns=[];
            technicalRows.forEach(function(record){
                var key=String(record.catalogColumnKey||"");
                if(!technicalColumns.some(function(column){return column.key===key;})){
                    technicalColumns.push({key:key,label:record.catalogColumnLabel||"",sort:record.catalogColumnSort==null?"":record.catalogColumnSort});
                }
            });
            if(!technicalColumns.length){technicalColumns.push({key:"",label:"",sort:""});}
            technicalColumns.sort(function(a,b){return Number(a.sort||0)-Number(b.sort||0);});
            var select=technicalField("Column").empty(),selected="",rowKey=row?String(row.attr("data-column-key")||""):null;
            select.append($("<option></option>").val("").text("请选择所属规格列"));
            technicalColumns.forEach(function(column,index){
                select.append($("<option></option>").val(String(index)).text(column.label||(column.key?"规格 "+(index+1):"默认规格")));
                if(rowKey===column.key||(!row&&technicalColumns.length===1)){selected=String(index);}
            });
            select.val(selected);chooseTechnicalColumn();
        }

        function alertText(msg){root.LayerMsgBox.alert($("<div>").text(msg).html(),2);}
        function setBusy(value){
            busy=value;
            panels.find("button,input,textarea,select").add(page.find(".pm-mat-add")).prop("disabled",value);
            dialogElement.find(".layui-layer-close").attr("aria-disabled",value?"true":"false");
        }
        function nextRank(table){
            var max=0;
            table.find("tbody tr[data-rank]").each(function(){
                var rank=parseInt($(this).attr("data-rank"),10);
                if(!isNaN(rank)&&rank>max){max=rank;}
            });
            return max+1;
        }
        function rankOf(selector){
            var raw=$.trim($(selector).val()||"");
            return /^\d+$/.test(raw)?parseInt(raw,10):null;
        }

        /* 暂存图仅在保存成功后发布，关闭和页面卸载时清理未保存的图片。 */
        function discardTemp(useBeacon){
            if(!tempUrl){return;}
            var url=tempUrl;
            tempUrl="";
            var target="admin/siargo/prodmodel/deleteTempFile";
            if(useBeacon&&root.navigator.sendBeacon){
                root.navigator.sendBeacon(root.actionUrl(target+"?filePath="+encodeURIComponent(url)));
                return;
            }
            $.ajax({url:root.actionUrl(target),type:"POST",dataType:"json",data:{filePath:url},async:!useBeacon});
        }
        function resetEditor(){
            editingId="";editingKind="";publishedUrl="";
            dimFile.val("");
        }
        function guardParentClose(event){
            if(busy){event.preventDefault();event.stopImmediatePropagation();return;}
            cleanup();
        }
        function cleanup(){
            if(disposed){return;}
            disposed=true;
            $(root).off(eventNamespace);
            $(root.document).off(eventNamespace);
            parentCloseButtons.each(function(){this.removeEventListener("click",guardParentClose,true);});
            page.off(eventNamespace);
            panels.find("*").off(eventNamespace);
            //请求完成后再清理，避免与服务端文件发布和回滚竞争。
            if(busy){return;}
            discardTemp(true);
            closePanel();
            resetEditor();
        }
        function finishClosedRequest(){
            if(!disposed&&!closePending){return false;}
            discardTemp(true);
            closePending=false;
            closePanel();
            resetEditor();
            return true;
        }
        function renderPreview(){
            var src=tempUrl||publishedUrl;
            dimPreview.empty();
            if(!src){
                dimPreview.append("<span class='pm-mat-tip-inline'>尚未上传图片</span>");
                return;
            }
            dimPreview.append($("<a target='_blank' rel='noopener' title='新窗口查看原图'></a>").attr("href",src)
                .append($("<img class='pm-mat-thumb-lg' alt='尺寸图预览'/>").attr("src",src)));
            if(tempUrl){dimPreview.append("<span class='pm-mat-tip-inline'>新图片将在保存后生效</span>");}
        }

        /* 原生 Layer 使用真实表单节点，避免克隆后的重复 ID、事件丢失和上传状态错位。 */
        function dialogSize(){
            var width=root.innerWidth||root.document.documentElement.clientWidth;
            var height=root.innerHeight||root.document.documentElement.clientHeight;
            return {width:Math.max(1,Math.min(editingKind==="dimension"?860:780,width-32)),
                height:Math.max(1,Math.min(editingKind==="dimension"?620:580,height-32)),
                viewportWidth:width,viewportHeight:height};
        }
        function fitDialog(){
            if(dialogIndex===null||!dialogElement.length){return;}
            var size=dialogSize();
            root.layer.style(dialogIndex,{width:size.width+"px",height:size.height+"px",
                left:Math.max(0,(size.viewportWidth-size.width)/2)+"px",
                top:Math.max(0,(size.viewportHeight-size.height)/2)+"px"});
            dialogElement.find(".layui-layer-content").height(Math.max(0,size.height-dialogElement.find(".layui-layer-title").outerHeight()));
        }
        function openPanel(panel,title,trigger,focusSelector){
            var size=dialogSize();
            //Layer 对 jQuery 内容在原位置包裹，先移出隐藏容器再打开。
            panel.detach().appendTo(root.document.body);
            dialogIndex=root.layer.open({
                type:1,title:title,skin:"pm-mat-dialog",area:[size.width+"px",size.height+"px"],
                content:panel,shade:0.3,shadeClose:false,maxmin:false,resize:false,
                success:function(layero,index){
                    dialogIndex=index;dialogElement=layero;
                    layero.attr({role:"dialog","aria-modal":"true","aria-label":title});
                    fitDialog();
                    panel.find(focusSelector).first().trigger("focus");
                },
                cancel:function(){return !busy;},
                end:function(){
                    panel.detach().appendTo(editorStore);
                    dialogIndex=null;dialogElement=$();
                    if(busy){closePending=true;return;}
                    discardTemp(false);
                    resetEditor();
                    if(!disposed&&trigger&&trigger.length&&root.document.documentElement.contains(trigger[0])){trigger.trigger("focus");}
                }
            });
        }
        function closePanel(){
            if(busy){return false;}
            if(dialogIndex!==null){root.layer.close(dialogIndex);}
            return false;
        }
        function openDimension(row,trigger){
            if(busy||disposed||dialogIndex!==null){return;}
            editingKind="dimension";
            editingId=row?String(row.attr("data-id")):"";
            publishedUrl=row?String(row.attr("data-image")||""):"";
            $("#pmDimTitle_"+sid).val(row?(row.attr("data-title")||""):"");
            $("#pmDimRank_"+sid).val(row?(row.attr("data-rank")||""):nextRank(dimTable));
            $("#pmDimRemark_"+sid).val(row?(row.attr("data-remark")||""):"");
            renderPreview();
            openPanel(dimPanel,row?"编辑机械尺寸图":"新增机械尺寸图",trigger,"#pmDimTitle_"+sid);
        }
        function openTechnical(row,trigger){
            if(busy||disposed||dialogIndex!==null){return;}
            editingKind="technical";
            editingId=row?String(row.attr("data-id")):"";
            $("#pmTechName_"+sid).val(row?(row.attr("data-name")||""):"");
            $("#pmTechValue_"+sid).val(row?(row.attr("data-value")||""):"");
            $("#pmTechUnit_"+sid).val(row?(row.attr("data-unit")||""):"");
            $("#pmTechRank_"+sid).val(row?(row.attr("data-rank")||""):nextRank(techTable));
            $("#pmTechRemark_"+sid).val(row?(row.attr("data-remark")||""):"");
            technicalField("RowKey").val(row?(row.attr("data-row-key")||""):"");
            technicalField("CellKey").val(row?(row.attr("data-cell-key")||""):"");
            loadTechnicalColumns(row);
            openPanel(techPanel,row?"编辑技术参数":"新增技术参数",trigger,"#pmTechName_"+sid);
        }

        function uploadPicked(files){
            dimFile.val("");
            if(busy||disposed||!files||!files.length){return;}
            var file=files[0];
            if(!/\.(png|jpe?g|gif|bmp)$/i.test(file.name)){alertText("仅支持 PNG/JPG/JPEG/GIF/BMP 图片");return;}
            if(file.size>20*1024*1024){alertText("图片不能超过 20MB");return;}
            setBusy(true);
            root.LayerMsgBox.loading("正在上传...",60000);
            var form=new FormData();
            form.append("file",file);
            root.Ajax.uploadFormData("admin/siargo/prodmodel/dimensionUpload",form,function(ret){
                root.LayerMsgBox.closeLoadingNow();
                setBusy(false);
                discardTemp(false);
                tempUrl=ret.data||"";
                if(finishClosedRequest()){return;}
                renderPreview();
            },function(){
                root.LayerMsgBox.closeLoadingNow();
                setBusy(false);
                finishClosedRequest();
            });
        }
        function submit(action,payload,table){
            setBusy(true);
            root.LayerMsgBox.loading("正在保存...",10000);
            root.Ajax.post("admin/siargo/prodmodel/"+action,JSON.stringify(payload),function(ret){
                root.LayerMsgBox.closeLoadingNow();
                setBusy(false);
                //保存成功的图片已发布，不能再作为暂存图片删除。
                tempUrl="";publishedUrl="";closePending=false;
                closePanel();
                if(dialogIndex===null){resetEditor();}
                if(!disposed){
                    root.refreshJBoltTable(table);
                    root.LayerMsgBox.success(ret.msg||"保存成功",500);
                }
            },function(){
                root.LayerMsgBox.closeLoadingNow();
                setBusy(false);
                finishClosedRequest();
            });
        }
        function saveDimension(){
            if(busy||disposed||editingKind!=="dimension"){return;}
            var title=$.trim($("#pmDimTitle_"+sid).val()||"");
            var remark=$.trim($("#pmDimRemark_"+sid).val()||"");
            var rank=rankOf("#pmDimRank_"+sid);
            var imagePath=tempUrl||publishedUrl;
            if(!imagePath){alertText("请先上传图片");return;}
            if(!title||title.length>200){alertText("标题不能为空且不超过200字");return;}
            if(rank===null){alertText("排序须为非负整数");return;}
            if(remark.length>500){alertText("备注不超过500字");return;}
            var payload={modelId:modelId,title:title,imagePath:imagePath,sortRank:rank,remark:remark};
            if(editingId){payload.id=editingId;}
            submit(editingId?"dimensionUpdate":"dimensionSave",payload,dimTable);
        }
        function saveTechnical(){
            if(busy||disposed||editingKind!=="technical"){return;}
            var name=$.trim($("#pmTechName_"+sid).val()||"");
            var value=$.trim($("#pmTechValue_"+sid).val()||"");
            var unit=$.trim($("#pmTechUnit_"+sid).val()||"");
            var remark=$.trim($("#pmTechRemark_"+sid).val()||"");
            var rank=rankOf("#pmTechRank_"+sid);
            alignTechnicalRow();
            var column=selectedTechnicalColumn(),rowKey=String(technicalField("RowKey").val()||""),cellKey=String(technicalField("CellKey").val()||"");
            var columnLabel=$.trim(technicalField("ColumnLabel").val()||""),columnSort=$.trim(technicalField("ColumnSort").val()||"");
            if(!column){alertText("请选择参数所属的规格列");return;}
            if(columnLabel.length>100||/[\x00-\x1f\x7f-\x9f]/.test(columnLabel)){alertText("规格列名称不超过100字，不能包含控制字符");return;}
            if(columnSort&&!/^[0-9]{1,9}$/.test(columnSort)){alertText("列顺序须为不超过9位的非负整数");return;}
            if(!name||name.length>200){alertText("参数名不能为空且不超过200字");return;}
            if((!value&&!(rowKey&&column.key))||value.length>16000){alertText("普通参数数值不能为空，数值不超过16000字");return;}
            if(unit.length>100){alertText("单位不超过100字");return;}
            if(rank===null){alertText("排序须为非负整数");return;}
            if(remark.length>500){alertText("备注不超过500字");return;}
            var payload={modelId:modelId,paramName:name,paramValue:value,unit:unit,sortRank:rank,remark:remark};
            payload.catalogRowKey=rowKey;payload.catalogCellKey=cellKey;payload.catalogColumnKey=column.key;
            payload.catalogColumnLabel=columnLabel;payload.catalogColumnSort=columnSort===""?null:Number(columnSort);
            if(editingId){payload.id=editingId;}
            submit(editingId?"technicalUpdate":"technicalSave",payload,techTable);
        }

        page.on("click"+eventNamespace,".pm-mat-add",function(){
            if($(this).attr("data-kind")==="dimension"){openDimension(null,$(this));}else{openTechnical(null,$(this));}
        });
        page.on("click"+eventNamespace,".pm-mat-edit",function(event){
            event.preventDefault();event.stopPropagation();
            var row=$(this).closest("tr");
            if(row.closest("table").attr("id")==="pmDimTable_"+sid){openDimension(row,$(this));}else{openTechnical(row,$(this));}
        });
        $("#pmDimPick_"+sid).on("click"+eventNamespace,function(){if(!busy){dimFile.trigger("click");}});
        dimFile.on("change"+eventNamespace,function(){uploadPicked(this.files);});
        $("#pmDimOk_"+sid).on("click"+eventNamespace,saveDimension);
        $("#pmDimCancel_"+sid).on("click"+eventNamespace,closePanel);
        $("#pmTechOk_"+sid).on("click"+eventNamespace,saveTechnical);
        $("#pmTechCancel_"+sid).on("click"+eventNamespace,closePanel);
        technicalField("Column").on("change"+eventNamespace,chooseTechnicalColumn);
        technicalField("Name").on("input"+eventNamespace,alignTechnicalRow);

        $(root).on("beforeunload"+eventNamespace,cleanup).on("resize"+eventNamespace,fitDialog);
        $(root.document).on("keydown"+eventNamespace,function(event){
            if(dialogIndex===null){return;}
            //校验提示或加载提示在上层时，由上层窗口接收键盘事件。
            var ownZ=parseInt(dialogElement.css("z-index"),10)||0,covered=false;
            $(".layui-layer:visible").each(function(){
                if(this!==dialogElement[0]&&(parseInt($(this).css("z-index"),10)||0)>ownZ){covered=true;}
            });
            if(covered){return;}
            if(event.key==="Escape"){
                event.preventDefault();event.stopImmediatePropagation();closePanel();
            }else if(event.key==="Tab"){
                var focusable=dialogElement.find("button,input,textarea,select,a[href],[tabindex='0']").filter(":visible").not(":disabled");
                if(!focusable.length){event.preventDefault();return;}
                var first=focusable[0],last=focusable[focusable.length-1],active=root.document.activeElement;
                if(!dialogElement[0].contains(active)||(event.shiftKey&&active===first)||(!event.shiftKey&&active===last)){
                    event.preventDefault();$(event.shiftKey?last:first).trigger("focus");
                }
            }
        });
        page.data("pmMaterialsCleanup",cleanup);
        try{
            var frameIndex=root.parent.layer.getFrameIndex(root.name);
            parentCloseButtons=root.parent.$("#layui-layer"+frameIndex).find(".layui-layer-close,.layui-layer-btn1");
            //捕获阶段先于原生 Layer 的关闭监听执行，处理中保留外层页面。
            parentCloseButtons.each(function(){this.addEventListener("click",guardParentClose,true);});
        }catch(ignored){/* 非 iframe 场景由 data-close-handler 负责清理。 */}
    }
    root.SiargoProdModelMaterials={
        init:init,
        technicalLoaded:function(table){table.data("pmTechnicalRows",Array.isArray(table.tableListDatas)?table.tableListDatas:[]);},
        close:function(container){
            var page=root.jQuery(container),cleanup=page.data("pmMaterialsCleanup");
            if(cleanup){cleanup();}
            page.removeData("prodMaterialsInitialized");
        }
    };
}(window));

/* ===== qarep 产品卡片：系列关联与逐产品表单 ===== */
(function(root) {
    "use strict";
    var fields = ['siargo_prod_model_id','model','number','qsi','qi','des','lt_status','flow_range','cuc','cucmax','cucmin','pv','thv','zp','fl','bv','la'];
    var currentPage = null;
    function text(value) { return value == null ? '' : String(value); }
    function normalize(row) {
        row = row || {};
        var result = {};
        fields.forEach(function(key) { result[key] = text(row[key]); });
        return result;
    }
    function validate(rows) {
        if (!rows.length) return '请至少添加一个产品';
        for (var i = 0; i < rows.length; i++) {
            var p = rows[i], prefix = '产品 #' + (i + 1) + '：';
            if (!/^[1-9][0-9]*$/.test(p.siargo_prod_model_id)) return prefix + '请选择型号系列';
            if (!p.model.trim()) return prefix + '请输入产品型号';
            if (!p.number.trim()) return prefix + '请输入产品编号';
            if (!/^[1-9][0-9]*$/.test(p.qsi) || !/^[1-9][0-9]*$/.test(p.qi)) return prefix + '送检数量和检验数量必须是正整数';
            if (Number(p.qsi) > 2147483647 || Number(p.qi) > 2147483647) return prefix + '数量超出允许范围';
            if (Number(p.qi) > Number(p.qsi)) return prefix + '检验数量不能大于送检数量';
            if (p.lt_status !== '1' && p.lt_status !== '2') return prefix + '请选择是否有成品检漏';
        }
        return '';
    }
    function collect(page) {
        var rows = [], parameters = {};
        page.find('[data-shared-product-parameters] [data-field]').each(function() {
            parameters[this.getAttribute('data-field')] = root.jQuery(this).val();
        });
        page.find('[data-product-list] > .qa-product-card').each(function() {
            var row = {};
            root.jQuery(this).find('[data-field]').each(function() { row[this.getAttribute('data-field')] = root.jQuery(this).val(); });
            rows.push(withSharedParameters(row, parameters));
        });
        return rows;
    }
    function withSharedParameters(row, parameters) {
        var result = normalize(row);
        // 新增页恢复独立的公共参数区，仍逐产品序列化；编辑页保留本产品参数。
        fields.slice(7).forEach(function(key) {
            if (Object.prototype.hasOwnProperty.call(parameters, key)) result[key] = text(parameters[key]);
        });
        return result;
    }
    function disposeCard(card) {
        card.find('select.select2-hidden-accessible').each(function() { root.jQuery(this).select2('destroy'); });
        card.remove();
    }
    function refreshSeriesHint(card) {
        var select = card.find('[data-field="siargo_prod_model_id"]');
        if (text(select.val())) card.find('[data-series-hint]').text('').removeClass('text-warning');
    }
    function refreshLargeMeterParams(page, card) {
        if (page.attr('data-form-mode') !== 'edit') return;
        var series = card.find('[data-field="siargo_prod_model_id"]');
        // 下拉异步加载完成前保留服务端回显 ID；加载后清空选择则隐藏参数。
        var id = Array.isArray(series.data('option-datas')) ? text(series.val()) : text(series.attr('data-select'));
        var ids = page.data('qarepFormState').largeMeterSeriesIds;
        // 只隐藏，不禁用或移除字段，确保原有参数仍随 product.* 提交。
        card.find('.qa-product-params').prop('hidden', !ids.has(id));
    }
    function initializeCard(page, card, data, adding, importedOptions) {
        var state = page.data('qarepFormState');
        var number = ++state.counter;
        if (adding) {
            var values = normalize(data);
            card.find('[data-field]').each(function() {
                var input = root.jQuery(this), key = input.attr('data-field');
                input.val(values[key]);
                if (this.tagName === 'SELECT') input.attr('data-select', values[key]).data('select', values[key]);
                input.removeAttr('name');
            });
        }
        card.find('[data-field]').each(function() {
            var input = root.jQuery(this);
            var id = 'qaProduct_' + state.id + '_' + number + '_' + input.attr('data-field');
            input.attr('id', id).closest('.form-group').find('label').first().attr('for', id);
        });
        var series = card.find('[data-field="siargo_prod_model_id"]');
        var selected = text(series.data('select'));
        if (selected) series.attr('data-url', 'admin/siargo/prodmodel/options?includeId=' + encodeURIComponent(selected)).data('url', 'admin/siargo/prodmodel/options?includeId=' + encodeURIComponent(selected));
        if (!adding) card.find('[data-product-remove]').remove();
        card.on('change.qarepSeries', '[data-field="siargo_prod_model_id"]', function() {
            refreshSeriesHint(card);
            refreshLargeMeterParams(page, card);
        });
        // 克隆内容未经过页面组件扫描，必须显式初始化远程选项及 Select2。
        if (importedOptions) {
            // 同一次导入复用选项节点，避免每张卡重复请求、解析整个系列目录。
            series.removeAttr('data-autoload').append(importedOptions.options.clone());
            if (selected && importedOptions.extra[selected]) series.append(importedOptions.extra[selected].clone());
            series.val(selected);
            root.Select2Util.initAutoLoadSelect(series);
        }
        root.SelectUtil.init({parent: card});
        card.find("select[data-autoload]").removeAttr("data-autoload");
        refreshLargeMeterParams(page, card);
        if (data && !selected) card.find('[data-series-hint]').text(text(data.matchReason) || '未识别型号系列，请搜索后选择').addClass('text-warning');
    }
    function renumber(page) {
        var cards = page.find('[data-product-list] > .qa-product-card');
        cards.each(function(i) { root.jQuery(this).find('.prod-idx').text('产品 #' + (i + 1)); });
        page.find('[data-product-empty]').toggle(cards.length === 0);
    }
    function add(page, data, importedOptions, index) {
        var template = page.find('template[data-product-template]')[0];
        if (!template) return;
        var card = root.jQuery(template.content.firstElementChild.cloneNode(true));
        page.find('[data-product-list]').append(card);
        initializeCard(page, card, data, true, importedOptions);
        if (importedOptions) {
            card.find('.prod-idx').text('产品 #' + (index + 1));
            card.find('[data-product-remove]').prop('disabled', true);
        } else renumber(page);
        return card;
    }
    function importStatus(page, message) {
        page.find('[data-order-import-status]').text(message).prop('hidden', !message);
    }
    function stopImport(page, restore) {
        var state = page.data('qarepFormState'), job = state && state.importJob;
        if (!job) return;
        state.importJob = null;
        root.clearTimeout(job.timer);
        job.requests.forEach(function(request) { request.abort(); });
        if (job.oldCards) {
            if (restore && !job.committed) {
                page.find('[data-product-list] > .qa-product-card').each(function() { disposeCard(root.jQuery(this)); });
                page.find('[data-product-list]').append(job.oldCards);
                renumber(page);
            } else job.oldCards.each(function() { disposeCard(root.jQuery(this)); });
        }
        job.controls.prop('disabled', false);
        page.find('[data-product-remove]').prop('disabled', false);
        page.removeAttr('aria-busy');
    }
    function importProducts(page, data) {
        var $ = root.jQuery, state = page.data('qarepFormState');
        if (!state || !page[0].isConnected) return;
        stopImport(page, true);
        var job = {requests: [], controls: page.find('[data-product-add], [data-product-remove], .qa-order-import :input').filter(':enabled')};
        state.importJob = job;
        job.controls.prop('disabled', true);
        page.attr('aria-busy', 'true');
        importStatus(page, '正在加载型号系列…');
        function active() { return page.data('qarepFormState') === state && state.importJob === job && page[0].isConnected; }
        function fail(message) {
            if (!active()) return;
            stopImport(page, true);
            importStatus(page, message);
            root.LayerMsgBox.alert(message, 2);
        }
        function requestOptions(includeId, done) {
            var url = 'admin/siargo/prodmodel/options' + (includeId ? '?includeId=' + encodeURIComponent(includeId) : '');
            job.requests.push($.ajax({url: root.actionUrl(url), type: 'GET', dataType: 'json', timeout: 10000,
                success: function(result) {
                    if (!active()) return;
                    if (!result || result.state !== 'ok' || !Array.isArray(result.data)) {
                        fail('型号系列加载失败，请重新导入'); return;
                    }
                    done(result.data);
                },
                error: function() { fail('型号系列加载失败，请重新导入'); }
            }));
        }
        function render(options) {
            if (!active()) return;
            var list = page.find('[data-product-list]'), index = 0, removed = 0;
            job.oldCards = list.children('.qa-product-card').detach();
            page.find('[data-product-empty]').hide();
            function step() {
                if (!active()) return;
                var start = Date.now(), count = 0;
                try {
                    // 限制每批工作量，让浏览器在卡片之间处理绘制和用户操作。
                    while (index < data.products.length && count++ < 5) {
                        add(page, data.products[index], options, index);
                        index++;
                        if (Date.now() - start >= 12) break;
                    }
                } catch (error) { fail('产品回填失败，请重新导入'); return; }
                importStatus(page, '正在导入产品 ' + index + ' / ' + data.products.length + '…');
                if (index < data.products.length) { job.timer = root.setTimeout(step, 0); return; }
                if (!job.committed) {
                    job.committed = true;
                    if (data.orderId != null) page.find('[name="qareport.order_id"]').val(text(data.orderId));
                    if (data.repType != null) page.find('[name="qareport.rep_type"]').val(text(data.repType)).trigger('change');
                }
                // 再次导入时，旧 Select2 的销毁也分批执行。
                while (removed < job.oldCards.length) {
                    disposeCard(job.oldCards.eq(removed++));
                    if (Date.now() - start >= 12) { job.timer = root.setTimeout(step, 0); return; }
                }
                job.oldCards = null;
                stopImport(page, false);
                importStatus(page, '已导入 ' + data.products.length + ' 个产品');
            }
            job.timer = root.setTimeout(step, 0);
        }
        requestOptions('', function(rows) {
            var template = page.find('template[data-product-template]')[0];
            if (!template) { fail('产品模板未加载，请重新打开页面'); return; }
            var select = $(template.content.querySelector('[data-field="siargo_prod_model_id"]').cloneNode(false));
            select.removeAttr('data-select-type data-select').append($('<option>').val('').text(select.attr('data-text') || '请选择'));
            root.SelectUtil.processSetOptionsHandler(select, null, false, {state: 'ok', data: rows});
            var options = {options: select.children(), extra: Object.create(null)}, present = Object.create(null), missing = [];
            rows.forEach(function(row) { present[text(row.id)] = true; });
            data.products.forEach(function(row) {
                var id = text(row.siargo_prod_model_id);
                if (id && !present[id]) { present[id] = true; missing.push(id); }
            });
            if (!missing.length) { render(options); return; }
            var pending = missing.length;
            missing.forEach(function(id) {
                // 保留原 includeId 回显语义；只补拉导入后停用等不在当前目录中的系列。
                requestOptions(id, function(included) {
                    var row = included.filter(function(item) { return text(item.id) === id; })[0];
                    if (!row) { fail('导入产品的型号系列已变化，请重新导入'); return; }
                    options.extra[id] = $('<option>').val(id).html(root.SiargoQarepForm.formatSeriesOption(row));
                    if (--pending === 0) render(options);
                });
            });
        });
    }
    function init(container) {
        var $ = root.jQuery, page = $(container);
        if (page.data('qarepFormState')) return;
        var state = {id: text(page.attr('data-page-id')), counter: 0, previousBefore: root.beforeFormSubmit,
            largeMeterSeriesIds: new Set(text(page.attr('data-large-meter-series-ids')).split(',').filter(Boolean))};
        page.data('qarepFormState', state);
        currentPage = page;
        if (page.attr('data-form-mode') === 'add') add(page);
        else {
            page.find('[data-product-list] > .qa-product-card').each(function() { initializeCard(page, $(this), null, false); });
            renumber(page);
        }
        page.on('click.qarepForm', '[data-product-add]', function() { if (!state.importJob) add(page); });
        page.on('click.qarepForm', '[data-product-remove]', function() { if (!state.importJob) { disposeCard($(this).closest('.qa-product-card')); renumber(page); } });
        state.before = function() {
            if (!page[0].isConnected) return false;
            if (state.importJob) { root.LayerMsgBox.alert('订单正在导入，请稍候再保存', 2); return false; }
            var rows = collect(page), error = validate(rows);
            if (error) { root.LayerMsgBox.alert(error, 2); return false; }
            page.find('input[name="productsJson"]').val(JSON.stringify(rows));
            return true;
        };
        root.beforeFormSubmit = state.before;
    }
    root.SiargoQarepForm = {
        init: init,
        formatSeriesOption: function(row) {
            row = row || {};
            var series = text(row.model_series), branch = text(row.branchlabel).trim();
            var label = series + ' - ' + text(row.prodtypename);
            if (branch && branch !== series) label += ' - ' + branch;
            // 原生 SelectUtil 将返回值拼入 option HTML，名称按纯文本显示。
            return label.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
        },
        close: function(container) {
            var page = root.jQuery(container), state = page.data('qarepFormState');
            if (!state) return;
            stopImport(page, false);
            if (root.beforeFormSubmit === state.before) {
                if (state.previousBefore) root.beforeFormSubmit = state.previousBefore;
                else delete root.beforeFormSubmit;
            }
            page.off('.qarepForm').find('.qa-product-card').off('.qarepSeries');
            page.removeData('qarepFormState');
            if (currentPage && currentPage[0] === page[0]) currentPage = null;
        },
        refreshVersions: function() {
            if (currentPage) root.SelectUtil.refresh(currentPage.find('select[name="product.pdfver"]'));
        },
        refreshCustomer: function() {
            if (currentPage) root.SelectUtil.refresh(currentPage.find('select[name="qareport.cust_id"]'));
        },
        refreshFlowRange: function() {
            if (currentPage) currentPage.find('select[data-field="flow_range"]').each(function() {
                root.SelectUtil.refresh(root.jQuery(this));
            });
        },
        importSuccess: function(uploader, type, fileInput, result) {
            var page = root.jQuery(fileInput).closest('.qa-product-form');
            if (!page.length) page = currentPage;
            if (!page || !page.length) return;
            var data = result && result.data;
            if (!data || !Array.isArray(data.products) || !data.products.length) { root.LayerMsgBox.alert('导入结果没有产品，请检查文件', 2); return; }
            importProducts(page, data);
        }
    };
    root.SiargoQarepProductData = {normalize: normalize, validate: validate, withSharedParameters: withSharedParameters, fields: fields.slice()};
}(window));

/* ===== qarep 列表与审批入口 ===== */
(function(root){
root.SiargoQarepList={init:function(container){
var page=root.jQuery(container),pageId=String(page.attr("data-page-id"));
if(page.data("qarepListReady"))return;page.data("qarepListReady",true);

/**
 * 报告单行合并 - AJAX成功回调入口
 * 同时合并主表和左侧固定列，确保无论 left_fixed 是否可见都能看到合并效果
 */
function mergeReportRows(table, res) {
    if (!table) return;

    // 分页参数
    var currentPage = parseInt(table.data("currentpage")) || 1;
    var pageSize = parseInt(table.data("pagesize")) || 15;
    // 合并列索引（跳过 0=checkbox），formnum 在索引 2
    var mergeColIndexes = [1, 2, 3, 4];
    var formnumColIdx = 2;

    // 1. 始终合并主表 tbody（确保无水平滚动条时用户也能看到合并效果）
    var $mainTbody = table.find("tbody");
    if ($mainTbody.length && $mainTbody.find("tr").length > 0) {
        mergeFixedLeftRows($mainTbody, mergeColIndexes, formnumColIdx, currentPage, pageSize);
    }

    // 2. 如果左侧固定列存在，也合并它（水平滚动条出现时覆盖层可见）
    var $fixedLeftTbody = null;
    if (table.left_fixed && table.left_fixed.body && table.left_fixed.body.table) {
        $fixedLeftTbody = table.left_fixed.body.table.find("tbody");
    }
    if (!$fixedLeftTbody || !$fixedLeftTbody.length) {
        // 降级：通过 DOM 查找
        if (table.table_box) {
            $fixedLeftTbody = table.table_box.find(".jbolt_table_fixed_left .jbolt_table_body table tbody");
        }
    }
    if ($fixedLeftTbody && $fixedLeftTbody.length && $fixedLeftTbody.find("tr").length > 0) {
        mergeFixedLeftRows($fixedLeftTbody, mergeColIndexes, formnumColIdx, currentPage, pageSize);
    }

    // 3. 绑定整组 hover 联动（主表与左侧固定列同步高亮）
    bindMergeGroupHover($mainTbody);
}

/**
 * 合并组 hover 联动 - 悬停组内任意行时整组高亮，主表与左固定列同步
 * 从 .jbolt_table_box 容器委托事件，确保固定列重建后仍能正常工作
 * @param $mainTbody      主表 tbody（用于定位 table_box）
 */
function bindMergeGroupHover($mainTbody) {
    if (!$mainTbody || !$mainTbody.length) return;
    var $tableBox = $mainTbody.closest('.jbolt_table_box');
    if (!$tableBox.length) return;
    // 从持久容器委托，解绑后重绑避免翻页重复
    $tableBox.off('.mergehover')
        .on('mouseenter.mergehover', 'tr[data-merge-gid]', function() {
            var gid = $(this).attr('data-merge-gid');
            $tableBox.find('tr[data-merge-gid="' + gid + '"]').addClass('merge-group-hover');
        })
        .on('mouseleave.mergehover', 'tr[data-merge-gid]', function() {
            var gid = $(this).attr('data-merge-gid');
            $tableBox.find('tr[data-merge-gid="' + gid + '"]').removeClass('merge-group-hover');
        });
}

/**
 * 核心合并逻辑 - 按 formnum 分组并应用 rowspan
 * @param $tbody       左侧固定列的 tbody jQuery 对象
 * @param colIndexes   需要合并的列索引数组（基于固定列表格的 td 索引）
 * @param formnumColIdx formnum 所在的列索引（用于分组依据）
 * @param currentPage  当前页码
 * @param pageSize     每页条数
 */
function mergeFixedLeftRows($tbody, colIndexes, formnumColIdx, currentPage, pageSize) {
    var $rows = $tbody.find("tr");
    if ($rows.length === 0) return;

    // 1. 按 formnum 连续分组
    var groups = [];
    var currentFormnum = null;
    $rows.each(function(i) {
        var formnum = $(this).find("td").eq(formnumColIdx).text().trim();
        if (formnum !== currentFormnum) {
            groups.push({ startIdx: i, count: 1 });
            currentFormnum = formnum;
        } else {
            groups[groups.length - 1].count++;
        }
    });

    // 2. 对每个分组执行合并
    var pageOffset = (currentPage - 1) * pageSize;
    groups.forEach(function(group, gIdx) {
        colIndexes.forEach(function(colIdx) {
            var $firstTd = $rows.eq(group.startIdx).find("td").eq(colIdx);
            if (group.count > 1) {
                $firstTd.attr("rowspan", group.count);
                for (var r = 1; r < group.count; r++) {
                    $rows.eq(group.startIdx + r).find("td").eq(colIdx).hide();
                }
            }
            $firstTd.css("vertical-align", "middle");
            // 序号列重算：每个分组只显示一个序号
            if (colIdx === 1) {
                $firstTd.text(pageOffset + gIdx + 1);
            }
        });
        // 多行合并组：统一底色+左侧色带（merge-group）、hover 联动标识（data-merge-gid）、产品数徽章
        if (group.count > 1) {
            $rows.eq(group.startIdx).addClass("merge-group-first");
            for (var r = 0; r < group.count; r++) {
                $rows.eq(group.startIdx + r).addClass("merge-group").attr("data-merge-gid", group.startIdx);
            }
            // 报告单编号单元格追加产品数徽章，明确合并语义
            var $formnumTd = $rows.eq(group.startIdx).find("td").eq(formnumColIdx);
            if (!$formnumTd.find(".merge-count-badge").length) {
                $formnumTd.append('<span class="merge-count-badge">' + group.count + ' 个</span>');
            }
        }
        // 为分组最后一行添加加深边框样式（组间刻痕线，含单行组）
        $rows.eq(group.startIdx + group.count - 1).addClass("merge-group-last");
    });
}

// ===== Tab懒加载与脏标记 =====
var loadedTabs = {};
var dirtyTabs = {};
var tableIds = ['allreport', 'noq', 'ltq', 'accq', 'funq', 'appq', 'allq'];

// 获取当前激活的Tab索引
function getCurrentTabIndex() {
	var activeLink = page.find('#qarepTabView .jbolt_tab_link.active');
	return activeLink.length ? page.find('#qarepTabView .jbolt_tab_link').index(activeLink) : 0;
}

// 切换Tab（函数名带 pageId 后缀，避免污染全局命名空间与其他页面冲突）
function switchTabByIndex(index) {
	// 更新导航active状态
	page.find('.flow-summary-badge').removeClass('active');
	page.find('.flow-stepper-item').removeClass('active');
	if (index === 0) {
		page.find('.flow-summary-badge').addClass('active');
	} else {
		page.find('.flow-stepper-item').eq(index - 1).addClass('active');
	}

	// 懒加载 + 脏标记刷新
	if (!loadedTabs[index] || dirtyTabs[index]) {
		loadedTabs[index] = true;
		dirtyTabs[index] = false;
		var tableId = tableIds[index] + '_mgrtable_' + pageId + '';
		setTimeout(function() {
			refreshJBoltTableById(tableId);
		}, 50);
	}

	// 切换底层JBolt Tab
	page.find('#qarepTabView .jbolt_tab_link').eq(index).trigger('click');
}

// 获取各流程阶段数量（带防抖，避免短时间内重复请求）
var loadFlowCountsTimer = null;
function loadFlowCounts() {
	if (loadFlowCountsTimer) clearTimeout(loadFlowCountsTimer);
	loadFlowCountsTimer = setTimeout(function() {
		// 规范 98：使用平台 Ajax 对象族（自动处理登录失效/系统锁定/终端离线等全局状态）
		Ajax.get('admin/siargo/qarep/getFlowCounts', function(res) {
			tableIds.slice(1).forEach(function(prefix) {
				page.find('#count-' + prefix).text(res.data[prefix] || 0);
			});
		});
	}, 100);
}

// 页面加载时获取数量（守卫：防止 JBolt 双重渲染导致重复初始化）
$(function() {
	if (!page[0].isConnected) return;
	// 初始化步骤条active状态并触发首次流程数量加载（内建防抖）
	const activeIndex = page.find('#qarepTabView').data('active-index') || 0;

	// 预标记默认tab为已加载，防止switchTabByIndex中重复触发
	loadedTabs[activeIndex] = true;
	switchTabByIndex(activeIndex);

	// 首次加载流程数量（防抖100ms确保只发一次请求）
	loadFlowCounts();

	// 定时刷新数量（每30秒），页面不可见时暂停以节省资源；
	// 用 AbortController 管理监听器生命周期，页面卸载/重载时可整体中止，避免监听器泄漏
	var flowCountTimer = setInterval(loadFlowCounts, 30000);
    page.data("qarepFlowStop", function(){ visibilityAbort.abort(); clearInterval(flowCountTimer); clearTimeout(loadFlowCountsTimer); });
	var visibilityAbort = new AbortController();
	document.addEventListener('visibilitychange', function() {
		if (document.hidden) {
			clearInterval(flowCountTimer);
		} else {
			loadFlowCounts();
			flowCountTimer = setInterval(loadFlowCounts, 30000);
		}
	}, { signal: visibilityAbort.signal });
	window.addEventListener('pagehide', function() {
		visibilityAbort.abort();
		clearInterval(flowCountTimer);
	}, { once: true });

	// PDF 查看链接防缓存：捕获阶段委托（第三参 true，先于 JBolt data-openpage 的冒泡委托执行），
	// 从 data-pdf-url（dataset 读取，天然免字符串拼接注入）取原始地址并拼时间戳改写 href，
	// 随后 JBolt 的 jboltlayer 机制按改写后的 href 正常打开
	document.addEventListener('click', function(e) {
		var link = e.target && e.target.closest ? e.target.closest('a[data-pdf-url]') : null;
		if (!link || !page[0].contains(link)) return;
		var pdfUrl = link.dataset.pdfUrl;
		if (pdfUrl) {
			link.setAttribute('href', pdfUrl + (pdfUrl.indexOf('?') >= 0 ? '&t=' : '?t=') + Date.now());
		}
	}, {capture:true, signal:visibilityAbort.signal});
});

// 打开删除确认弹窗
function openDeleteDialog(tableId) {
    // 获取选中的ID
    const ids = jboltTableGetCheckedIds(tableId);
    if (!ids || ids.length === 0) {
        LayerMsgBox.alert('请先选择要删除的数据', 2);
        return;
    }

    // 打开layer弹窗
    layer.open({
        type: 1,
        title: '删除确认',
        area: ['450px', '320px'],
        content: '<div class="p-3">' +
            '<div class="form-group">' +
            '<label class="mb-2"><span class="text-danger">*</span> <i class="fa fa-question-circle text-warning mr-1"></i>确定把选中数据放入回收站？</label>' +
            '<textarea id="deleteReason_' + pageId + '" class="form-control" rows="4" placeholder="请输入删除原因（必填）..."></textarea>' +
            '</div></div>',
        btn: ['确定', '取消'],
        yes: function(index) {
            var reason = $('#deleteReason_' + pageId + '').val();
            if (!reason || $.trim(reason) === '') {
                LayerMsgBox.alert('请输入删除原因', 2);
                return;
            }
            var idsStr = $.isArray(ids) ? ids.join(',') : ids;
            LayerMsgBox.loading('正在处理...', 10000);
            $.ajax({
                url: 'admin/siargo/qarep/deleteByIds',
                type: 'post',
                data: { ids: idsStr, delete_des: reason },
                success: function(res) {
                    LayerMsgBox.closeLoading();
                    layer.close(index);
                    if (res.msg && res.msg == 'jbolt_terminal_offline') {
                        showReloginDialog();
                        LayerMsgBox.alert('当前用户已在其它终端登录，本机已下线', 2, function() {
                            top.location.href = '/admin';
                        });
                    } else if ('ok' == res.state) {
                        LayerMsgBox.success('删除成功', 800, function() {
                            refreshAllQarepTables();
                        });
                    } else {
                        LayerMsgBox.alert(res.msg || '删除失败', 2);
                    }
                },
                error: function() {
                    LayerMsgBox.closeLoading();
                    layer.close(index);
                    LayerMsgBox.error('请求失败，请重试');
                }
            });
        }
    });
}

// 打开审批工作台抽屉（JBoltLayer 右侧滑出，iframe 加载独立审批页）
// 注意：必须阻断点击冒泡，否则事件冒泡到 .jbolt_admin_main 会触发 JBoltLayerUtil.close 导致抽屉闪退
function openApprovalDrawer(e, tableId, insp) {
    if (e) { e.stopPropagation(); e.preventDefault(); }
    var ids = jboltTableGetCheckedIds(tableId);
    if (!ids || ids.length === 0) {
        LayerMsgBox.alert('请先勾选要审批的产品', 2);
        return;
    }
    var idsStr = $.isArray(ids) ? ids.join(',') : ids;
    JBoltLayerUtil.openByNav('admin/siargo/qarep/approval?insp=' + insp + '&ids=' + idsStr, {
        dir: 'right', width: 620, resize: true, loadType: 'iframe'
    });
}

// 刷新当前Tab + "全部"Tab，其余Tab标记为脏（切换时再刷新）
function refreshAllQarepTables() {
    // 始终刷新"全部"Tab
    refreshJBoltTableById('allreport_mgrtable_' + pageId + '');
    var currentIdx = getCurrentTabIndex();
    // 刷新当前激活的Tab（非"全部"时）
    if (currentIdx > 0) {
        refreshJBoltTableById(tableIds[currentIdx] + '_mgrtable_' + pageId + '');
    }
    // 清除当前Tab的脏标记（已刷新，无需再标记）
    dirtyTabs[currentIdx] = false;
    // 标记其余Tab为脏，切换时自动刷新
    for (var i = 1; i < tableIds.length; i++) {
        if (i !== currentIdx) {
            dirtyTabs[i] = true;
        }
    }
    loadFlowCounts();
}

// 暴露刷新入口供审批 iframe 跨窗口调用（须位于 refreshAllQarepTables 声明之后，函数声明提升不受影响）
window._qarepRefresh = refreshAllQarepTables;

// 框架标签页切换自动刷新：切回本页（报单号）时刷新一次列表数据与流程数量
// 复用平台自身的 tab 点击事件委托（与平台切换逻辑同源）：
// - 捕获阶段监听（先于平台在 jboltBody 上的冒泡 handler 执行），此时能读到 tab 点击前的激活状态
// - 仅"从其他标签页切回本页"（点击的 tab 非当前激活，且其 key 就是本页所在 tabcontent 的 key）时刷新一次
// - 非 data-auto-refresh 周期机制，无周期轮询；页面整页刷新后重新执行脚本时先移除旧监听，避免累积泄漏
(function(){
	if (window.__qarepTabRefreshCapture) {
		document.removeEventListener('click', window.__qarepTabRefreshCapture, true);
	}
	var handler = function(e) {
		var t = e.target;
		var li = t && t.closest ? t.closest('ul.jbolt_tabs>li') : null;
		if (!li || li.classList.contains('active')) return; // 点击的是当前激活 tab，跳过
		if (t.closest && t.closest('i.close')) return; // 点击关闭按钮不触发刷新
		var clickedKey = li.getAttribute('data-key');
		setTimeout(function() {
			var portal = $('.jbolt_page[data-page-id="' + pageId + '"]').closest('.jbolt_tabcontent');
			// portal 仍挂载、本页已激活、且其 tab key 与点击的 tab 一致 → 确实切回了本页，刷新一次
			if (portal.length === 1 && portal[0].isConnected && portal.hasClass('active') && portal.attr('data-key') === clickedKey) {
				refreshAllQarepTables();
			}
		}, 80);
	};
	window.__qarepTabRefreshCapture = handler;
    page.data("qarepTabCapture",handler);
	document.addEventListener('click', handler, true);
})();


window['switchTabByIndex_'+pageId]=switchTabByIndex;
window['openDeleteDialog_'+pageId]=openDeleteDialog;
window['openApprovalDrawer_'+pageId]=openApprovalDrawer;
window['refreshAllQarepTables_'+pageId]=refreshAllQarepTables;
root.SiargoQarepList.mergeReportRows=mergeReportRows;
page.data("qarepRefresh",refreshAllQarepTables);
root.SiargoQarepPdf.init(page);

},close:function(container){
var page=root.jQuery(container),stop=page.data("qarepFlowStop"),capture=page.data("qarepTabCapture"),pageId=page.attr("data-page-id");
if(stop)stop();if(capture)document.removeEventListener('click',capture,true);
if(root.__qarepTabRefreshCapture===capture)delete root.__qarepTabRefreshCapture;
page.off('.qarepPdf').removeData("qarepListReady");
['switchTabByIndex','openDeleteDialog','openApprovalDrawer','refreshAllQarepTables'].forEach(function(name){delete root[name+'_'+pageId];});
if(root._qarepRefresh===page.data("qarepRefresh"))delete root._qarepRefresh;
}};
}(window));

/* ===== qarep 审批工作台 ===== */
(function(root){root.SiargoQarepApproval={init:function(container){
var page=root.jQuery(container),pageId=String(page.attr("data-page-id"));
if(page.data("qarepApprovalReady"))return;page.data("qarepApprovalReady",true);

var rejectArmed = false;   // 驳回按钮是否处于「确认驳回」态
var submitting = false;    // 提交中防重复标记

// 获取当前勾选的产品 id 数组
function apprCheckedIds() {
	var ids = [];
	$('#apprBody_' + pageId + ' .qa-appr-ck:checked').each(function() {
		ids.push($(this).val());
	});
	return ids;
}

// 勾选状态变化 → 刷新计数、按钮可用态、全选框联动
function apprRefreshState() {
	var n = apprCheckedIds().length;
	$('.qa-appr-cnt').text(n);
	var disabled = (n === 0) || submitting;
	$('#apprPassBtn_' + pageId + '').prop('disabled', disabled || rejectArmed);
	$('#apprRejectBtn_' + pageId + '').prop('disabled', disabled);
	var total = $('#apprBody_' + pageId + ' .qa-appr-ck').length;
	$('#apprCheckAll_' + pageId + '').prop('checked', total > 0 && n === total);
}

// 取消驳回：收起原因区，按钮复原
function cancelReject() {
	rejectArmed = false;
	$('#apprRejectBox_' + pageId + '').removeClass('qa-appr-open');
	$('#apprRejectBtn_' + pageId + '').removeClass('qa-appr-arming');
	$('#apprRejectTxt_' + pageId + '').text('驳回');
	apprRefreshState();
}

// 驳回按钮：第一次点击展开原因区，第二次点击校验并提交
function onRejectClick() {
	if (submitting) return;
	var ids = apprCheckedIds();
	if (ids.length === 0) {
		LayerMsgBox.alert('请先勾选要驳回的产品', 2);
		return;
	}
	if (!rejectArmed) {
		rejectArmed = true;
		$('#apprRejectBox_' + pageId + '').addClass('qa-appr-open');
		$('#apprRejectBtn_' + pageId + '').addClass('qa-appr-arming');
		$('#apprRejectTxt_' + pageId + '').text('确认驳回');
		apprRefreshState();
		$('#apprRejectDes_' + pageId + '').focus();
		return;
	}
	var reason = $.trim($('#apprRejectDes_' + pageId + '').val());
	if (!reason) {
		LayerMsgBox.alert('请输入驳回原因', 2);
		$('#apprRejectDes_' + pageId + '').focus();
		return;
	}
	apprSubmit('admin/siargo/qarep/batchReject',
		{ ids: ids.join(','), rejectDes: reason },
		'#apprRejectBtn_' + pageId + '');
}

// 通过按钮：提交 batchInspection 推进到下一阶段
function onPassClick() {
	if (submitting || rejectArmed) return;
	var ids = apprCheckedIds();
	if (ids.length === 0) {
		LayerMsgBox.alert('请先勾选要审批的产品', 2);
		return;
	}
	apprSubmit('admin/siargo/qarep/batchInspection',
		{ insp: Number(page.attr("data-insp")), ids: ids.join(',') },
		'#apprPassBtn_' + pageId + '');
}

// 统一提交：成功 → 刷新父页表格并关闭抽屉；失败 → 恢复按钮
function apprSubmit(url, data, btnSel) {
	submitting = true;
	var $btn = $(btnSel);
	var oldHtml = $btn.html();
	$('#apprPassBtn_' + pageId + ', #apprRejectBtn_' + pageId + '').prop('disabled', true);
	$btn.html('<i class="fa fa-spinner fa-spin"></i> 提交中...');
	$.ajax({
		url: url,
		type: 'post',
		data: data,
		success: function(res) {
			if (res.state == 'ok') {
				// 跨iframe通知父页刷新（兜底：父页未注册时降级为关闭弹窗）
				try {
					if (parent && parent.window._qarepRefresh) { parent.window._qarepRefresh(); }
				} catch(e) { console.warn('_qarepRefresh调用失败，将关闭弹窗', e); }
				LayerMsgBox.success(res.msg || '操作成功', 600, function(){
					if (parent && parent.JBoltLayerUtil) { parent.JBoltLayerUtil.close(true); }
				});
			} else {
				submitting = false;
				$btn.html(oldHtml);
				apprRefreshState();
				LayerMsgBox.alert(res.msg || '操作失败', 2);
			}
		},
		error: function() {
			submitting = false;
			$btn.html(oldHtml);
			apprRefreshState();
			LayerMsgBox.error('请求失败，请重试');
		}
	});
}

$(function() {
	// 头部汇总：N = 产品行数，M = 报告单卡片数（按 formnum 分组后）
	$('#apprTotalN_' + pageId + '').text($('#apprBody_' + pageId + ' .qa-appr-ck').length);
	$('#apprTotalM_' + pageId + '').text($('#apprBody_' + pageId + ' .qa-appr-card').length);

	// 单个产品勾选：切换行选中态竖条
	$('#apprBody_' + pageId + '').on('change', '.qa-appr-ck', function() {
		$(this).closest('.qa-appr-prod').toggleClass('qa-appr-prod-on', this.checked);
		apprRefreshState();
	});

	// 全选/全不选
	$('#apprCheckAll_' + pageId + '').on('change', function() {
		var on = this.checked;
		$('#apprBody_' + pageId + ' .qa-appr-ck').each(function() {
			this.checked = on;
			$(this).closest('.qa-appr-prod').toggleClass('qa-appr-prod-on', on);
		});
		apprRefreshState();
	});

	apprRefreshState();
});

window['cancelReject_'+pageId]=cancelReject;
window['onRejectClick_'+pageId]=onRejectClick;
window['onPassClick_'+pageId]=onPassClick;
},close:function(container){root.jQuery(container).find("*").off();}};}(window));

/* ===== 报告统计：报告单模板分类图表 ===== */
(function(root){
    function escape(value){return String(value==null?'':value).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}
    function color(sn){
        var value=String(sn==null?'':sn),hash=0;
        var templateColors={large_meter:'#e11d48',small_flow:'#14b8a6',sensor:'#3b82f6'};
        if(Object.prototype.hasOwnProperty.call(templateColors,value))return templateColors[value];
        for(var i=0;i<value.length;i++)hash=(hash*31+value.charCodeAt(i))>>>0;
        return 'hsl('+(hash*137.508%360).toFixed(1)+', 52%, 47%)';
    }
    function monthly(data){return (data.series||[]).map(function(item){return {id:String(item.sn),name:item.name,type:'bar',data:item.data||[],barMaxWidth:22,itemStyle:{color:color(item.sn),borderRadius:[5,5,0,0]}};});}
    function quarter(data){
        var items=data.series||[],series=[];
        ['previous','current'].forEach(function(period){items.forEach(function(item,index){series.push({id:period+'_'+item.sn,name:item.name,type:'bar',stack:period,data:item[period]||[0,0,0,0],barMaxWidth:20,itemStyle:{color:color(item.sn),opacity:period==='previous'?.38:1,borderRadius:index===items.length-1?[5,5,0,0]:[0,0,0,0]}});});});
        return series;
    }
    root.SiargoQarepCharts={color:color,monthly:monthly,quarter:quarter,escape:escape};
    root.SiargoDashboard={init:function(container,config){
        var page=root.jQuery(container),pageId=String(page.attr('data-page-id'));
        if(page.data('qarepDashboard'))return;

var dashboard = {
	charts: {},
	resizeObserver: null,
	flow: config.flowCounts,
	barData: config.repalldata,
	lineData: config.repfixdata,
	donutData: config.donutData,
	quarterData: config.quarterData,
	// 模板分类语义色（与柱状图 series 保持一致）
	prodColor: root.SiargoQarepCharts.color,

	init: function () {
		$('#dbYear_' + pageId + '').text(new Date().getFullYear() + ' 年度');
		this.initMetaTime();
		this.initStack();
		this.initBarChart();
		this.initQuarterChart();
		this.initLineChart();
		this.initDonutChart();
		this.observeResize();
	},

	// ---- 刷新时间 ----
	initMetaTime: function () {
		var _d = new Date(), _p = function (n) { return n < 10 ? '0' + n : '' + n; };
		var _t = _p(_d.getHours()) + ':' + _p(_d.getMinutes()) + ':' + _p(_d.getSeconds());
		$('#dbMetaTime_' + pageId + '').text(_t);
	},

	// ---- 统计模块 + 环节堆叠占比：待处理/已完成大数字、5 环节堆叠段 + 数字滚动 + hover 联动 ----
	initStack: function () {
		var flow = this.flow || {};
		var pending = (Number(flow.noq) || 0) + (Number(flow.ltq) || 0) + (Number(flow.accq) || 0) + (Number(flow.funq) || 0) + (Number(flow.appq) || 0);
		var doneCnt = Number(flow.allq) || 0;
		// 统计模块大数字（待处理直接显示，已完成千分位）
		$('#dbPend_' + pageId + '').text(pending.toLocaleString());
		var $doneNum = $('.db-stat-num-done');
		$doneNum.text(doneCnt.toLocaleString());
		// 堆叠段宽 = 在检单数占 5 环节总量比例
		var $segs = $('.db-stack-seg');
		var total = 0;
		$segs.each(function () { total += (Number($(this).attr('data-count')) || 0); });
		$segs.each(function () {
			var count = Number($(this).attr('data-count')) || 0;
			var pct = total > 0 ? (count * 100 / total) : 0;
			$(this).css('flex-grow', pct.toFixed(3));
			$(this).css('flex-basis', '0%');
			$(this).css('min-width', count > 0 ? '6px' : '0');
		});
		// 数字滚动（600ms ease-out）+ 送检千分位
		$('.db-stack-col').each(function () {
			var $col = $(this);
			var count = Number($col.find('.db-stack-num').attr('data-count')) || 0;
			var $num = $col.find('.db-stack-num');
			var start = null;
			function step(ts) {
				if (!start) start = ts;
				var p = Math.min((ts - start) / 600, 1);
				var eased = 1 - Math.pow(1 - p, 3);
				$num.text(Math.round(count * eased).toLocaleString());
				if (p < 1) requestAnimationFrame(step);
			}
			requestAnimationFrame(step);
			var $qsi = $col.find('[data-qsi]');
			$qsi.text((Number($qsi.attr('data-qsi')) || 0).toLocaleString());
		});
		// 段 ↔ 列 hover 联动（按索引对应）
		var self = this;
		$segs.each(function (i) {
			$(this).on('mouseenter', function () { $('.db-stack-col').eq(i).addClass('db-stack-col-hot'); });
			$(this).on('mouseleave', function () { $('.db-stack-col').eq(i).removeClass('db-stack-col-hot'); });
		});
		$('.db-stack-col').each(function (i) {
			$(this).on('mouseenter', function () { $segs.eq(i).addClass('db-stack-seg-hot'); });
			$(this).on('mouseleave', function () { $segs.eq(i).removeClass('db-stack-seg-hot'); });
		});
	},

	// ---- 通用：初始化/重建 ECharts 实例 ----
	buildChart: function (key, domId, option) {
		var dom = document.getElementById(domId);
		if (!dom) return;
		var old = echarts.getInstanceByDom(dom);
		if (old) old.dispose();
		var chart = echarts.init(dom);
		chart.setOption(option);
		this.charts[key] = chart;
	},

	// ---- 月度送检柱状图 ----
    initBarChart: function () {
        var data = this.barData || {};
        this.buildChart('bar', 'dbBar_' + pageId, {
            grid: {left:8,right:8,top:40,bottom:0,containLabel:true},
            tooltip: {trigger:'axis',axisPointer:{type:'shadow'}},
            legend: {top:0},
            xAxis: {type:'category',data:(data.months || []).map(function(m){return m+'月';})},
            yAxis: {type:'value'},
            series: root.SiargoQarepCharts.monthly(data)
        });
    },

	// ---- 季度送检同比柱状图（今年 vs 去年，按模板分类堆叠） ----
    initQuarterChart: function () {
        var data = this.quarterData || {};
        this.buildChart('quarter', 'dbQuarter_' + pageId, {
            grid: {left:8,right:8,top:44,bottom:0,containLabel:true},
            tooltip: {trigger:'axis',axisPointer:{type:'shadow'},formatter:function(params){
                if (!params.length) return '';
                var rows = {}, order = [], escape = root.SiargoQarepCharts.escape;
                params.forEach(function(p){
                    var previous = p.seriesId.indexOf('previous_') === 0;
                    var key = p.seriesId.replace(/^(previous|current)_/, '');
                    if (!rows[key]) {
                        rows[key] = {name:p.seriesName,marker:p.marker,previous:0,current:0};
                        order.push(key);
                    }
                    rows[key][previous ? 'previous' : 'current'] = Number(p.value) || 0;
                    if (!previous) rows[key].marker = p.marker;
                });
                var html = escape(params[0].name) + '（' + data.lastYear + ' 年 → ' + data.curYear + ' 年）<br/>';
                order.forEach(function(key){
                    var row = rows[key], change;
                    if (row.current === row.previous) {
                        change = '同比持平';
                    } else if (row.previous === 0) {
                        change = '新增';
                    } else {
                        var percent = Math.abs(row.current - row.previous) * 100 / row.previous;
                        change = (row.current > row.previous ? '同比增长 ' : '同比降低 ')
                            + (percent < 0.1 ? '不足0.1%' : Number(percent.toFixed(1)) + '%');
                    }
                    html += row.marker + escape(row.name) + '：' + row.previous.toLocaleString()
                        + ' → <b>' + row.current.toLocaleString() + '</b> 只（' + change + '）<br/>';
                });
                return html;
            }},
            legend: {top:0},
            xAxis: {type:'category',data:['Q1','Q2','Q3','Q4']},
            yAxis: {type:'value'},
            series: root.SiargoQarepCharts.quarter(data)
        });
    },

	// ---- 退修趋势折线图（今年/去年双线，legend 切换） ----
	initLineChart: function () {
		var qd = this.lineData || {};
		var months = [];
		for (var m = 1; m <= 12; m++) months.push(m + '月');
		var curName = (qd.curYear || new Date().getFullYear()) + ' 年';
		var lastName = (qd.lastYear || new Date().getFullYear() - 1) + ' 年';
		this.buildChart('line', 'dbLine_' + pageId + '', {
			grid: { left: 8, right: 16, top: 34, bottom: 0, containLabel: true },
			tooltip: {
				trigger: 'axis',
				formatter: function (params) {
					var html = params[0].name + '<br/>';
					params.forEach(function (p) {
						html += p.marker + p.seriesName + '：<b>' + (p.value || 0).toLocaleString() + '</b> 只<br/>';
					});
					return html;
				}
			},
			legend: { right: 0, top: 0, itemWidth: 12, itemHeight: 8, icon: 'roundRect', textStyle: { color: '#64748b', fontSize: 12 } },
			xAxis: { type: 'category', boundaryGap: false, data: months, axisTick: { show: false }, axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8' } },
			yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f0f2f5' } }, axisLabel: { color: '#94a3b8' } },
			series: [
				{
					name: lastName, type: 'line', data: qd.last || [], smooth: true,
					symbol: 'circle', symbolSize: 7, showSymbol: false,
					lineStyle: { width: 2.5, color: '#94a3b8' },
					itemStyle: { color: '#94a3b8', borderColor: '#fff', borderWidth: 2 },
					// 去年：灰色实线；hover 保持原样式，避免鼠标移入时线型/粗细变化累眼
					emphasis: {
						lineStyle: { width: 2.5, color: '#94a3b8' },
						itemStyle: { color: '#94a3b8', borderColor: '#fff', borderWidth: 2 }
					}
				},
				{
					name: curName, type: 'line', data: qd.cur || [], smooth: true,
					symbol: 'circle', symbolSize: 7, showSymbol: false,
					lineStyle: { width: 3, color: '#e71d36' },
					itemStyle: { color: '#e71d36', borderColor: '#fff', borderWidth: 2 },
					areaStyle: {
						color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{ offset: 0, color: 'rgba(231,29,54,.22)' },
							{ offset: 1, color: 'rgba(231,29,54,0)' }
						])
					}
				}
			]
		});
	},

	// ---- 模板分类占比环形图 ----
	initDonutChart: function () {
		var self = this;
		var total = 0;
		var data = (this.donutData || []).map(function (d) {
			var value = Number(d.value) || 0;
			total += value;
			return { name: d.label, value: value, itemStyle: { color: self.prodColor(d.sn) } };
		});
		this.buildChart('donut', 'dbDonut_' + pageId + '', {
			tooltip: { trigger: 'item', formatter: '{b}：<b>{c}</b> 单（{d}%）' },
			legend: { bottom: 0, itemWidth: 12, itemHeight: 8, icon: 'roundRect', textStyle: { color: '#64748b', fontSize: 12 } },
			title: {
				text: total.toLocaleString(), subtext: '报告单',
				left: 'center', top: '38%',
				textStyle: { fontSize: 22, fontWeight: 700, color: '#1e293b' },
				subtextStyle: { fontSize: 11, color: '#94a3b8' }
			},
			series: [{
				type: 'pie', radius: ['56%', '78%'], center: ['50%', '46%'],
				avoidLabelOverlap: true,
				itemStyle: { borderColor: '#fff', borderWidth: 3, borderRadius: 5 },
				label: { show: false },
				emphasis: { scaleSize: 6 },
				data: data
			}]
		});
	},

	// ---- 尺寸自适应（含全屏切换、窗口缩放） ----
	observeResize: function () {
		var self = this;
		if (this.resizeObserver) { this.resizeObserver.disconnect(); this.resizeObserver = null; }
		if (window.ResizeObserver) {
			this.resizeObserver = new ResizeObserver(function () {
				Object.keys(self.charts).forEach(function (k) {
					var c = self.charts[k];
					if (c && !c.isDisposed()) c.resize();
				});
			});
			['dbBar_' + pageId + '', 'dbQuarter_' + pageId + '', 'dbLine_' + pageId + '', 'dbDonut_' + pageId + ''].forEach(function (id) {
				var dom = document.getElementById(id);
				if (dom) self.resizeObserver.observe(dom);
			});
		} else {
			$(window).off('resize.dashboard_' + pageId).on('resize.dashboard_' + pageId, function () {
				Object.keys(self.charts).forEach(function (k) {
					var c = self.charts[k];
					if (c && !c.isDisposed()) c.resize();
				});
			});
		}
	}
};

function initialize() {
	dashboard.init();
}

// 框架标签页切换自动刷新：切回本页（数据分析）时整页刷新一次，获取最新统计数据
// 复用平台自身的 tab 点击事件委托（与平台切换逻辑同源）：
// - 捕获阶段监听（先于平台在 jboltBody 上的冒泡 handler 执行），此时能读到 tab 点击前的激活状态
// - 仅"从其他标签页切回本页"（点击的 tab 非当前激活，且其 key 就是本页所在 tabcontent 的 key）时刷新一次
// - 非 data-auto-refresh 周期机制，无周期轮询；页面整页刷新后重新执行脚本时先移除旧监听，避免累积泄漏
(function(){
	if (window.__dbTabRefreshCapture) {
		document.removeEventListener('click', window.__dbTabRefreshCapture, true);
	}
	var handler = function(e) {
		var t = e.target;
		var li = t && t.closest ? t.closest('ul.jbolt_tabs>li') : null;
		if (!li || li.classList.contains('active')) return; // 点击的是当前激活 tab，跳过
		if (t.closest && t.closest('i.close')) return; // 点击关闭按钮不触发刷新
		var clickedKey = li.getAttribute('data-key');
		setTimeout(function() {
			var portal = $('#dbPend_' + pageId + '').closest('.jbolt_tabcontent');
			// portal 仍挂载、本页已激活、且其 tab key 与点击的 tab 一致 → 确实切回了本页，刷新一次
			if (portal.length === 1 && portal[0].isConnected && portal.hasClass('active') && portal.attr('data-key') === clickedKey) {
				portal.ajaxPortal(true);
			}
		}, 80);
	};
	window.__dbTabRefreshCapture = handler;
	document.addEventListener('click', handler, true);
})();

page.data("qarepDashboard",dashboard);
initialize();

    },close:function(container){
        var page=root.jQuery(container),dashboard=page.data('qarepDashboard');
        if(dashboard){if(dashboard.resizeObserver)dashboard.resizeObserver.disconnect();Object.keys(dashboard.charts).forEach(function(key){dashboard.charts[key].dispose();});}
        root.jQuery(root).off('resize.dashboard_'+page.attr('data-page-id'));
        if(root.__dbTabRefreshCapture){document.removeEventListener('click',root.__dbTabRefreshCapture,true);delete root.__dbTabRefreshCapture;}
        page.removeData('qarepDashboard');
    }};
}(window));

/* ===== 报告单模板文件与系列关联管理 ===== */
(function(root){root.SiargoPdfTemplates={init:function(container){var page=root.jQuery(container),pageId=String(page.attr("data-page-id"));if(page.data("pdfTemplatesReady"))return;page.data("pdfTemplatesReady",true);



var BASE = 'admin/siargo/qarep/pdffolder/';
var selectedVer = null;

// ========== 左侧文件夹列表 ==========
function showFolderError(message) {
	$('#pfFolderList_'+pageId).html('<div class="pf-empty">'
		+ root.SiargoQarepCharts.escape(message || '版号加载失败，请重试')
		+ '<br><button type="button" class="btn btn-outline-secondary btn-xs" data-retry-folders>重新加载</button></div>');
}
page.on('click.pdfRules', '[data-retry-folders]', function() { window['pfLoadFolders_'+pageId](true); });
window['pfLoadFolders_'+pageId] = function(refreshCurrent) {
	$('#pfFolderList_'+pageId).html('<div class="pf-empty">加载中...</div>');
	$.ajax({
		url: actionUrl(BASE + 'folders'), type: 'get', dataType: 'json', cache: false, timeout: 15000,
		success: function(res) {
			if (!res || res.state !== 'ok' || !Array.isArray(res.data)) {
				showFolderError(res && res.msg);
				return;
			}
			var list = res.data;
			if (selectedVer && !list.some(function(folder) { return folder.pdfver === selectedVer; })) {
				selectedVer = null;
				$('#pfCurrentVer_'+pageId).text('请选择左侧版号');
				$('#pfFileTable_'+pageId).html('<tr><td colspan="4" class="pf-empty">请先选择版号</td></tr>');
				page.find('[data-rule-version]').val('');
				refreshJBoltTableById('pfRules_'+pageId);
			}
			var html = '';
			if (list.length === 0) {
				html = '<div class="pf-empty">暂无文件夹，请点击"新增"创建</div>';
			} else {
				// 按字典名称分组：pdfver 格式如 "G/2" → 主版号"G"，子版号"2"。
				var groups = {};
				var groupOrder = [];
				for (var i = 0; i < list.length; i++) {
					var f = list[i];
					var parts = f.pdfver.split('/');
					var major = parts.length > 1 ? parts[0] : f.pdfver;
					var sub = parts.length > 1 ? parts[1] : f.pdfver;
					if (!groups[major]) {
						groups[major] = [];
						groupOrder.push(major);
					}
					groups[major].push({sub: sub, pdfver: f.pdfver, description: f.description || ''});
				}
				// 渲染树形结构
				for (var g = 0; g < groupOrder.length; g++) {
					var major = groupOrder[g];
					var children = groups[major];
					// 组头
					html += '<div class="pf-group-header" onclick="pfToggleGroup_'+pageId+'(this)">';
					html += '<i class="fa fa-caret-down"></i> <i class="fa fa-folder-o"></i> ' + root.SiargoQarepCharts.escape(major);
					html += '</div>';
					html += '<div class="pf-group-children">';
					for (var c = 0; c < children.length; c++) {
						var item = children[c];
						var cls = (selectedVer === item.pdfver) ? ' active' : '';
						html += '<div class="pf-sub-item' + cls + '" data-ver="' + root.SiargoQarepCharts.escape(item.pdfver) + '" onclick="pfSelectFolder_'+pageId+'(this)">';
						html += '<i class="fa fa-file-o"></i> ' + root.SiargoQarepCharts.escape(item.sub);
						if (item.description) html += ' <span style="color:#aaa;font-size:11px;">' + root.SiargoQarepCharts.escape(item.description) + '</span>';
						html += '</div>';
					}
					html += '</div>';
				}
			}
			$('#pfFolderList_'+pageId).html(html);
			if (refreshCurrent && selectedVer) {
				var activeTab = $('#pfTabLinks_'+pageId+' a.active').data('tab');
				window[(activeTab === 'rules' ? 'pfLoadRules_' : 'pfLoadFiles_')+pageId]();
			}
		},
		error: function() { showFolderError('版号加载失败，请检查网络后重试'); }
	});
};

window['pfToggleGroup_'+pageId] = function(el) {
	$(el).toggleClass('collapsed');
	$(el).next('.pf-group-children').toggleClass('collapsed');
};

window['pfSelectFolder_'+pageId] = function(el) {
	$('#pfFolderList_'+pageId+' .pf-sub-item').removeClass('active');
	$(el).addClass('active');
	selectedVer = $(el).attr('data-ver');
	$('#pfCurrentVer_'+pageId).html('版号：<strong>' + root.SiargoQarepCharts.escape(selectedVer) + '</strong>');
	// 加载当前Tab数据
	var activeTab = $('#pfTabLinks_'+pageId+' a.active').data('tab');
	if (activeTab === 'files') {
		window['pfLoadFiles_'+pageId]();
	} else {
		window['pfLoadRules_'+pageId]();
	}
};

// ========== 新增文件夹 ==========
window['pfShowAddFolder_'+pageId] = function() {
	$.ajax({
		url: BASE + 'dictVersions', type: 'get', dataType: 'json',
		success: function(res) {
			var list = res.data || [];
			var options = '';
			var hasAvailable = false;
			for (var i = 0; i < list.length; i++) {
				if (!list[i].created) {
					hasAvailable = true;
					options += '<option value="' + list[i].dict_id + '">' + root.SiargoQarepCharts.escape(list[i].name) + '</option>';
				}
			}
			if (!hasAvailable) {
				layer.msg('所有字典版号已创建', {icon: 0});
				return;
			}
			var content = '<div style="padding:20px;">'
				+ '<div class="form-group"><label>选择要创建的版号：</label>'
				+ '<select class="form-control" id="pfNewFolderSelect_'+pageId+'">' + options + '</select></div>'
				+ '</div>';
			layer.open({
				type: 1, title: '新增版号文件夹', area: ['400px','220px'],
				content: content,
				btn: ['确定','取消'],
				yes: function(idx) {
					var dictId = $('#pfNewFolderSelect_'+pageId).val();
					if (!dictId) { layer.msg('请选择版号',{icon:0}); return; }
					$.post(BASE + 'createFolder', {dictId: dictId}, function(ret) {
						if (ret.state === 'ok') {
							layer.close(idx);
							layer.msg('创建成功', {icon: 1});
							window['pfLoadFolders_'+pageId]();
						} else {
							layer.msg(ret.msg || '创建失败', {icon: 2});
						}
					}, 'json');
				}
			});
		}
	});
};

// ========== 删除文件夹 ==========
window['pfDeleteFolder_'+pageId] = function() {
	if (!selectedVer) { layer.msg('请先选中一个文件夹', {icon: 0}); return; }
	layer.confirm('确定删除版号 [' + root.SiargoQarepCharts.escape(selectedVer) + '] 的文件夹？<br><small class="text-danger">请先移除该版号下的模板关联及文件</small>', {
		icon: 3, title: '确认删除'
	}, function(idx) {
		$.post(BASE + 'deleteFolder', {pdfver: selectedVer}, function(ret) {
			if (ret.state === 'ok') {
				layer.close(idx);
				layer.msg('已删除', {icon: 1});
				selectedVer = null;
				$('#pfCurrentVer_'+pageId).text('请选择左侧版号');
				$('#pfFileTable_'+pageId).html('<tr><td colspan="4" class="pf-empty">请先选择版号</td></tr>');
				page.find('[data-rule-version]').val('');refreshJBoltTableById('pfRules_'+pageId);
				window['pfLoadFolders_'+pageId]();
			} else {
				layer.msg(ret.msg || '删除失败', {icon: 2});
			}
		}, 'json');
	});
};

// ========== Tab 切换 ==========
window['pfSwitchTab_'+pageId] = function(el, tab) {
	$('#pfTabLinks_'+pageId+' a').removeClass('active');
	$(el).addClass('active');
	$('#pfTabFiles_'+pageId+',#pfTabRules_'+pageId).removeClass('active');
	if (tab === 'files') {
		$('#pfTabFiles_'+pageId).addClass('active');
		if (selectedVer) window['pfLoadFiles_'+pageId]();
	} else {
		$('#pfTabRules_'+pageId).addClass('active');
		// 隐藏 Tab 显示后，按当前右栏宽度和容器高度重算表格。
		var rulesTable = $('#pfRules_'+pageId);
		var rulesInstance = rulesTable.jboltTable ? rulesTable.jboltTable('inst') : null;
		if (rulesInstance) {
			rulesInstance.me.processTableColWidthAfterResize(rulesInstance);
			rulesInstance.me.resize(rulesInstance);
		}
		if (selectedVer) window['pfLoadRules_'+pageId]();
	}
};

// ========== 模板文件列表 ==========
window['pfLoadFiles_'+pageId] = function() {
	if (!selectedVer) return;
	var requestedVer = selectedVer;
	function showFileError(message) {
		if (selectedVer !== requestedVer) return;
		$('#pfFileTable_'+pageId).html('<tr><td colspan="4" class="pf-empty">'
			+ root.SiargoQarepCharts.escape(message || '模板文件加载失败，请刷新重试') + '</td></tr>');
	}
	$.ajax({
		url: actionUrl(BASE + 'templates'), type: 'get', data: {pdfver: requestedVer}, dataType: 'json', cache: false, timeout: 15000,
		success: function(res) {
			if (selectedVer !== requestedVer) return;
			if (!res || res.state !== 'ok' || !Array.isArray(res.data)) {
				showFileError(res && res.msg);
				return;
			}
			var list = res.data;
			if (list.length === 0) {
				$('#pfFileTable_'+pageId).html('<tr><td colspan="4" class="pf-empty">暂无模板文件</td></tr>');
				return;
			}
			var html = '';
			for (var i = 0; i < list.length; i++) {
				var f = list[i];
				var sizeKB = (f.fileSize / 1024).toFixed(1) + ' KB';
				html += '<tr class="pf-file-row"><td>' + (i+1) + '</td>';
				html += '<td><i class="fa fa-file-pdf-o text-danger"></i> ' + root.SiargoQarepCharts.escape(f.fileName) + '</td>';
				html += '<td>' + sizeKB + '</td>';
				html += '<td><a href="javascript:;" class="c-danger" onclick="pfDeleteFile_'+pageId+'(this)" data-fname="' + root.SiargoQarepCharts.escape(f.fileName) + '"><i class="fa fa-trash"></i></a></td>';
				html += '</tr>';
			}
			$('#pfFileTable_'+pageId).html(html);
		},
		error: function() { showFileError(); }
	});
};

// ========== 上传模板文件 ==========
window['pfUploadFile_'+pageId] = function() {
	if (!selectedVer) { layer.msg('请先选择版号', {icon: 0}); return; }
	$('#pfFileInput_'+pageId).click();
};

window['pfDoUpload_'+pageId] = function(input) {
	if (!input.files || !input.files[0]) return;
	var formData = new FormData();
	formData.append('file', input.files[0]);
	formData.append('pdfver', selectedVer);
	var loadIdx = layer.load(1, {shade: [0.3, '#000']});
	$.ajax({
		url: BASE + 'upload', type: 'post', data: formData,
		processData: false, contentType: false, dataType: 'json',
		success: function(ret) {
			layer.close(loadIdx);
			if (ret.state === 'ok') {
				layer.msg('上传成功', {icon: 1});
				window['pfLoadFiles_'+pageId]();
			} else {
				layer.msg(ret.msg || '上传失败', {icon: 2});
			}
		},
		error: function() { layer.close(loadIdx); layer.msg('上传异常', {icon: 2}); }
	});
	input.value = '';
};

// ========== 删除模板文件 ==========
window['pfDeleteFile_'+pageId] = function(el) {
	var fileName = $(el).data('fname');
	layer.confirm('确定删除文件 [' + fileName + ']？', {icon: 3, title: '确认'}, function(idx) {
		$.post(BASE + 'deleteFile', {pdfver: selectedVer, fileName: fileName}, function(ret) {
			if (ret.state === 'ok') {
				layer.close(idx);
				layer.msg('已删除', {icon: 1});
				window['pfLoadFiles_'+pageId]();
			} else {
				layer.msg(ret.msg || '删除失败', {icon: 2});
			}
		}, 'json');
	});
};

// ========== 模板与系列关联 ==========
window['pfLoadRules_'+pageId] = function() {
    if (!selectedVer) return;
    page.find('[data-rule-version]').val(selectedVer);
    refreshJBoltTableById('pfRules_'+pageId);
};
window['pfAddRule_'+pageId] = function() { openRule(null); };
function requestRule(action, payload, success, complete) {
    $.ajax({
        url: actionUrl(BASE + action), type: 'post', dataType: 'json', data: payload, timeout: 60000,
        success: function(ret) {
            if (complete) complete();
            if (ret && ret.state === 'ok') success(ret);
            else LayerMsgBox.alert(ret && ret.msg ? ret.msg : '模板操作失败', 2);
        },
        error: function(xhr, status) {
            if (complete) complete();
            var message = status === 'timeout' ? '请求超时，请刷新列表确认操作结果后再重试' : '网络通讯异常，请稍后重试';
            LayerMsgBox.alert(xhr.responseJSON && xhr.responseJSON.msg ? xhr.responseJSON.msg : message, 2);
        }
    });
}
function openRule(data) {
    if (!selectedVer) { LayerMsgBox.alert('请先选择版号',2); return; }
    data=data||{};
    var ver=selectedVer;
    var esc=root.SiargoQarepCharts.escape;
    var modelIds=Array.isArray(data.modelids)?data.modelids.map(String):[];
    var formId='pfRuleForm_'+pageId;
    var originalNames={};
    modelIds.forEach(function(id,index){originalNames[id]=(data.modelseries||[])[index]||id;});
    var content='<form id="'+formId+'" class="pf-rule-form" onsubmit="return false;">'
        +'<input type="hidden" name="rule.id" value="'+esc(data.id||'')+'">'
        +'<input type="hidden" name="rule.pdfver" value="'+esc(ver)+'">'
        +'<div class="pf-rule-fields"><div class="form-group"><label for="'+formId+'_file">模板文件</label><select id="'+formId+'_file" class="form-control" name="rule.template_file" data-autoload data-rule="select" data-tips="请选择模板文件" data-text="请选择已上传的模板" data-value-attr="fileName" data-text-attr="fileName" data-select="'+esc(data.template_file||'')+'" data-url="'+BASE+'templates?pdfver='+encodeURIComponent(ver)+'"></select></div>'
        +'<div class="form-group"><label for="'+formId+'_active">状态</label><select id="'+formId+'_active" class="form-control" name="rule.is_active"><option value="1">启用</option><option value="0">停用</option></select></div></div>'
        +'<div class="form-group"><label for="'+formId+'_hint">说明</label><input id="'+formId+'_hint" class="form-control" name="rule.error_hint" maxlength="50" data-rule="len<=50" data-notnull="false" data-tips="说明不能超过50字" value="'+esc(data.error_hint||'')+'" placeholder="选填，最多50字"></div>'
        +'<select multiple hidden data-model-ids>'+modelIds.map(function(id){return '<option selected value="'+esc(id)+'">'+esc(originalNames[id])+'</option>';}).join('')+'</select>'
        +'<section class="pf-series-picker" aria-label="关联系列"><div class="pf-series-heading"><strong>关联系列</strong><span>勾选即可关联，支持搜索后批量选择</span></div>'
        +'<div class="pf-series-panels"><div class="pf-series-panel"><div class="pf-series-search"><input type="search" class="form-control" data-series-search aria-label="搜索系列或分支名称" placeholder="搜索系列或分支名称，如 FS7002" autocomplete="off"></div>'
        +'<div class="pf-series-toolbar"><span data-series-match-count></span><div><button type="button" class="btn btn-link btn-sm" data-series-batch="select">全选搜索结果</button></div></div>'
        +'<div class="pf-series-options" data-series-options></div><div class="pf-series-load-error" data-series-error hidden><span data-series-error-message></span><button type="button" class="btn btn-outline-secondary btn-sm" data-series-retry>重新加载</button></div></div>'
        +'<div class="pf-series-panel pf-series-selected-panel"><div class="pf-series-toolbar"><strong>已选 <span data-series-selected-count aria-live="polite">'+modelIds.length+'</span> 项</strong><button type="button" class="btn btn-link btn-sm" data-series-clear>清空</button></div><div class="pf-series-selected" data-series-selected></div></div></div></section>'
        +'<p class="pf-series-help">未选择系列的模板可保存，关联后才能用于报告单生成。</p></form>';
    var saving=false, disposed=false, seriesLoading=false, seriesRequest, form, dialog, modelSelect;
    var seriesOptions=modelIds.map(function(id){return {id:id,name:originalNames[id]};}), visibleSeries=[];
    function updateSeriesSelection() {
        var selected=new Set((modelSelect.val()||[]).map(String));
        var unavailable=saving||!Array.isArray(modelSelect.data('option-datas'));
        form.find('[data-series-option]').prop('disabled',unavailable);
        form.find('[data-series-selected-count]').text(selected.size);
        form.find('[data-series-selected]').html(seriesOptions.filter(function(item){return selected.has(item.id);}).map(function(item){
            return '<div class="pf-series-selected-item"><span>'+esc(item.name)+(item.unavailable?'<small>当前不可用，原关联已保留</small>':'')+'</span><button type="button" class="pf-series-remove" data-series-remove="'+esc(item.id)+'" aria-label="移除 '+esc(item.name)+'"'+(unavailable?' disabled':'')+'>&times;</button></div>';
        }).join('')||'<div class="pf-series-empty">暂无已选系列<br>在左侧勾选即可添加</div>');
        form.find('[data-series-search]').prop('disabled',unavailable);
        form.find('[data-series-clear]').prop('disabled',unavailable||!selected.size);
        form.find('[data-series-batch="select"]').prop('disabled',unavailable||!visibleSeries.length);
    }
    function renderSeriesOptions() {
        var keyword=String(form.find('[data-series-search]').val()||'').trim().toLowerCase();
        var selected=new Set((modelSelect.val()||[]).map(String));
        visibleSeries=seriesOptions.filter(function(item){return !item.unavailable&&!selected.has(item.id)&&item.name.toLowerCase().indexOf(keyword)!==-1;});
        form.find('[data-series-match-count]').text((keyword?'匹配 ':'共 ')+visibleSeries.length+' 项');
        form.find('[data-series-options]').html(visibleSeries.map(function(item){
            return '<label class="pf-series-option"><input type="checkbox" data-series-option value="'+esc(item.id)+'"><span>'+esc(item.name)+'</span></label>';
        }).join('')||'<div class="pf-series-empty">'+(keyword?'没有匹配的系列，请更换关键词':'暂无可关联的系列')+'</div>');
        updateSeriesSelection();
    }
    function loadSeriesOptions() {
        if(disposed||seriesLoading||saving)return;
        seriesLoading=true;
        modelSelect.data('option-datas',null);
        form.find('[data-series-error]').prop('hidden',true);
        form.find('[data-series-options]').html('<div class="pf-series-empty" role="status">正在加载系列...</div>');
        form.find('[data-series-match-count]').text('');
        updateSeriesSelection();
        function fail(message) {
            form.find('[data-series-options]').html('');
            form.find('[data-series-error-message]').text(message);
            form.find('[data-series-error]').prop('hidden',false);
        }
        seriesRequest=$.ajax({
            url:actionUrl(BASE+'seriesOptions'),type:'get',dataType:'json',cache:false,timeout:15000,data:{pdfver:ver,templateId:data.id||'',selectedIds:modelIds.join(',')},
            success:function(ret){
                if(disposed)return;
                if(!ret||ret.state!=='ok'||!Array.isArray(ret.data)){
                    fail(ret&&ret.msg?ret.msg:'系列加载失败，请重新加载');return;
                }
                if(ret.data.some(function(item){return !item||typeof item.id!=='string'||!item.id||typeof item.name!=='string';})){
                    fail('系列数据格式异常，请重新加载');return;
                }
                var selected=modelSelect.val()||[], known=new Set();
                seriesOptions=ret.data.map(function(item){known.add(item.id);return {id:item.id,name:item.name};});
                selected.forEach(function(id){if(!known.has(id))seriesOptions.push({id:id,name:originalNames[id]||id,unavailable:true});});
                modelSelect.html(seriesOptions.map(function(item){return '<option value="'+esc(item.id)+'">'+esc(item.name)+'</option>';}).join('')).val(selected).data('option-datas',ret.data);
                renderSeriesOptions();
            },
            error:function(xhr,status){if(!disposed)fail(status==='timeout'?'系列加载超时，请重新加载':'系列加载失败，请检查网络后重试');},
            complete:function(){seriesLoading=false;seriesRequest=null;}
        });
    }
    function setSaving(value) {
        saving=value;
        form.find(':input').prop('disabled',value);
        updateSeriesSelection();
        dialog.find('.layui-layer-btn a, .layui-layer-close').toggleClass('disabled',value).attr('aria-disabled',String(value));
        dialog.find('.layui-layer-btn0').text(value?'保存中...':'保存');
    }
    function closeRule() {
        if(saving)return false;
        disposed=true;
        form.off('.pdfRuleSeries');
        if(seriesRequest)seriesRequest.abort();
    }
    layer.open({type:1,title:data.id?'编辑模板关联':'新增模板关联',skin:'pf-rule-dialog',area:[Math.min(960,(root.innerWidth||1024)-32)+'px',Math.min(730,(root.innerHeight||800)-32)+'px'],content:content,btn:['保存','取消'],
        success:function(layero){
            dialog=layero;
            form=layero.find('#'+formId);
            modelSelect=form.find('[data-model-ids]');
            form.find('[name="rule.is_active"]').val(data.is_active===false||data.is_active===0||data.is_active==='0'?'0':'1');
            form.on('input.pdfRuleSeries','[data-series-search]',renderSeriesOptions)
                .on('change.pdfRuleSeries','[data-model-ids]',renderSeriesOptions)
                .on('change.pdfRuleSeries','[data-series-option]',function(){
                    if(saving||!this.checked)return;
                    var selected=new Set(modelSelect.val()||[]), id=String($(this).val());
                    selected.add(id);
                    modelSelect.val(Array.from(selected));renderSeriesOptions();
                }).on('click.pdfRuleSeries','[data-series-batch="select"]',function(){
                    if(saving)return;
                    var selected=new Set(modelSelect.val()||[]);
                    visibleSeries.forEach(function(item){selected.add(item.id);});
                    modelSelect.val(Array.from(selected));renderSeriesOptions();
                }).on('click.pdfRuleSeries','[data-series-remove]',function(){
                    if(saving)return;
                    var id=$(this).attr('data-series-remove');
                    modelSelect.val((modelSelect.val()||[]).filter(function(value){return value!==id;}));renderSeriesOptions();
                }).on('click.pdfRuleSeries','[data-series-clear]',function(){if(!saving){modelSelect.val([]);renderSeriesOptions();}})
                .on('click.pdfRuleSeries','[data-series-retry]',loadSeriesOptions);
            SelectUtil.init({parent:form});
            loadSeriesOptions();
        },
        yes:function(index){
            if(saving)return;
            var templateSelect=form.find('[name="rule.template_file"]'), modelSelect=form.find('[data-model-ids]');
            if(!Array.isArray(templateSelect.data('option-datas'))||!Array.isArray(modelSelect.data('option-datas'))){
                LayerMsgBox.alert('模板文件或系列选项尚未加载完成，请稍候再保存；如持续未加载，请关闭后重新打开',2);
                return;
            }
            if(!FormChecker.check(form)){
                LayerMsgBox.alert(form.find('.is-invalid').first().data('tips')||'请检查必填项及输入内容',2);
                return;
            }
            var payload={};form.serializeArray().forEach(function(item){payload[item.name]=item.value;});
            payload.modelIds=(modelSelect.val()||[]).join(',');
            setSaving(true);
            requestRule('saveRule',payload,function(ret){
                closeRule();
                layer.close(index);
                LayerMsgBox.success(ret.msg||'保存成功');
                window['pfLoadRules_'+pageId]();
            },function(){setSaving(false);});
        },btn2:closeRule,cancel:closeRule
    });
}
page.on('click.pdfRules','[data-edit-template]',function(){
    var data=(page.data('pdfRules')||{})[$(this).attr('data-edit-template')];
    if(data)openRule(data);else LayerMsgBox.alert('模板数据已变化，请刷新后重试',2);
});
page.on('click.pdfRules','[data-delete-template]',function(){
    var id=$(this).attr('data-delete-template');
    LayerMsgBox.confirm('确定删除该模板记录？请先在编辑中移除已关联系列。',function(){requestRule('deleteRule',{id:id},function(){window['pfLoadRules_'+pageId]();});});
});

// ========== 刷新缓存 ==========
window['pfClearCache_'+pageId] = function() {
	requestRule('clearCache', {}, function(ret) {
		layer.msg(ret.msg || '缓存已刷新', {icon: 1});
		window['pfLoadFolders_'+pageId](true);
	});
};

// ========== 初始化 ==========
window['pfLoadFolders_'+pageId]();


},cacheRules:function(table,res){
    var page=root.jQuery(table).closest('.jbolt_page'),data=(res&&res.data)||res||{},list=table.tableListDatas||(Array.isArray(data)?data:(data.list||[])),map={};
    list.forEach(function(item){map[String(item.id)]=item;});page.data('pdfRules',map);
},close:function(container){root.jQuery(container).off('.pdfRules').removeData('pdfTemplatesReady');}};}(window));

/* ===== qarep PDF 生成结果与归档下载 ===== */
(function(root){
    function safeUrl(value){
        if(typeof value!=='string'||!value.trim())return '';
        try{var url=new URL(value,root.location.href);return url.origin===root.location.origin&&/^https?:$/.test(url.protocol)?url.href:'';}catch(ignored){return '';}
    }
    function resultData(ret){
        var data=ret&&ret.data?ret.data:(ret||{});
        return {successCount:Number(data.successCount)||0,failCount:Number(data.failCount)||0,
            msg:String(data.msg||''),failures:Array.isArray(data.failures)?data.failures:[],warnings:Array.isArray(data.warnings)?data.warnings:[],
            outputs:Array.isArray(data.outputs)?data.outputs:[],archiveUrl:safeUrl(data.archiveUrl)};
    }
    function show(ret){
        var data=resultData(ret),escape=root.SiargoQarepCharts.escape;
        var message=data.msg||('成功 '+data.successCount+' 项，失败 '+data.failCount+' 项');
        var failed=data.failCount>0||data.failures.length>0||(ret&&ret.state==='fail');
        if(!failed&&!data.warnings.length){root.LayerMsgBox.success(escape(message),1500);return;}
        var content=escape(message);
        function messages(title,items){
            if(!items.length)return;
            content+='<br><br>'+(title?title+'：<br>':'')+items.map(function(item){return escape(String(item));}).join('<br>');
        }
        messages('未生成的报告单',data.failures);messages('',data.warnings);
        root.LayerMsgBox.alert(content,failed?2:7);
    }
    function generate(page,button,monthly,params){
        button.prop('disabled',true);root.LayerMsgBox.loading('正在生成 PDF，请稍候...',600000);
        function finish(){button.prop('disabled',false);root.LayerMsgBox.closeLoadingNow();}
        root.jQuery.ajax({
            url:root.actionUrl('admin/siargo/qarep/'+(monthly?'toPdfs':'toPdf')),type:'post',dataType:'json',timeout:600000,data:params,
            success:function(ret){finish();show(ret);var refresh=page.data('qarepRefresh');if(refresh)refresh();},
            error:function(xhr,status){finish();var data=xhr.responseJSON;root.LayerMsgBox.alert(data&&data.msg?data.msg:(status==='timeout'?'生成请求超时，请刷新列表查看已生成的 PDF':'网络通讯异常，请稍后重试'),2);}
        });
    }
    function chooseMonths(page,button){
        var year=Number(button.attr('data-export-year')),maxMonth=Number(button.attr('data-max-month'));
        if(!Number.isInteger(year)||!Number.isInteger(maxMonth)||maxMonth<1||maxMonth>11){
            root.LayerMsgBox.alert('今年暂无可导出的已结束月份',2);return;
        }
        var dialogId='qarep_pdf_months_'+page.attr('data-page-id'),submitted=false;
        // 原生 Dialog 的字符串内容会挂载到页面，避免 detached clone 只产生遮罩。
        root.DialogUtil.openNewDialog({
            id:dialogId,ele:button,title:'按月份导出 PDF',width:String(Math.min(480,(root.innerWidth||1024)-32)),height:String(Math.min(264,(root.innerHeight||768)-32)),btn:'no',
            content:page.find('[data-qarep-pdf-months]').html(),
            successHandler:function(){
                var content=root.jQuery('#'+dialogId),dialog=content.closest('.layui-layer').addClass('qa-pdf-month-dialog'),index=Number(dialog.attr('times'));
                content.find('select').val(String(maxMonth));
                content.on('click.qarepMonths','[data-qarep-month-cancel]',function(){root.LayerMsgBox.close(index);});
                content.on('click.qarepMonths','[data-qarep-month-submit]',function(){
                    if(submitted)return;
                    var startMonth=Number(content.find('[name="startMonth"]').val()),endMonth=Number(content.find('[name="endMonth"]').val());
                    if(!Number.isInteger(startMonth)||!Number.isInteger(endMonth)||startMonth<1||endMonth<1||startMonth>maxMonth||endMonth>maxMonth){
                        root.LayerMsgBox.alert('请选择今年 1 月至 '+maxMonth+' 月内的月份',2);return;
                    }
                    if(startMonth>endMonth){root.LayerMsgBox.alert('起始月份不能晚于结束月份',2);return;}
                    submitted=true;root.LayerMsgBox.close(index);
                    generate(page,button,true,{year:year,startMonth:startMonth,endMonth:endMonth});
                });
                button.prop('disabled',true);
                content.find('[name="startMonth"]').trigger('focus');
            },
            closeHandler:function(){if(!submitted)button.prop('disabled',false);}
        });
    }
    root.SiargoQarepPdf={init:function(page){
        page.off('click.qarepPdf').on('click.qarepPdf','[data-qarep-pdf]',function(){
            var button=root.jQuery(this);
            if(button.prop('disabled'))return;
            if(button.attr('data-qarep-pdf')==='monthly'){chooseMonths(page,button);return;}
            var checked=root.jboltTableGetCheckedIds(button.attr('data-table-id')),ids=Array.isArray(checked)?checked.join(','):String(checked||'');
            if(!ids){root.LayerMsgBox.alert('请先选择需要生成 PDF 的产品',2);return;}
            root.LayerMsgBox.confirm('确定生成选中产品的报告单 PDF？',function(){generate(page,button,false,{ids:ids});});
        });
    }};
    root.SiargoQarepPdfResult={normalize:resultData,safeUrl:safeUrl};
}(window));

/* ===== 型号参数种类与参数值卡片 ===== */
(function(root){
    root.filterProdParamValues=function(portal){
        var input=portal.closest('.jbolt_page').find('[id=prodParamValueSearch]');
        var table=portal.find('table.prod-param-value-table.jbolt_main_table');
        var instance=table.length?table.jboltTable('inst'):null;
        input.prop('disabled',!instance);
        // 列表刷新后复用 JBoltTable 原生筛选，列号从 1 开始，参数值为第 2 列。
        if(instance){instance.me.filterByKeywords(instance,input.val(),[2]);}
    };
    function initValueSearch(page){
        var input=page.find('[id=prodParamValueSearch]'),portal=page.find('[id=prodParamValuePortal]');
        input.off('.prodParamSearch').on('keyup.prodParamSearch',function(){
            var table=portal.find('table.prod-param-value-table.jbolt_main_table');
            if(table.length){root.jboltTableFilterByKeywords(table,this.value,[2]);}
        });
        portal.data('handler','filterProdParamValues');
    }
    root.readParamValues=function(type,typeId,typeName,event){
        var $=root.jQuery;
        if(event&&$(event.target).closest('a,button').length){return;}
        var page=$(type).closest('.jbolt_page'),portal=page.find('[id=prodParamValuePortal]');
        page.find('.prodParamTypeTable tr.active').removeClass('active');
        $(type).addClass('active');
        page.find('[id=selectParamTypeName]').text(typeName);
        initValueSearch(page);
        if(portal.data('param-type-id')!==String(typeId)){page.find('[id=prodParamValueSearch]').val('').trigger('change');}
        portal.data('param-type-id',String(typeId));
        page.find('[id=prodParamValueSearch]').prop('disabled',true);
        portal.ajaxPortal(true,'/admin/siargo/prodparam/value/mgr/'+typeId,true);
    };
    root.refreshAllProdParamPortal=function(){
        var page=root.jQuery('.prod-param-value-header:visible').closest('.jbolt_page');
        page.find('[id=prodParamTypePortal]').ajaxPortal(true);
        page.find('[id=selectParamTypeName]').text('未选');
        page.find('[id=prodParamValueSearch]').val('').trigger('change').prop('disabled',true);
        page.find('[id=prodParamValuePortal]').removeData('param-type-id')
            .ajaxPortal(true,'/admin/siargo/prodparam/value/mgr/0',true);
    };
}(window));
