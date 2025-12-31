var jbolt_autocomplete_js_version="1.3.1";
/**
 * JBolt 内置 Autocomplete组件
 * 基于jQuery Autocomplete plugin
 * @param $
 * @returns
 */
;(function($) {
	$.fn.extend({
		autocomplete : function(urlOrData, options) {
			var isUrl = typeof urlOrData == "string";
			options = $.extend({}, $.Autocompleter.defaults, {
				url : isUrl ? urlOrData : null,
				data : isUrl ? null : urlOrData,
				delay : isUrl ? $.Autocompleter.defaults.delay : 10,
				max : options && !options.scroll ? 10 : 150,
				noRecord : "No Records."
			}, options);
			options.highlight = options.highlight || function(value) {
				return value
			};
			options.formatMatch = options.formatMatch || options.formatItem;
			return this.each(function() {
				new $.Autocompleter(this, options)
			})
		},
		result : function(handler) {
			return this.bind("result", handler)
		},
		search : function(handler) {
			return this.trigger("search", [ handler ])
		},
		flushCache : function() {
			return this.trigger("flushCache")
		},
		setOptions : function(options) {
			return this.trigger("setOptions", [ options ])
		},
		unautocomplete : function() {
			return this.trigger("unautocomplete")
		}
	});
	$.Autocompleter = function(input, options) {
		var KEY = {
			UP : 38,
			DOWN : 40,
			DEL : 46,
			TAB : 9,
			RETURN : 13,
			ESC : 27,
			COMMA : 188,
			PAGEUP : 33,
			PAGEDOWN : 34,
			BACKSPACE : 8
		};
		var globalFailure = null;
		if (options.failure != null && typeof options.failure == "function") {
			globalFailure = options.failure
		}
		var $input = $(input).attr("autocomplete", "off").addClass(
				options.inputClass);
		var timeout;
		var previousValue = "";
		var cache = $.Autocompleter.Cache(options);
		var hasFocus = 0;
		var lastKeyPressCode;
		var config = {
			mouseDownOnSelect : false
		};
		var select = $.Autocompleter.Select(options, input, selectCurrent,
				config);
		var blockSubmit;
		navigator.userAgent.indexOf("Opera") != -1
				&& $(input.form).bind("submit.autocomplete", function() {
					if (blockSubmit) {
						blockSubmit = false;
						return false
					}
				});
		var bindEvent=(navigator.userAgent.indexOf("Opera") != -1
				&& !'KeyboardEvent' in window ? "keypress" : "keydown")
				+ ".autocomplete";
		$input.bind(bindEvent,function(event) {
					hasFocus = 1;
					lastKeyPressCode = event.keyCode;
					switch (event.keyCode) {
					case KEY.UP:
						if (select.visible()) {
							event.preventDefault();
							select.prev()
						} else {
							onChange(0, true)
						}
						break;
					case KEY.DOWN:
						if (select.visible()) {
							event.preventDefault();
							select.next()
						} else {
							onChange(0, true)
						}
						break;
					case KEY.PAGEUP:
						if (select.visible()) {
							event.preventDefault();
							select.pageUp()
						} else {
							onChange(0, true)
						}
						break;
					case KEY.PAGEDOWN:
						if (select.visible()) {
							event.preventDefault();
							select.pageDown()
						} else {
							onChange(0, true)
						}
						break;
					case options.multiple
							&& $.trim(options.multipleSeparator) == ","
							&& KEY.COMMA:
					case KEY.TAB:
					case KEY.RETURN:
						if (selectCurrent()) {
							event.preventDefault();
							blockSubmit = true;
							return false
						}
						break;
					case KEY.ESC:
						select.hide();
						break;
					default:
						clearTimeout(timeout);
						timeout = setTimeout(onChange, options.delay);
						break
					}
				}).focus(function() {
			hasFocus++
		}).blur(function() {
			hasFocus = 0;

			if (!config.mouseDownOnSelect) {
				hideResults()
			}
		}).click(function() {
			if (options.clickFire) {
				if (!select.visible()) {
					onChange(0, true)
				}
			} else {
				if (hasFocus++ > 1 && !select.visible()) {
					onChange(0, true)
				}
			}
		}).bind(
				"search",
				function() {
					var fn = (arguments.length > 1) ? arguments[1] : null;
					function findValueCallback(q, data) {
						var result;
						if (data && data.length) {
							for (var i = 0; i < data.length; i++) {
								if (data[i].result.toLowerCase() == q
										.toLowerCase()) {
									result = data[i];
									break
								}
							}
						}
						if (typeof fn == "function")
							fn(result);
						else
							$input.trigger("result", result
									&& [ result.data, result.value ])
					}
					$.each(trimWords($input.val()), function(i, value) {
						request(value, findValueCallback, findValueCallback)
					})
				}).bind("flushCache", function() {
			cache.flush()
		}).bind("setOptions", function() {
			$.extend(true, options, arguments[1]);
			if ("data" in arguments[1])
				cache.populate()
		}).bind("unautocomplete", function() {
			select.unbind();
			$input.unbind();
			$(input.form).unbind(".autocomplete")
		});
		function selectCurrent() {
			var selected = select.selected();
			if (!selected)
				return false;
			var v = selected.result;
			previousValue = v;
			if (options.multiple) {
				var words = trimWords($input.val());
				if (words.length > 1) {
					var seperator = options.multipleSeparator.length;
					var cursorAt = $(input).selection().start;
					var wordAt, progress = 0;
					$.each(words, function(i, word) {
						progress += word.length;
						if (cursorAt <= progress) {
							wordAt = i;
							return false
						}
						progress += seperator
					});
					words[wordAt] = v;
					v = words.join(options.multipleSeparator)
				}
				v += options.multipleSeparator
			}
			$input.val(v);
			hideResultsNow();
			$input.trigger("result", [ selected.data, selected.value ]);
			return true
		}
		function onChange(crap, skipPrevCheck) {
			if (lastKeyPressCode == KEY.DEL) {
				select.hide();
				return
			}
			var currentValue = $input.val();
			if (!skipPrevCheck && currentValue == previousValue)
				return;
			previousValue = currentValue;
			currentValue = lastWord(currentValue);
			if (currentValue.length >= options.minChars) {
				$input.addClass(options.loadingClass);
				if (!options.matchCase)
					currentValue = currentValue.toLowerCase();
				request(currentValue, receiveData, hideResultsNow)
			} else {
				stopLoading();
				select.hide()
			}
		}
		;
		function trimWords(value) {
			if (!value)
				return [ "" ];
			if (!options.multiple)
				return [ $.trim(value) ];
			return $.map(value.split(options.multipleSeparator),
					function(word) {
						return $.trim(value).length ? $.trim(word) : null
					})
		}
		function lastWord(value) {
			if (!options.multiple)
				return value;
			var words = trimWords(value);
			if (words.length == 1)
				return words[0];
			var cursorAt = $(input).selection().start;
			if (cursorAt == value.length) {
				words = trimWords(value)
			} else {
				words = trimWords(value.replace(value.substring(cursorAt), ""))
			}
			return words[words.length - 1]
		}
		function autoFill(q, sValue) {
			if (options.autoFill
					&& (lastWord($input.val()).toLowerCase() == q.toLowerCase())
					&& lastKeyPressCode != KEY.BACKSPACE) {
				$input.val($input.val()
						+ sValue.substring(lastWord(previousValue).length));
				$(input).selection(previousValue.length,
						previousValue.length + sValue.length)
			}
		}
		;
		function hideResults() {
			clearTimeout(timeout);
			timeout = setTimeout(hideResultsNow, 200)
		}
		;
		function hideResultsNow() {
			var wasVisible = select.visible();
			select.hide();
			clearTimeout(timeout);
			stopLoading();
			if (options.mustMatch) {
				$input.search(function(result) {
					if (!result) {
						if (options.multiple) {
							var words = trimWords($input.val()).slice(0, -1);
							$input.val(words.join(options.multipleSeparator)
									+ (words.length ? options.multipleSeparator
											: ""))
						} else {
							$input.val("");
							$input.trigger("result", null)
						}
					}
				})
			}
		}
		;
		function receiveData(q, data) {
			if (data && data.length && hasFocus) {
				stopLoading();
				select.display(data, q);
				autoFill(q, data[0].value);
				select.show()
			} else {
				hideResultsNow()
			}
		}
		;
		function request(term, success, failure) {
			if (!options.matchCase)
				term = term.toLowerCase();
			var data = cache.load(term);
			if (data) {
				if (data.length) {
					success(term, data)
				} else {
					var parsed = options.parse
							&& options.parse(options.noRecord)
							|| parse(options.noRecord);
					success(term, parsed)
				}
			} else if ((typeof options.url == "string")
					&& (options.url.length > 0)) {
				var extraParams = {
					timestamp : +new Date()
				};
				$.each(options.extraParams, function(key, param) {
					extraParams[key] = typeof param == "function" ? param()
							: param
				});
				$.ajax({
					mode : "abort",
					port : "autocomplete" + input.name,
					dataType : options.dataType,
					url : options.url,
					data : $.extend({
						q : lastWord(term),
						limit : options.max
					}, extraParams),
					success : function(data) {
						var parsed = options.parse && options.parse(data)
								|| parse(data);
						cache.add(term, parsed);
						success(term, parsed)
						if(data.state=="fail"){
							LayerMsgBox.alert(data.msg?data.msg:"数据获取异常",2);
						}
					}
				})
			} else {
				select.emptyList();
				if (globalFailure != null) {
					globalFailure()
				} else {
					failure(term)
				}
			}
		}
		;
		function parse(data) {
			var parsed = [];
			var rows = data.split("\n");
			for (var i = 0; i < rows.length; i++) {
				var row = $.trim(rows[i]);
				if (row) {
					row = row.split("|");
					parsed[parsed.length] = {
						data : row,
						value : row[0],
						result : options.formatResult
								&& options.formatResult(row, row[0]) || row[0]
					}
				}
			}
			return parsed
		}
		;
		function stopLoading() {
			$input.removeClass(options.loadingClass)
		}
	};
	$.Autocompleter.defaults = {
		inputClass : "ac_input",
		resultsClass : "ac_results",
		loadingClass : "ac_loading",
		minChars : 1,
		clickHeader:false,
		delay : 400,
		matchCase : false,
		matchSubset : true,
		matchContains : false,
		cacheLength : 100,
		max : 1000,
		mustMatch : false,
		extraParams : {},
		selectFirst : true,
		formatItem : function(row) {
			return row[0]
		},
		formatMatch : null,
		autoFill : false,
		width : 0,
		multiple : false,
		multipleSeparator : " ",
		inputFocus : true,
		clickFire : true,
		highlight : function(value, term) {
			return value.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)("
					+ term
							.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi,
									"\\$1") + ")(?![^<>]*>)(?![^&;]+;)", "gi"),
					"<strong>$1</strong>")
		},
		unescapeHTML:false,
		scroll : true,
		scrollHeight : 300,
		scrollJumpPosition : true
	};
	$.Autocompleter.Cache = function(options) {
		var data = {};
		var length = 0;
		function matchSubset(s, sub) {
			if (!options.matchCase)
				s = s.toLowerCase();
			var i = s.indexOf(sub);
			if (options.matchContains == "word") {
				i = s.toLowerCase().search("\\b" + sub.toLowerCase())
			}
			if (i == -1)
				return false;
			return i == 0 || options.matchContains
		}
		;
		function add(q, value) {
			if (length > options.cacheLength) {
				flush()
			}
			if (!data[q]) {
				length++
			}
			data[q] = value
		}
		function populate() {
			if (!options.data)
				return false;
			var stMatchSets = {}, nullData = 0;
			if (!options.url)
				options.cacheLength = 1;
			stMatchSets[""] = [];
			for (var i = 0, ol = options.data.length; i < ol; i++) {
				var rawValue = options.data[i];
				rawValue = (typeof rawValue == "string") ? [ rawValue ]
						: rawValue;
				var value = options.formatMatch(rawValue, i + 1,
						options.data.length);
				if (typeof (value) === 'undefined' || value === false)
					continue;
				var firstChar = value.charAt(0).toLowerCase();
				if (!stMatchSets[firstChar])
					stMatchSets[firstChar] = [];
				var row = {
					value : value,
					data : rawValue,
					result : options.formatResult
							&& options.formatResult(rawValue) || value
				};
				stMatchSets[firstChar].push(row);
				if (nullData++ < options.max) {
					stMatchSets[""].push(row)
				}
			}
			;
			$.each(stMatchSets, function(i, value) {
				options.cacheLength++;
				add(i, value)
			})
		}
		setTimeout(populate, 25);
		function flush() {
			data = {};
			length = 0
		}
		return {
			flush : flush,
			add : add,
			populate : populate,
			load : function(q) {
				if (!options.cacheLength || !length)
					return null;
				if (!options.url && options.matchContains) {
					var csub = [];
					for ( var k in data) {
						if (k.length > 0) {
							var c = data[k];
							$.each(c, function(i, x) {
								if (matchSubset(x.value, q)) {
									csub.push(x)
								}
							})
						}
					}
					return csub
				} else if (data[q]) {
					return data[q]
				} else if (options.matchSubset) {
					for (var i = q.length - 1; i >= options.minChars; i--) {
						var c = data[q.substr(0, i)];
						if (c) {
							var csub = [];
							$.each(c, function(i, x) {
								if (matchSubset(x.value, q)) {
									csub[csub.length] = x
								}
							});
							return csub
						}
					}
				}
				return null
			}
		}
	};
	$.Autocompleter.Select = function(options, input, select, config) {
		var CLASSES = {
			ACTIVE : "ac_over"
		};
		var listItems, active = -1, data, term = "", needsInit = true, element, list;
		function init() {
			if (!needsInit)
				return;
			var id="acr_"+randomId();
			$(input).data("acresult",id).attr("data-acresult",id);
			element = $("<div/>").hide().attr("id",id).addClass(options.resultsClass).css(
					"position", "absolute").appendTo(document.body).hover(
					function(event) {
						if ($(this).is(":visible")) {
							input.focus()
						}
						config.mouseDownOnSelect = false
					});

			list = $("<ul/>")
					.appendTo(element)
					.mouseover(
							function(event) {
								if (target(event).nodeName
										&& target(event).nodeName.toUpperCase() == 'LI') {
									active = $("li", list).removeClass(
											CLASSES.ACTIVE)
											.index(target(event));
									$(target(event)).addClass(CLASSES.ACTIVE)
								}
							}).click(function(event) {
					event.preventDefault();
					event.stopPropagation();
						$(target(event)).addClass(CLASSES.ACTIVE);
						select();
						if (options.inputFocus)
							input.focus();

						return false
					}).mousedown(function() {
						config.mouseDownOnSelect = true
					}).mouseup(function() {
						config.mouseDownOnSelect = false
					});
			if (options.width > 0)
				element.css("width", options.width);
			needsInit = false
		}
		function target(event) {
			var element = event.target;
			while (element && element.tagName != "LI")
				element = element.parentNode;
			if (!element)
				return [];
			return element
		}
		function moveSelect(step) {
			listItems.slice(active, active + 1).removeClass(CLASSES.ACTIVE);
			movePosition(step);
			var activeItem = listItems.slice(active, active + 1).addClass(
					CLASSES.ACTIVE);
			if (options.scroll) {
				var offset = 0;
				listItems.slice(0, active).each(function() {
					offset += this.offsetHeight
				});
				if ((offset + activeItem[0].offsetHeight - list.scrollTop()) > list[0].clientHeight) {
					list.scrollTop(offset + activeItem[0].offsetHeight
							- list.innerHeight())
				} else if (offset < list.scrollTop()) {
					list.scrollTop(offset)
				}
			}
		}
		;
		function movePosition(step) {
			if (options.scrollJumpPosition
					|| (!options.scrollJumpPosition && !((step < 0 && active == 0) || (step > 0 && active == listItems
							.length - 1)))) {
				active += step;
				if (active < 0) {
					active = listItems.length - 1
				} else if (active >= listItems.length) {
					active = 0
				}
			}
		}
		function limitNumberOfItems(available) {
			return options.max && options.max < available ? options.max
					: available
		}


		function unescapeHTML(html) {
			var doc = new DOMParser().parseFromString(html, 'text/html');
			return doc.body.textContent || "";
		}

		function fillList() {
			list.empty();
			var max = limitNumberOfItems(data.length);
			for (var i = 0; i < max; i++) {
				if (!data[i])
					continue;
				var formatted = options.formatItem(data[i].data, i + 1, max,
						data[i].value, term);
				if (formatted === false)
					continue;
				var liobj = $("<li/>");
				var spans = $(formatted);
				spans.each(function(i,span){
					if($.trim(span.innerText)){
						span.innerHTML = options.highlight(options.unescapeHTML?unescapeHTML(span.innerHTML):span.innerHTML, term);
					}
				});
				var li = liobj.html(spans)
						.addClass(i % 2 == 0 ? "ac_even" : "ac_odd").appendTo(
								list)[0];
				$.data(li, "ac_data", data[i])
			}
			listItems = list.find("li");
			if (options.selectFirst) {
				listItems.slice(0, 1).addClass(CLASSES.ACTIVE);
				active = 0
			}
			if ($.fn.bgiframe)
				list.bgiframe()
		}
		return {
			processHeaderSpan:function(elHeader,align){
				if(elHeader.indexOf("-")==-1){
					html = "<span style='text-align:"+align+"'>"+elHeader+"</span>";
				}else{
					var harr = elHeader.split("-");
					var width = harr[1];
					if(typeof(width)=="undefined" || width.length == 0 || (isNaN(width) && !width.endWith("%"))){
						width = null;
					}else if(width.endWith("%")){
						width = "width:"+width +";";
					}else{
						width = "width:"+width +"px;";
					}
					var isAuto=width;
					if(harr.length == 2){
						html = "<span "+(isAuto?"class='auto'":'')+" style='"+width+"text-align:"+align+"'>"+harr[0]+"</span>";
					}else if(harr.length == 3){
						var align3 = harr[2]||align;
						html = "<span "+(isAuto?"class='auto'":'')+" style='"+width+"text-align:"+align3+"'>"+harr[0]+"</span>";
					}
				}
				return html;
			},
			display : function(d, q) {
				init();
				data = d;
				term = q;
				fillList()
			},
			next : function() {
				moveSelect(1)
			},
			prev : function() {
				moveSelect(-1)
			},
			pageUp : function() {
				if (active != 0 && active - 8 < 0) {
					moveSelect(-active)
				} else {
					moveSelect(-8)
				}
			},
			pageDown : function() {
				if (active != listItems.length - 1
						&& active + 8 > listItems.length) {
					moveSelect(listItems.length - 1 - active)
				} else {
					moveSelect(8)
				}
			},
			hide : function() {
				element && element.hide();
				listItems && listItems.removeClass(CLASSES.ACTIVE);
				active = -1
			},
			visible : function() {
				return element && element.is(":visible")
			},
			current : function() {
				return this.visible()
						&& (listItems.filter("." + CLASSES.ACTIVE)[0] || options.selectFirst
								&& listItems[0])
			},
			show : function() {
				var theInput=$(input);
				var inTd=theInput.closest("td"),isTdEditor=false;
				if(isOk(inTd)){
					var editableTable=inTd.closest("table[data-editable='true']");
					isTdEditor=isOk(editableTable);
				}
				// var offset = theInput.offset();
				// var eleTop = offset.top + input.offsetHeight+(isTdEditor?3:2);
				// element.css(
				// 		{
				// 			width : typeof options.width == "string"
				// 					|| options.width > 0 ? options.width+1:theInput.css("width"),
				// 			top : eleTop,
				// 			left : offset.left-(isTdEditor?1:0)
				// 		}).show();


				var offset=theInput.offset(),
					elHeader=theInput.data("header"),
					hasHeader = isOk(elHeader),
					windowScrollTop=jboltWindow.scrollTop(),
					windowScrollLeft=jboltWindow.scrollLeft(),
					height=theInput.outerHeight(),
					top=offset.top+height+(isTdEditor?3:2)-windowScrollTop,
					left=offset.left-windowScrollLeft,
					owidth = theInput.outerWidth(),
					width=theInput.data("width")||owidth,
					dataHeight=theInput.data("height");
				var newWidth=jboltWindowWidth-left-20;
				if(width>newWidth){
					left=left+owidth-width;
				}
				if(self!=top){
					windowScrollTop = 0;
				}
				if(offset.top+options.scrollHeight>jboltWindowHeight-25){
					top = offset.top-options.scrollHeight-(isTdEditor?3:2)-windowScrollTop;
				}
				var eleoptions = {
					"top":top+"px",
					"left":left+"px",
					"width":width+"px",
					"max-width":width+"px",
					"height":options.scrollHeight+"px",
					"max-height":options.scrollHeight+"px"
				};
				var dataUlHeight = options.scrollHeight;
				if(hasHeader){
					var align = theInput.data("text-align")||"center";
					var exist = element.find("div.header");
					if(notOk(exist)){
						var header=$("<div class='header'></div>");
						dataUlHeight = dataUlHeight-30;
						element.addClass("hasHeader");
						var html="";

						if(elHeader.indexOf(",")==-1){
							html = this.processHeaderSpan(elHeader,align);
						}else{
							var hharr=elHeader.split(",");
							var hhvalue;
							for(var i in hharr){
								hhvalue = hharr[i];
								html+=this.processHeaderSpan(hhvalue,align);
							}
						}
						header.html(html);
						list.before(header);
					}

				}

				element.css(eleoptions).show();


				if (options.scroll) {
					list.scrollTop(0);
					list.css({
						maxHeight : dataUlHeight,
						overflow : 'auto'
					});
					if (navigator.userAgent.indexOf("MSIE") != -1
							&& typeof document.body.style.maxHeight === "undefined") {
						var listHeight = 0;
						listItems.each(function() {
							listHeight += this.offsetHeight
						});
						var scrollbarsVisible = listHeight > options.scrollHeight;
						list.css('height',
								scrollbarsVisible ? options.scrollHeight
										: listHeight);
						if (!scrollbarsVisible) {
							listItems.width(list.width()
									- parseInt(listItems.css("padding-left"))
									- parseInt(listItems.css("padding-right")))
						}
					}
				}
			},
			selected : function() {
				var selected = listItems
						&& listItems.filter("." + CLASSES.ACTIVE).removeClass(
								CLASSES.ACTIVE);
				return selected && selected.length
						&& $.data(selected[0], "ac_data")
			},
			emptyList : function() {
				list && list.empty()
			},
			unbind : function() {
				element && element.remove()
			}
		}
	};
	$.fn.selection = function(start, end) {
		if (start !== undefined) {
			return this.each(function() {
				if (this.createTextRange) {
					var selRange = this.createTextRange();
					if (end === undefined || start == end) {
						selRange.move("character", start);
						selRange.select()
					} else {
						selRange.collapse(true);
						selRange.moveStart("character", start);
						selRange.moveEnd("character", end);
						selRange.select()
					}
				} else if (this.setSelectionRange) {
					this.setSelectionRange(start, end)
				} else if (this.selectionStart) {
					this.selectionStart = start;
					this.selectionEnd = end
				}
			})
		}
		var field = this[0];
		if (field.createTextRange) {
			var range = document.selection.createRange(), orig = field.value, teststring = "<->", textLength = range.text.length;
			range.text = teststring;
			var caretAt = field.value.indexOf(teststring);
			field.value = orig;
			this.selection(caretAt, caretAt + textLength);
			return {
				start : caretAt,
				end : caretAt + textLength
			}
		} else if (field.selectionStart !== undefined) {
			return {
				start : field.selectionStart,
				end : field.selectionEnd
			}
		}
	}
})(jQuery);
