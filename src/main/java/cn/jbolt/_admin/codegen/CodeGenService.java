package cn.jbolt._admin.codegen;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import cn.jbolt._admin.codegen.modelattr.CodeGenModelAttrService;
import cn.jbolt.common.model.CodeGen;
import cn.jbolt.common.model.CodeGenModelAttr;
import cn.jbolt.core.base.JBoltIDGenMode;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.cache.JBoltPermissionCache;
import cn.jbolt.core.consts.JBoltConst;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.db.sql.SqlExpress;
import cn.jbolt.core.enjoy.directive.EnumToOptionsDirective;
import cn.jbolt.core.gen.JBoltColumnToBuildAttrNameFunction;
import cn.jbolt.core.gen.JBoltInnerPermissionKeyGen;
import cn.jbolt.core.gen.JBoltProjectGenConfig;
import cn.jbolt.core.gen.JFinalModelGenerator;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.Permission;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltArrayUtil;
import cn.jbolt.core.util.JBoltConsoleUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Engine;

import java.io.File;
import java.util.*;

/**
 * JBolt 开发平台 - 代码生成器 service
 *
 * @ClassName: CodeGenService
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2021年9月25日
 */
public class CodeGenService extends JBoltBaseService<CodeGen> {
    private CodeGen dao = new CodeGen().dao();
    @Inject
    private CodeGenModelAttrService codeGenModelAttrService;

    @Override
    protected CodeGen dao() {
        return dao;
    }

    private static final String controllerTemplate = "/gentpl/codegen/controller_template.jf";
    private static final String cacheTemplate = "/gentpl/codegen/cache_template.jf";
    private static final String controllerCommonTemplate = "/gentpl/codegen/controller_common_template.jf";
    private static final String serviceCommonTemplate = "/gentpl/codegen/service_common_template.jf";
    private static final String serviceTemplate = "/gentpl/codegen/service_template.jf";
    private static final String routesTemplate = "/gentpl/codegen/routes_template.jf";
    private static final String indexhtmlTemplate = "/config/_table_portal.html";
    private static final String formhtmlTemplate = "/config/_form_portal.html";
    private static final String importExcelHtmlTemplate = "/config/_import_excel.html";
    private static final String addhtmlTemplate = "/gentpl/codegen/add_html_template.jf";
    private static final String edithtmlTemplate = "/gentpl/codegen/edit_html_template.jf";
    private Engine codeGenEngine;

    private Engine getCodeGenTplEngine() {
        if (codeGenEngine == null) {
            codeGenEngine = new Engine()
//                    .setStaticFieldExpression(true)
//                    .setStaticMethodExpression(true)
                    .setToClassPathSourceFactory() // 从 class path 内读模板文件
                    .addSharedMethod(new StrKit())
                    .addSharedFunction(controllerCommonTemplate)
                    .addSharedFunction(serviceCommonTemplate);
        }
        return codeGenEngine;
    }

    private Engine codeGenIndexHtmlEngine;

    private Engine getCodeGenIndexHtmlTplEngine() {
        if (codeGenIndexHtmlEngine == null) {
            codeGenIndexHtmlEngine = new Engine()
//                    .setStaticFieldExpression(true)
//                    .setStaticMethodExpression(true)
                    .setBaseTemplatePath(PathKit.getWebRootPath() + "/_view/_admin/_jbolt_code_gen")
                    .addSharedMethod(new StrKit())
                    .addDirective("enumToOptions", EnumToOptionsDirective.class)
                    //可视化生成器 index.html专用 查询条件公用模板
                    .addSharedFunction("/config/_condition.html");
        }
        return codeGenIndexHtmlEngine;
    }

    private Engine codeGenFormHtmlEngine;

    private Engine getCodeGenFormHtmlTplEngine() {
        if (codeGenFormHtmlEngine == null) {
            codeGenFormHtmlEngine = new Engine()
//                    .setStaticFieldExpression(true)
//                    .setStaticMethodExpression(true)
                    .setBaseTemplatePath(PathKit.getWebRootPath() + "/_view/_admin/_jbolt_code_gen")
                    .addSharedMethod(new StrKit())
                    .addDirective("enumToOptions", EnumToOptionsDirective.class)
                    //可视化生成器 _form.html专用 模板
                    .addSharedFunction("/config/_form_ele_define_function.html");
        }
        return codeGenFormHtmlEngine;
    }
    private Engine codeGenImportExcelHtmlEngine;
    private Engine getCodeGenImportExcelHtmlTplEngine() {
        if (codeGenImportExcelHtmlEngine == null) {
            codeGenImportExcelHtmlEngine = new Engine()
                    .setStaticFieldExpression(true)
                    .setStaticMethodExpression(true)
                    .setBaseTemplatePath(PathKit.getWebRootPath() + "/_view/_admin/_jbolt_code_gen")
                    .addSharedMethod(new StrKit());
        }
        return codeGenImportExcelHtmlEngine;
    }

    /**
     * 后台数据查询 分页
     *
     * @param pid
     * @param pageNumber
     * @param pageSize
     * @param sortColumn
     * @param sortType
     * @param keywords
     * @param style
     * @param state
     * @param isDelete
     * @param matchColumns
     * @return
     */
    public Page<CodeGen> paginateAdminDatas(Integer pid, Integer pageNumber, Integer pageSize, String sortColumn, String sortType,
                                            String keywords, Integer style, Integer state, Boolean isDelete, String matchColumns) {
        Sql sql = selectSql().page(pageNumber, pageSize);
        if (isDelete == null) {
            isDelete = false;
        }
        sql.eq("pid", pid);
        sql.eq("is_deleted", isDelete);
        sql.orderBy(sortColumn, sortType);
        sql.likeMulti(keywords, matchColumns);
        return paginate(sql);
    }

    /**
     * 保存配置
     *
     * @param codeGen
     * @return
     */
    public Ret save(CodeGen codeGen) {
        Ret ret = checkPara(codeGen, false);
        if (ret.isFail()) {
            return ret;
        }
        processVersionSn(codeGen);
        codeGen.setIndexHtmlPageTitle(codeGen.getMainTableRemark()+"管理");
        codeGen.setIndexHtmlPageIcon("jbicon2 jbi-appstore");
        codeGen.setCreateUserId(JBoltUserKit.getUserId());
        codeGen.setUpdateUserId(JBoltUserKit.getUserId());
        codeGen.setSubTableCount(0);
        processCodeGenPrefixAndPackages(codeGen);
        codeGen.setIsAutoCache(false);
        processCacheSettings(codeGen);
        codeGen.setIsKeyCache(false);
        codeGen.setIsGenModel(true);
        codeGen.setIsTableUseRecord(false);
        codeGen.setIsTableRecordCamelCase(false);
        codeGen.setIsIdCache(true);
        codeGen.setIsToolbar(false);
        codeGen.setIsShowOptcol(true);
        codeGen.setIsShowOptcol(codeGen.getIsCrud());
        codeGen.setViewLayout("jboltLayout");
        if (isOk(codeGen.getMainTableRemark())) {
            codeGen.setIndexHtmlPageTitle(codeGen.getMainTableRemark());
        }
        codeGen.setState(CodeGenState.NONE.getValue());
        if (codeGen.getIsEditable() != null && codeGen.getIsEditable()) {
            codeGen.setEditableSubmitType("all");
        }
        if (codeGen.getIsCrud()) {
            codeGen.setIsShowOptcol(true);
            codeGen.setIsShowOptcolEdit(true);
            codeGen.setIsShowOptcolDel(true);
        }
        codeGen.setTableDefaultSortColumn("id");
        codeGen.setTableDefaultSortType("desc");
        codeGen.setIsViewUsePath(true);
        codeGen.setHtmlViewPath(FileUtil.normalize("/_view/" + codeGen.getControllerPath().toLowerCase()));
        codeGen.setRoutesScanPackage(codeGen.getMainJavaPackage());
        codeGen.setIsNeedAdminInterceptor(true);
        if (isOk(codeGen.getMainTableRemark())) {
            codeGen.setModelTitle(codeGen.getMainTableRemark());
        }else{
            codeGen.setModelTitle(codeGen.getModelName());
        }
        boolean success = codeGen.save();
        if (success) {
            codeGenModelAttrService.genMainTableAttrs(codeGen.getId());
            processCodeGenRecoverById(codeGen.getId());
        }
        return ret(success);
    }

    private void processCacheSettings(CodeGen codeGen) {
        boolean hasNameAttr = codeGenModelAttrService.checkHasNameAttr(codeGen.getId());
        codeGen.setIsCacheGetName(hasNameAttr);
        boolean hasSnAttr = codeGenModelAttrService.checkHasSnAttr(codeGen.getId());
        codeGen.setIsCacheGetBySn(hasSnAttr);
        codeGen.setIsCacheGetNameBySn(hasNameAttr && hasSnAttr);
        codeGen.setIsCacheGetSn(hasSnAttr);
    }

    /**
     * 处理是否需要recover
     * @param codeGen
     */
    private void processCodeGenRecover(CodeGen codeGen) {
        boolean hasIsDeletedColumn = codeGenModelAttrService.checkHasIsDeletedColumn(codeGen.getId());
        codeGen.setIsToolbarRecoverBtn(hasIsDeletedColumn && codeGen.getIsToolbar());
        codeGen.setIsShowOptcolRecover(hasIsDeletedColumn && codeGen.getIsShowOptcol());
    }

    /**
     * 处理是否需要recover
     * @param id
     */
    public void processCodeGenRecoverById(Long id) {
        CodeGen codeGen = findById(id);
        if(codeGen == null){
            throw new RuntimeException(JBoltMsg.DATA_NOT_EXIST);
        }
        boolean hasIsDeletedColumn = codeGenModelAttrService.checkHasIsDeletedColumn(id);
        codeGen.setIsToolbarRecoverBtn(hasIsDeletedColumn && codeGen.getIsToolbar());
        codeGen.setIsShowOptcolRecover(hasIsDeletedColumn && codeGen.getIsShowOptcol());
        boolean success = codeGen.update();
        if(!success){
            throw new RuntimeException("更新假删相关配置时发生异常");
        }
    }

    private void processCodeGenPrefixAndPackages(CodeGen codeGen) {
        String tableRemovePrefix = codeGen.getTableRemovePrefix();
        String mainTableName = codeGen.getMainTableName();
        if (isOk(tableRemovePrefix)) {
            mainTableName = StrUtil.removePrefixIgnoreCase(mainTableName, tableRemovePrefix);
        }
        String mainTableNameCamelCase = StrUtil.toCamelCase(mainTableName);
        String modelName = StrUtil.upperFirst(mainTableNameCamelCase);
        String oldModelName = codeGen.getModelName();
        boolean mustReplace = StrKit.notBlank(oldModelName) && !modelName.equals(oldModelName);
        codeGen.setModelName(modelName);
        codeGen.setBaseModelName("Base" + modelName);
        String mainJavaPackage = codeGen.getMainJavaPackage();
        if (notOk(mainJavaPackage)) {
            mainJavaPackage = "cn.jbolt";
            codeGen.setMainJavaPackage(mainJavaPackage);
        }
        if (notOk(codeGen.getModelPackage())) {
            codeGen.setModelPackage(mainJavaPackage + "." + modelName.toLowerCase() + ".model");
        } else if (mustReplace) {
            codeGen.setModelPackage(codeGen.getModelPackage().replace(oldModelName.toLowerCase(), modelName.toLowerCase()));
        }
        if (notOk(codeGen.getControllerPackage())) {
            codeGen.setControllerPackage(mainJavaPackage + "." + modelName.toLowerCase());
        } else if (mustReplace) {
            codeGen.setControllerPackage(codeGen.getControllerPackage().replace(oldModelName.toLowerCase(), modelName.toLowerCase()));
        }
        if (notOk(codeGen.getServicePackage())) {
            codeGen.setServicePackage(mainJavaPackage + "." + modelName.toLowerCase());
        } else if (mustReplace) {
            codeGen.setServicePackage(codeGen.getServicePackage().replace(oldModelName.toLowerCase(), modelName.toLowerCase()));
        }

        if (notOk(codeGen.getCacheClassPackage())) {
            codeGen.setCacheClassPackage(mainJavaPackage + "." + modelName.toLowerCase());
        } else if (mustReplace) {
            codeGen.setCacheClassPackage(codeGen.getCacheClassPackage().replace(oldModelName.toLowerCase(), modelName.toLowerCase()));
        }

        if (notOk(codeGen.getControllerName())) {
            codeGen.setControllerName(modelName + codeGen.getType() + "Controller");
        } else if (mustReplace) {
            codeGen.setControllerName(codeGen.getControllerName().replace(oldModelName, modelName));
        }
        if (notOk(codeGen.getControllerPath())) {
            codeGen.setControllerPath("/" + codeGen.getType().toLowerCase() + "/" + StrKit.firstCharToLowerCase(modelName));
        } else if (mustReplace) {
            String newPath = codeGen.getControllerPath().replace(StrKit.firstCharToLowerCase(oldModelName), StrKit.firstCharToLowerCase(modelName));
            if (newPath.charAt(0) != '/') {
                newPath = "/" + newPath;
            }
            codeGen.setControllerPath(newPath);
        }
        if (notOk(codeGen.getServiceName())) {
            codeGen.setServiceName(modelName + "Service");
        } else if (mustReplace) {
            codeGen.setServiceName(codeGen.getServiceName().replace(oldModelName, modelName));
        }

        if (notOk(codeGen.getCacheClassName())) {
            codeGen.setCacheClassName(modelName + "Cache");
        } else if (mustReplace) {
            codeGen.setCacheClassName(codeGen.getCacheClassName().replace(oldModelName, modelName));
        }

    }

    public void processVersionSn(CodeGen codeGen) {
        if (notOk(codeGen.getVersionSn())) {
            codeGen.setVersionSn(getNextVersion(codeGen.getDatasourceName(), codeGen.getMainTableName(), codeGen.getType()));
        }
    }

    public int getNextVersion(String datasource, String tableName, String type) {
        Integer max = queryInt(selectSql().select("version_sn").eq("datasource_name", datasource).eq("main_table_name", tableName).firstPage(1).orderBy("version_sn", true).eq("type", type));
        return max == null ? 1 : (max.intValue() + 1);
    }

    /**
     * 更新主配置
     *
     * @param codeGen
     * @return
     */
    public Ret update(CodeGen codeGen) {
        Ret ret = checkPara(codeGen, true);
        if (ret.isFail()) {
            return ret;
        }
        CodeGen db = findById(codeGen.getId());
        if (db == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        processVersionSn(codeGen);
        if ((isOk(codeGen.getTableRemovePrefix()) && isOk(db.getTableRemovePrefix()) && !codeGen.getTableRemovePrefix().equalsIgnoreCase(db.getTableRemovePrefix())) || isOk(codeGen.getTableRemovePrefix()) && notOk(db.getTableRemovePrefix()) || notOk(codeGen.getTableRemovePrefix()) && isOk(db.getTableRemovePrefix())) {
            processCodeGenPrefixAndPackages(codeGen);
        }
        if (isOk(codeGen.getMainTableRemark()) || notOk(db.getIndexHtmlPageTitle())) {
            codeGen.setIndexHtmlPageTitle(codeGen.getMainTableRemark());
        }
        codeGen.setUpdateUserId(JBoltUserKit.getUserId());
        codeGen.setState(CodeGenState.NOT_GEN.getValue());
        if(isOk(codeGen.getIsCrud())){
            codeGen.setIsShowOptcol(codeGen.getIsCrud());
        }
        if (notOk(codeGen.getModelTitle())) {
            codeGen.setModelTitle(StrKit.defaultIfBlank(codeGen.getIndexHtmlPageTitle(),codeGen.getModelName()));
        }
        boolean success = codeGen.update();
        if(success){
            processCodeGenRecoverById(codeGen.getId());
        }
        return ret(success);
    }

    /**
     * 更新主配置base
     *
     * @param codeGen
     * @return
     */
    public Ret updateBase(CodeGen codeGen) {
        if (notOk(codeGen.getId())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        codeGen.setUpdateUserId(JBoltUserKit.getUserId());
        codeGen.setState(CodeGenState.NOT_GEN.getValue());
        boolean success = codeGen.update();
        if(success){
            processCodeGenRecoverById(codeGen.getId());
        }
        return ret(success);
    }

    private Ret checkPara(CodeGen codeGen, boolean update) {
        if (codeGen == null || notOk(codeGen.getType()) || (update && notOk(codeGen.getId())) || (!update && isOk(codeGen.getId()))) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        if (notOk(codeGen.getDatasourceName())) {
            return fail("未指定数据源");
        }
        if (notOk(codeGen.getMainTableName())) {
            return fail("未指定数据表");
        }
        if (notOk(codeGen.getStyle())) {
            return fail("未指定样式类型");
        }
        if (notOk(codeGen.getAuthor())) {
            return fail("未指定功能作者");
        }
        if (notOk(codeGen.getMainJavaPackage())) {
            return fail("未指定Java主包package");
        }
        return Ret.ok();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public Ret delete(Long id) {
        return deleteById(id);
    }

    /**
     * 删除codeGen数据之后额外要处理的
     */
    @Override
    protected String afterDelete(CodeGen codeGen, Kv kv) {
        //因为这个表是假删除 所以这里就不真删除子表数据了
        //codeGenModelAttrService.deleteByCodeGenId(codeGen.getId());
        return null;
    }

    /**
     * 删除codeGen数据之后额外要处理的
     */
    @Override
    protected String afterRealDelete(CodeGen codeGen, Kv kv) {
        if (isOk(codeGen.getId())) {
            deleteByPid(codeGen.getId());
            codeGenModelAttrService.deleteByCodeGenId(codeGen.getId());
        }
        return null;
    }

    /**
     * 清空解绑权限菜单
     *
     * @param id
     * @return
     */
    public Ret unbindPermission(Long id) {
        update(updateSql().set("topnav_id", new SqlExpress("NULL")).set("permission_id", new SqlExpress("NULL")).idEq(id));
        return SUCCESS;
    }

    /**
     * 更新 table列表页面查询条件里的关键词查询匹配相关
     *
     * @param codeGenId
     * @return
     */
    public Ret updateKeywordsMatchColumn(Long codeGenId) {
        if (notOk(codeGenId)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        CodeGen codeGen = findById(codeGenId);
        if (codeGen == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        List<CodeGenModelAttr> attrs = codeGenModelAttrService.getKeywordsMatchColumns(codeGenId);
        if (isOk(attrs)) {
            codeGen.setIsKeywordsSearch(true);
            codeGen.setKeywordsMatchColumns(ArrayUtil.join(JBoltArrayUtil.getStringArray(attrs, "col_name"), ","));
        } else {
            codeGen.setIsKeywordsSearch(false);
            codeGen.setKeywordsMatchColumns(null);
        }
        boolean success = codeGen.update();
        return ret(success);
    }


    @Override
    protected int systemLogTargetType() {
        return 0;
    }

    /**
     * 全部生成
     *
     * @param codeGenId
     * @param cover
     * @return
     */
    public Ret genAll(Long codeGenId, boolean cover) {
        if (notOk(codeGenId)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        CodeGen codeGen = findById(codeGenId);
        if (codeGen == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        //1、model与baseModel
        genModel(codeGen, cover);
        //2、mainLogic
        genMainLogic(codeGen, cover);
        if(codeGen.getState().intValue()!=CodeGenState.GENED.getValue()){
            //3、更新生成状态
            codeGen.setState(CodeGenState.GENED.getValue());
            boolean success = codeGen.update();
            if(!success){
                return fail("代码生成异常");
            }
        }
        return SUCCESS;
    }
    /**
     * 生成 controller service html等
     *
     * @param codeGenId
     * @param cover
     * @return
     */
    public Ret genMainLogic(Long codeGenId, boolean cover) {
        if (notOk(codeGenId)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        CodeGen codeGen = findById(codeGenId);
        if (codeGen == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        return genMainLogic(codeGen, cover);
    }
    /**
     * 生成 controller service html等
     *
     * @param codeGen
     * @param cover
     */
    private Ret genMainLogic(CodeGen codeGen, boolean cover) {
        //1、重新生成PermissionKey
        genPermissionKeys(codeGen.getProjectPath());
        //2、生成依赖的ProjectSystemNoticeTargetType
        genProjectSystemLogTargetType(codeGen);
        //3、生成service
        genService(codeGen, cover);
        //4、生成controller
        genController(codeGen, cover);
        //4.1、生成cache工具类
        genCache(codeGen, cover);
        //5、路由配置
        genRoutes(codeGen);
        //6、生成html
        genHtml(codeGen, cover);
        //7、更新生成状态
        if(codeGen.getState() == CodeGenState.ONLY_MODEL_GEN.getValue()){
            codeGen.setState(CodeGenState.GENED.getValue());
        }else{
            codeGen.setState(CodeGenState.ONLY_MAIN_LOGIC_GEN.getValue());
        }
        boolean success = codeGen.update();
        return success ? success("主逻辑代码生成成功，请刷新项目目录") : fail("主逻辑代码生成异常，请检查后重试");
    }

    /**
     * 生成cache类
     * @param codeGen
     * @param cover
     */
    private void genCache(CodeGen codeGen, boolean cover) {
        if(!codeGen.getIsAutoCache() || !codeGen.getIsGenCacheUtilClass()){
            JBoltConsoleUtil.printMessageWithDate("检测不满足生成缓存工具类条件,忽略此缓存工具类生成...");
            return;
        }
        String cacheFullPath = FileUtil.normalize(getDirFromPackage(codeGen.getProjectPath(), codeGen.getCacheClassPackage()) + "/" + codeGen.getCacheClassName() + ".java");
        JBoltConsoleUtil.printMessageWithDate("正在处理生成Cache:" + cacheFullPath);
        boolean exists = FileUtil.exist(cacheFullPath);
        if (exists) {
            if (!cover) {
                //如果已经存在 并且没有强制覆盖 就直接返回
                JBoltConsoleUtil.printMessageWithDate("检测Cache已经存在,忽略此Cache生成...");
                return;
            }
            JBoltConsoleUtil.printMessageWithDate("检测Cache已经存在，生成内容将覆盖已存在内容...");
        } else {
            JBoltConsoleUtil.printMessageWithDate("检测Cache不存在，直接生成Java文件...");
        }

        //执行生成Controller的内容
        String content = genCacheJavaCode(codeGen);
        if (StrKit.isBlank(content)) {
            JBoltConsoleUtil.printErrorMessageWithDate("Cache 生成内容为空");
            throw new RuntimeException("Cache 生成内容为空");
        }

        //覆盖写入内容
        File controllerFile = FileUtil.writeUtf8String(content, cacheFullPath);
        if (controllerFile == null || !controllerFile.exists()) {
            JBoltConsoleUtil.printErrorMessageWithDate("Cache 生成过程发生异常，未能生成java文件");
            throw new RuntimeException("Cache 生成过程发生异常，未能生成java文件");
        }
    }

    /**
     * 生成cache工具类的源码
     * @param codeGen
     * @return
     */
    private String genCacheJavaCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        // 处理所需生成的方法名
        data.set("paramIdType", getParamIdTypeByGenMode(codeGen.getMainTableIdgenmode()));
        //执行生成 返回内容
        return engine.getTemplate(cacheTemplate).renderToString(data);
    }


    /**
     * 生成路由设置
     *
     * @param codeGen
     */
    private void genRoutes(CodeGen codeGen) {
        String javaFileFullPath = FileUtil.normalize(codeGen.getProjectPath() + "/src/main/java/cn/jbolt/extend/config/ProjectCodeGenRoutesConfig.java");
        if (codeGen.getIsViewUsePath()) {
            if (StrKit.notBlank(codeGen.getRoutesScanPackage())) {
                String code = "me.scan(\"" + codeGen.getRoutesScanPackage() + "\");";
                //如果存在了 就判断 就判断有没有 没有就插入 有就忽略
                List<String> codeLines = FileUtil.readLines(javaFileFullPath, CharsetUtil.UTF_8);
                if (codeLines == null || codeLines.size() == 0) {
                    throw new RuntimeException(javaFileFullPath + "文件内容异常");
                }

                String searchCode = "public static void config(Routes me) {";
                int insertIndex = -1;
                int size = codeLines.size();
                String currentLine;
                boolean hasSeachCode = false;
                for (int line = 0; line < size; line++) {
                    currentLine = codeLines.get(line).trim();
                    if (code.equals(currentLine)) {
                        //已经存在相同的就不处理了
                        return;
                    }
                    if (!hasSeachCode && searchCode.equals(currentLine)) {
                        hasSeachCode = true;
                    }
                    if (hasSeachCode && "}".equals(currentLine)) {
                        insertIndex = line;
                        break;
                    }
                }

                if (insertIndex > 0) {
                    codeLines.add(insertIndex, "\t\t" + code);
                    //执行重新生成
                    FileUtil.writeLines(codeLines, javaFileFullPath, CharsetUtil.UTF_8);
                }
            }
        }



        /*//如果设置了指定了routes
        if (StrKit.notBlank(codeGen.getRoutesClassName())) {
            String javaFileFullPath = FileUtil.normalize(codeGen.getProjectPath() + SEPARATOR + StrUtil.replace(codeGen.getRoutesClassName(), ".", SEPARATOR) + ".java");
            String code = null;
            if (codeGen.getIsViewUsePath()) {
                if (StrKit.isBlank(codeGen.getRoutesScanPackage())) {
                    throw new RuntimeException("未设置路由扫描的包");
                }
                code = "this.scan(\"" + codeGen.getRoutesScanPackage() + "\");";
            } else {
                code = "this.add(\"" + codeGen.getControllerPath() + "\", " + codeGen.getControllerName() + ".class,\"" + codeGen.getHtmlViewPath() + "\");";
            }
            //判断设置为使用新的
            if (codeGen.getIsNeedNewRoute()) {
                //如果需要新创建的routes 类 并且设置值了className
                createNewRoutesClass(codeGen,javaFileFullPath,code);
            } else {
                //如果设置为使用已存在的
                processRoutesAdd(codeGen,javaFileFullPath,code);
            }
        }*/
    }

    private void processRoutesAdd(CodeGen codeGen, String javaFileFullPath, String code) {
        //如果存在了 就判断 就判断有没有 没有就插入 有就忽略
        List<String> codeLines = FileUtil.readLines(javaFileFullPath, CharsetUtil.UTF_8);
        if (codeLines == null || codeLines.size() == 0) {
            throw new RuntimeException(javaFileFullPath + "文件内容异常");
        }

        String searchCode = "public void config() {";
        int insertIndex = -1;
        int size = codeLines.size();
        String currentLine;
        boolean hasSeachCode = false;
        for (int line = 0; line < size; line++) {
            currentLine = codeLines.get(line).trim();
            if (!hasSeachCode && searchCode.equals(currentLine)) {
                hasSeachCode = true;
            }
            if (hasSeachCode && "}".equals(currentLine)) {
                insertIndex = line;
                break;
            }
        }

        if (insertIndex > 0) {
            codeLines.add(insertIndex, code);
            //执行重新生成
            FileUtil.writeLines(codeLines, javaFileFullPath, CharsetUtil.UTF_8);
        }

    }

    private void createNewRoutesClass(CodeGen codeGen, String javaFileFullPath, String code) {

        if (FileUtil.exist(javaFileFullPath)) {
            throw new RuntimeException("指定路由已经存在了");
        }

        //如果不存在 就直接生成
        Engine engine = getCodeGenTplEngine();
        Kv data = Kv.by("code", code);
        String routesName = codeGen.getRoutesClassName().substring(codeGen.getRoutesClassName().lastIndexOf(".") + 1);
        String routesPackage = codeGen.getRoutesClassName().substring(0, codeGen.getRoutesClassName().lastIndexOf("."));
        data.set("routesName", routesName);
        data.set("routesPackage", routesPackage);
        data.set("codeGen", codeGen);
        //渲染模板
        String content = engine.getTemplate(routesTemplate).renderToString();
        //写出java文件
        FileUtil.writeUtf8String(content, javaFileFullPath);
    }

    /**
     * 检查是否存在一行里包含指定代码
     *
     * @param codeLines
     * @param code
     * @return
     */
    private boolean checkFileContainCode(List<String> codeLines, String code) {
        String tmpCode;
        for (String codeLine : codeLines) {
            tmpCode = codeLine.trim();
            if (StrKit.isBlank(tmpCode)
                    ||
                    tmpCode.startsWith("package ")
                    ||
                    tmpCode.startsWith("public class ")
                    ||
                    tmpCode.startsWith("return ")
                    ||
                    tmpCode.equals("}")
            ) {
                continue;
            }
            if (codeLine.indexOf(code) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成ProjectSystemLogTargetType
     *
     * @param codeGen
     */
    private void genProjectSystemLogTargetType(CodeGen codeGen) {
        if (!codeGen.getIsProjectSystemLog()) {
            return;
        }
        //ProjectSystemLogTargetType.java的绝对路径
        String ProjectSystemLogTargetType_PATH = FileUtil.normalize(codeGen.getProjectPath() + SEPARATOR + "src" + SEPARATOR + "main" + SEPARATOR + "java" + SEPARATOR + "cn" + SEPARATOR + "jbolt" + SEPARATOR + "extend" + SEPARATOR + "systemlog" + SEPARATOR + "ProjectSystemLogTargetType.java");
        JBoltConsoleUtil.printMessageWithDate("正在生成ProjectSystemLogTargetType...");
        boolean exists = FileUtil.exist(ProjectSystemLogTargetType_PATH);
        if (!exists) {
            throw new RuntimeException(ProjectSystemLogTargetType_PATH + "文件不存在");
        }

        List<String> codeLines = FileUtil.readLines(ProjectSystemLogTargetType_PATH, CharsetUtil.UTF_8);
        if (codeLines == null || codeLines.size() == 0) {
            throw new RuntimeException(ProjectSystemLogTargetType_PATH + "文件内容异常");
        }
        String code = codeGen.getProjectSystemLogTargetTypeKeyName() + "(\"" + codeGen.getProjectSystemLogTargetTypeText() + "\"," + codeGen.getProjectSystemLogTargetTypeValue() + ")";
        //如果已经存在 就略过
        if (checkFileContainCode(codeLines, code)) {
            JBoltConsoleUtil.printMessage(String.format("ProjectSystemLogTargetType 文件已经存在[%s]定义，略过！", code));
            return;
        }
        int size = codeLines.size();
        String tmpCode;
        int insertIndex = 0;
        int startIndex = -1;
        for (int i = 1; i < size; i++) {
            tmpCode = codeLines.get(i);
            if (StrKit.isBlank(tmpCode)) {
                continue;
            }
            if (startIndex == -1 && tmpCode.trim().startsWith("public enum ProjectSystemLogTargetType")) {
                startIndex = i + 1;
            }
            if (tmpCode.trim().equals(";")) {
                insertIndex = i;
            }
        }
        boolean needProcessUpComma = true;
        if (insertIndex == 0) {
            insertIndex = startIndex;
            code = code + ",";
            needProcessUpComma = false;
        }

        if (startIndex > 0) {
            codeLines.add(insertIndex, "\t" + code);
            if (needProcessUpComma) {
                String tmp;
                String tmpTrim;
                for (int i = insertIndex - 1; i > startIndex; i--) {
                    tmp = codeLines.get(i);
                    if (StrKit.isBlank(tmp)) {
                        continue;
                    }
                    tmpTrim = tmp.trim();
                    if (tmpTrim.endsWith(",") || tmpTrim.endsWith(";") || tmpTrim.endsWith("{") || tmpTrim.endsWith("}")) {
                        break;
                    }
                    if (tmpTrim.charAt(tmpTrim.length() - 1) != ',') {
                        codeLines.set(i, "\t" + tmpTrim + ",");
                        break;
                    }
                }

            }
            JBoltConsoleUtil.printMessageWithDate("正在重新生成新的ProjectSystemLogTargetType...");
            //执行重新生成
            FileUtil.writeLines(codeLines, ProjectSystemLogTargetType_PATH, CharsetUtil.UTF_8);
        }
    }

    /**
     * 重新生成permissionKey
     *
     * @param projectPath
     */
    private void genPermissionKeys(String projectPath) {
        JBoltConsoleUtil.printMessageWithDate("正在生成PermissionKey...");
        JBoltInnerPermissionKeyGen.gen(projectPath);
    }

    /**
     * 生成html
     *
     * @param codeGen
     * @param cover
     */
    private void genHtml(CodeGen codeGen, boolean cover) {
        JBoltConsoleUtil.printMessageWithDate("正在生成Html...");
        //1、生成index.html
        genIndexHtml(codeGen, cover);
        if(codeGen.getIsCrud()){
            //2、生成add.html
            genAddHtml(codeGen, cover);
            //3、生成edit.html
            genEditHtml(codeGen, cover);
            //4、生成form.html
            genFormHtml(codeGen, cover);
            //5、生成importExcel.html
            genImportExcelHtml(codeGen, cover);
        }
        //5、生成detail.html
        genDetailHtml(codeGen, cover);
    }

    /**
     * 生成importExcel.html
     * @param codeGen
     * @param cover
     */
    private void genImportExcelHtml(CodeGen codeGen, boolean cover) {
        if(!codeGen.getIsImportExcel()){
            return;
        }
        String htmlFilePath = FileUtil.normalize(codeGen.getProjectPath() + "/src/main/webapp" + codeGen.getHtmlViewPath() + "/import_excel.html");
        JBoltConsoleUtil.printMessageWithDate("正在生import_excel,路径:" + htmlFilePath);
        if (FileUtil.exist(htmlFilePath) && !cover) {
            JBoltConsoleUtil.printMessageWithDate("import_excel已存在,忽略生成...");
            return;
        }
        //准备内容
        String content = genImportExcelHtmlCode(codeGen);
        //写入_form.html
        FileUtil.writeUtf8String(content, htmlFilePath);
    }

    /**
     * 生成import_excel.html
     * @param codeGen
     * @return
     */
    private String genImportExcelHtmlCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenImportExcelHtmlTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        // 准备数据
        data.set("codeGenServiceMode", true);
        //执行生成 返回内容
        return engine.getTemplate(importExcelHtmlTemplate).renderToString(data);
    }

    private void genDetailHtml(CodeGen codeGen, boolean cover) {
    }

    /**
     * 生成_form.html
     * @param codeGen
     * @param cover
     */
    private void genFormHtml(CodeGen codeGen, boolean cover) {
        String htmlFilePath = FileUtil.normalize(codeGen.getProjectPath() + "/src/main/webapp" + codeGen.getHtmlViewPath() + "/_form.html");
        JBoltConsoleUtil.printMessageWithDate("正在生_form.html,路径:" + htmlFilePath);
        if (FileUtil.exist(htmlFilePath) && !cover) {
            JBoltConsoleUtil.printMessageWithDate("_form.html已存在,忽略生成...");
            return;
        }
        //准备内容
        String content = genFormHtmlCode(codeGen);
        //写入_form.html
        FileUtil.writeUtf8String(content, htmlFilePath);
    }

    /**
     * 生成edit.html
     *
     * @param codeGen
     * @param cover
     */
    private void genEditHtml(CodeGen codeGen, boolean cover) {
        String htmlFilePath = FileUtil.normalize(codeGen.getProjectPath() + "/src/main/webapp" + codeGen.getHtmlViewPath() + "/edit.html");
        JBoltConsoleUtil.printMessageWithDate("正在生成edit.html,路径:" + htmlFilePath);
        if (FileUtil.exist(htmlFilePath) && !cover) {
            JBoltConsoleUtil.printMessageWithDate("edit.html已存在,忽略生成...");
            return;
        }
        //准备内容
        String content = genEditHtmlCode(codeGen);
        //写入edit.html
        FileUtil.writeUtf8String(content, htmlFilePath);
    }

    /**
     * 生成add.html
     *
     * @param codeGen
     * @param cover
     */
    private void genAddHtml(CodeGen codeGen, boolean cover) {
        String htmlFilePath = FileUtil.normalize(codeGen.getProjectPath() + "/src/main/webapp" + codeGen.getHtmlViewPath() + "/add.html");
        JBoltConsoleUtil.printMessageWithDate("正在生成add.html,路径:" + htmlFilePath);
        if (FileUtil.exist(htmlFilePath) && !cover) {
            JBoltConsoleUtil.printMessageWithDate("add.html已存在,忽略生成...");
            return;
        }
        //准备内容
        String content = genAddHtmlCode(codeGen);
        //写入add.html
        FileUtil.writeUtf8String(content, htmlFilePath);
    }

    /**
     * 生成add.html的代码
     *
     * @param codeGen
     * @return
     */
    private String genAddHtmlCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        // 表单action
        data.set("action", codeGen.getControllerPath() + "/save");
        //执行生成 返回内容
        return engine.getTemplate(addhtmlTemplate).renderToString(data);
    }

    /**
     * 生成edit.html的代码
     *
     * @param codeGen
     * @return
     */
    private String genEditHtmlCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        // 表单action
        data.set("action", codeGen.getControllerPath() + "/update");
        //执行生成 返回内容
        return engine.getTemplate(edithtmlTemplate).renderToString(data);
    }

    /**
     * 生成index.html
     *
     * @param codeGen
     * @param cover
     */
    private void genIndexHtml(CodeGen codeGen, boolean cover) {
        String htmlFilePath = FileUtil.normalize(codeGen.getProjectPath() + "/src/main/webapp" + codeGen.getHtmlViewPath() + "/index.html");
        JBoltConsoleUtil.printMessageWithDate("正在生index.html,路径:" + htmlFilePath);
        if (FileUtil.exist(htmlFilePath) && !cover) {
            JBoltConsoleUtil.printMessageWithDate("index.html已存在,忽略生成...");
            return;
        }
        //准备内容
        String content = genIndexHtmlCode(codeGen);
        //写入index.html
        FileUtil.writeUtf8String(content, htmlFilePath);
    }

    /**
     * index.html内容生成
     *
     * @param codeGen
     * @return
     */
    private String genIndexHtmlCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenIndexHtmlTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        // 准备数据
        data.set("codeGenServiceMode", true);
        data.set("sortableColumns", codeGenModelAttrService.getSortableColumnsStr(codeGen.getId()));
        data.set("cols", codeGenModelAttrService.getCodeGenTableColumns(codeGen.getId()));
        data.set("conditions", codeGenModelAttrService.getCodeGenTableConditions(codeGen.getId()));
        boolean hasIsDeletedColumn = codeGenModelAttrService.checkHasIsDeletedColumn(codeGen.getId());
        data.set("hasIsDeletedColumn",hasIsDeletedColumn);
        boolean hasIsKeywordsColumn = codeGenModelAttrService.checkHasIsKeywordsColumn(codeGen.getId());
        data.set("hasIsKeywordsColumn",hasIsKeywordsColumn);
        //执行生成 返回内容
        return engine.getTemplate(indexhtmlTemplate).renderToString(data);
    }

    /**
     * _form.html内容生成
     *
     * @param codeGen
     * @return
     */
    private String genFormHtmlCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenFormHtmlTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        // 准备数据
        data.set("codeGenServiceMode", true);
        data.set("primaryKey", codeGen.getMainTablePkey());
        data.set("editMode", false);
        data.set("colDatas", codeGenModelAttrService.getCodeGenFormColDatas(codeGen.getId(),false));

        //执行生成 返回内容
        return engine.getTemplate(formhtmlTemplate).renderToString(data);
    }

    /**
     * 生成service
     *
     * @param codeGen
     * @param cover
     */
    private void genService(CodeGen codeGen, boolean cover) {
        String serviceFullPath = FileUtil.normalize(getDirFromPackage(codeGen.getProjectPath(), codeGen.getServicePackage()) + "/" + codeGen.getServiceName() + ".java");
        JBoltConsoleUtil.printMessageWithDate("正在处理生成Service:" + serviceFullPath);
        boolean exists = FileUtil.exist(serviceFullPath);
        if (exists) {
            if (!cover) {
                //如果已经存在 并且没有强制覆盖 就直接返回
                JBoltConsoleUtil.printMessageWithDate("检测Service已经存在,忽略此Service生成...");
                return;
            }
            JBoltConsoleUtil.printMessageWithDate("检测Service是已经存在，生成内容将覆盖已存在内容...");
        } else {
            JBoltConsoleUtil.printMessageWithDate("检测Service不存在，直接生成Java文件...");
        }

        //执行生成Service的内容
        String content = genServiceJavaCode(codeGen);
        if (StrKit.isBlank(content)) {
            JBoltConsoleUtil.printErrorMessageWithDate("Service 生成内容为空");
            throw new RuntimeException("Service 生成内容为空");
        }

        //覆盖写入内容
        File serviceFile = FileUtil.writeUtf8String(content, serviceFullPath);
        if (serviceFile == null || !serviceFile.exists()) {
            JBoltConsoleUtil.printErrorMessageWithDate("Service 生成过程发生异常，未能生成java文件");
            throw new RuntimeException("Service 生成过程发生异常，未能生成java文件");
        }
    }

    /**
     * controller生成
     *
     * @param codeGen
     * @param cover
     */
    private void genController(CodeGen codeGen, boolean cover) {
        String controllerFullPath = FileUtil.normalize(getDirFromPackage(codeGen.getProjectPath(), codeGen.getControllerPackage()) + "/" + codeGen.getControllerName() + ".java");
        JBoltConsoleUtil.printMessageWithDate("正在处理生成Controller:" + controllerFullPath);
        boolean exists = FileUtil.exist(controllerFullPath);
        if (exists) {
            if (!cover) {
                //如果已经存在 并且没有强制覆盖 就直接返回
                JBoltConsoleUtil.printMessageWithDate("检测Controller已经存在,忽略此Controller生成...");
                return;
            }
            JBoltConsoleUtil.printMessageWithDate("检测Controller已经存在，生成内容将覆盖已存在内容...");
        } else {
            JBoltConsoleUtil.printMessageWithDate("检测Controller不存在，直接生成Java文件...");
        }

        //执行生成Controller的内容
        String content = genControllerJavaCode(codeGen);
        if (StrKit.isBlank(content)) {
            JBoltConsoleUtil.printErrorMessageWithDate("Controller 生成内容为空");
            throw new RuntimeException("Controller 生成内容为空");
        }

        //覆盖写入内容
        File controllerFile = FileUtil.writeUtf8String(content, controllerFullPath);
        if (controllerFile == null || !controllerFile.exists()) {
            JBoltConsoleUtil.printErrorMessageWithDate("Controller 生成过程发生异常，未能生成java文件");
            throw new RuntimeException("Controller 生成过程发生异常，未能生成java文件");
        }
    }


    /**
     * 模板生成Controller内容
     *
     * @param codeGen
     * @return
     */
    private String genControllerJavaCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        boolean hasIsDeletedColumn = codeGenModelAttrService.checkHasIsDeletedColumn(codeGen.getId());
        data.set("hasIsDeletedColumn",hasIsDeletedColumn);
        // 处理所需生成的方法名
        data.set("methods", processContrllerGenMethods(codeGen,hasIsDeletedColumn,data));
        data.set("getIdType", getGetIdTypeByGenMode(codeGen.getMainTableIdgenmode()));
        data.set("needSort", codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolSort());
        data.set("sortableColumns", codeGenModelAttrService.getSortableColumnsStr(codeGen.getId()));
        data.set("paramIdType", getParamIdTypeByGenMode(codeGen.getMainTableIdgenmode()));
        data.set("needUnCheck", (codeGen.getIsGenOptionsAction() || codeGen.getIsGenAutocompleteAction()));

        List<CodeGenModelAttr> conditions = codeGenModelAttrService.getCodeGenSearchConditionsAttrs(codeGen.getId());
        List<CodeGenModelAttr> keywordsSearchColumns = codeGenModelAttrService.getCodeGenSearchKeywordsColumnAttrs(codeGen.getId());
        processHasDateTimeColumn(conditions, data);
        processHasDateTimeColumn(keywordsSearchColumns, data);
        data.set("conditions", conditions);
        data.set("hasIsKeywordsColumn",isOk(keywordsSearchColumns));
        data.set("permission", JBoltPermissionCache.me.get(codeGen.getPermissionId()));

        //执行生成 返回内容
        return engine.getTemplate(controllerTemplate).renderToString(data);
    }

    private void processHasDateTimeColumn(List<CodeGenModelAttr> columns, Kv data) {
        if (isOk(columns)) {
            Boolean hasDateColumn = data.getAs("hasDateColumn", false);
            Boolean hasTimeColumn = data.getAs("hasTimeColumn", false);
            Boolean hasDateRangeColumn = data.getAs("hasDateRangeColumn", false);
            for (CodeGenModelAttr col : columns) {
                if ("Date".equals(col.getJavaTypeName()) && !hasDateColumn) {
                    hasDateColumn = true;
                } else if ("Time".equals(col.getJavaTypeName()) && !hasTimeColumn) {
                    hasTimeColumn = true;
                }
                if (isOk(col.getSearchUiType()) && col.getSearchUiType().startsWith("laydate_range_") && !hasDateRangeColumn) {
                    hasDateRangeColumn = true;
                }
            }
            data.set("hasDateColumn", hasDateColumn);
            data.set("hasTimeColumn", hasTimeColumn);
            data.set("hasDateRangeColumn", hasDateRangeColumn);
        }
    }

    /**
     * 模板生成Service内容
     *
     * @param codeGen
     * @return
     */
    private String genServiceJavaCode(CodeGen codeGen) {
        //准备模板引擎
        Engine engine = getCodeGenTplEngine();
        // 准备模板数据
        Kv data = Kv.by("codeGen", codeGen);
        boolean hasIsDeletedColumn = codeGenModelAttrService.checkHasIsDeletedColumn(codeGen.getId());
        data.set("hasIsDeletedColumn",hasIsDeletedColumn);
        // 处理所需生成的方法名
        data.set("methods", processServiceGenMethods(codeGen,hasIsDeletedColumn));
        data.set("paramIdType", getParamIdTypeByGenMode(codeGen.getMainTableIdgenmode()));
        data.set("needSort", codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolSort());
        data.set("sortableColumns", codeGenModelAttrService.getSortableColumnsStr(codeGen.getId()));
        data.set("checkExistsColumns", codeGenModelAttrService.getNeedCheckExistsColumns(codeGen.getId()));
        List<CodeGenModelAttr> conditions = codeGenModelAttrService.getCodeGenSearchConditionsAttrs(codeGen.getId());
        List<CodeGenModelAttr> keywordsSearchColumns = codeGenModelAttrService.getCodeGenSearchKeywordsColumnAttrs(codeGen.getId());
        processHasDateTimeColumn(conditions, data);
        processHasDateTimeColumn(keywordsSearchColumns, data);
        data.set("conditions", conditions);
        data.set("keywordsSearchColumns", keywordsSearchColumns);
        data.set("hasIsKeywordsColumn",isOk(keywordsSearchColumns));
        String primaryKey = codeGen.getMainTablePkey();
        if (primaryKey.contains(",")) {
            data.set("getIdMethodName", "getId");
        } else {
            data.set("getIdMethodName", "get" + StrUtil.upperFirst(StrUtil.toCamelCase(primaryKey.toLowerCase())));
        }
        //执行生成 返回内容
        return engine.getTemplate(serviceTemplate).renderToString(data);
    }


    /**
     * 根据ID策略获取IDType
     *
     * @param idGenMode
     * @return
     */
    private String getGetIdTypeByGenMode(String idGenMode) {
        String idType = "Long";
        switch (idGenMode) {
            case JBoltIDGenMode.AUTO:
                idType = "Int";
                break;
            case JBoltIDGenMode.AUTO_LONG:
                idType = "Long";
                break;
            case JBoltIDGenMode.BIGSERIAL:
                idType = "Int";
                break;
            case JBoltIDGenMode.SERIAL:
                idType = "Int";
                break;
            case JBoltIDGenMode.UUID:
                idType = "";
                break;
            case JBoltIDGenMode.SEQUENCE:
                idType = "Int";
                break;
            case JBoltIDGenMode.SEQUENCE_LONG:
                idType = "Long";
                break;
            case JBoltIDGenMode.SNOWFLAKE:
                idType = "Long";
                break;
            default:
                idType = "Long";
                break;
        }
        return idType;
    }

    private String getParamIdTypeByGenMode(String idGenMode) {
        String idType = "Long";
        switch (idGenMode) {
            case JBoltIDGenMode.AUTO:
                idType = "Integer";
                break;
            case JBoltIDGenMode.AUTO_LONG:
                idType = "Long";
                break;
            case JBoltIDGenMode.BIGSERIAL:
                idType = "Integer";
                break;
            case JBoltIDGenMode.SERIAL:
                idType = "Integer";
                break;
            case JBoltIDGenMode.UUID:
                idType = "String";
                break;
            case JBoltIDGenMode.SEQUENCE:
                idType = "Integer";
                break;
            case JBoltIDGenMode.SEQUENCE_LONG:
                idType = "Long";
                break;
            case JBoltIDGenMode.SNOWFLAKE:
                idType = "Long";
                break;
            default:
                idType = "Long";
                break;
        }
        return idType;
    }

    /**
     * 处理Controller方法
     *
     * @param codeGen
     * @param hasIsDeletedColumn
     * @param data
     * @return
     */
    private List<CodeGenMethod> processContrllerGenMethods(CodeGen codeGen,boolean hasIsDeletedColumn,Kv data) {
        // 先得到通用的，然后判断有不需要的就删掉，有特殊的就新增
        List<CodeGenMethod> genMehtods = getCommonControllerMethods(codeGen);
        //处理 需要显示在表格中的boolean字段char(1) 显示为switchBtn 所以生成togglexxx的方法
        processControllerToggleMethods(codeGen, genMehtods);
        //处理上传类列的methods
        processControllerUploadMethods(codeGen,genMehtods,data);
        //处理 isDeleted相关
        if(hasIsDeletedColumn){
            //如果启用了toolbar 并且 启用toolbar上的recoverbtn
            if(codeGen.getIsToolbar() && codeGen.getIsToolbarRecoverBtn()){
                if(codeGen.getIsTablePrependColumn()){
                    //如果是启动了checkbox或者radio
                    if("checkbox".equals(codeGen.getTablePrependColumnType())){
                        genMehtods.add(new CodeGenMethod("recoverByIds"));
                        genMehtods.add(new CodeGenMethod("realDeleteByIds"));
                    }else{
                        genMehtods.add(new CodeGenMethod("recover"));
                        genMehtods.add(new CodeGenMethod("realDelete"));
                    }
                }else{
                    genMehtods.add(new CodeGenMethod("recover"));
                    genMehtods.add(new CodeGenMethod("realDelete"));
                }
            }else if(codeGen.getIsShowOptcolRecover()){
                genMehtods.add(new CodeGenMethod("recover"));
                genMehtods.add(new CodeGenMethod("realDelete"));
            }
        }

        if(codeGen.getIsGenAutocompleteAction()){
            genMehtods.add(new CodeGenMethod("autocompleteDatas"));
        }
        if(codeGen.getIsGenOptionsAction()){
            CodeGenMethod method =new CodeGenMethod("options");
            method.setHasIsDeletedColumn(hasIsDeletedColumn);
            method.setHasEnableColumn(codeGenModelAttrService.checkHasEnableColumn(codeGen.getId()));
            genMehtods.add(method);
        }
        return genMehtods;
    }

    /**
     * 处理生成Controller中的关于上传的action
     * @param codeGen
     * @param genMehtods
     * @param data
     */
    private void processControllerUploadMethods(CodeGen codeGen, List<CodeGenMethod> genMehtods,Kv data) {
        List<CodeGenModelAttr> attrs = codeGenModelAttrService.getCodeGenInFormUploadModelAttrs(codeGen.getId());
        if(notOk(attrs)){return;}
        CodeGenMethod codeGenMethod;
        for(CodeGenModelAttr attr:attrs){
            codeGenMethod = new CodeGenMethod(attr.getFormUploadUrl());
            codeGenMethod.setIsUploadAction(true);
            codeGenMethod.setUploadColName(StrKit.firstCharToLowerCase(codeGen.getModelName())+"/"+attr.getAttrName());
            codeGenMethod.setUploadType(attr.getFormUiType());
            codeGenMethod.setUploadDataName(attr.getFormLabel());
            genMehtods.add(codeGenMethod);
        }
        data.set("hasUploadFile",true);
    }

    /**
     * 处理Service方法
     *
     * @param codeGen
     * @return
     */
    private List<CodeGenMethod> processServiceGenMethods(CodeGen codeGen,boolean hasIsDeletedColumn) {
        // 先得到通用的，然后判断有不需要的就删掉，有特殊的就新增
        List<CodeGenMethod> genMehtods = getCommonServiceMethods(codeGen);
        //处理是否存在
        processServiceToggleMethods(codeGen, genMehtods);
        //处理 isDeleted相关
        if(hasIsDeletedColumn){
             if(codeGen.getIsCheckCanRecover()){
                 genMehtods.add(new CodeGenMethod("checkCanRecover"));
             }
            genMehtods.add(new CodeGenMethod("afterRecover"));
        }
        return genMehtods;
    }

    private void processServiceToggleMethods(CodeGen codeGen, List<CodeGenMethod> genMehtods) {
        //1、获取表中的字段列表属性中是
        List<CodeGenModelAttr> codeGenModelAttrs = codeGenModelAttrService.getTableToggleCodeGenModelAttrs(codeGen.getId());
        if (isOk(codeGenModelAttrs)) {
            if(codeGen.getIsCheckCanToggle()){
                genMehtods.add(new CodeGenMethod("checkCanToggle"));
            }
            genMehtods.add(new CodeGenMethod("afterToggleBoolean", true, JBoltArrayUtil.getStringArray(codeGenModelAttrs, "col_name")));
        }
    }

    /**
     * 处理 需要显示在表格中的boolean字段char(1) 显示为switchBtn 所以生成togglexxx的方法
     *
     * @param codeGen
     * @param genMehtods
     */
    private void processControllerToggleMethods(CodeGen codeGen, List<CodeGenMethod> genMehtods) {
        //1、获取表中的字段列表属性中是
        List<CodeGenModelAttr> codeGenModelAttrs = codeGenModelAttrService.getTableToggleCodeGenModelAttrs(codeGen.getId());
        if (isOk(codeGenModelAttrs)) {
            for (CodeGenModelAttr codeGenModelAttr : codeGenModelAttrs) {
                genMehtods.add(new CodeGenMethod("toggle" + StrKit.firstCharToUpperCase(codeGenModelAttr.getAttrName()), true, codeGenModelAttr.getColName()));
            }
        }
    }

    private List<CodeGenMethod> getCommonControllerMethods(CodeGen codeGen) {
        List<CodeGenMethod> genMehtods = new ArrayList<CodeGenMethod>();
        genMehtods.add(new CodeGenMethod("index"));
        if (codeGen.getIsCrud()) {
            if (codeGen.getIsPageTitleAddBtn() || (codeGen.getIsToolbar() && codeGen.getIsToolbarAddBtn())) {
                genMehtods.add(new CodeGenMethod("add"));
                genMehtods.add(new CodeGenMethod("save"));
            }
            if ((codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolEdit()) || (codeGen.getIsToolbar() && codeGen.getIsToolbarEditBtn())) {
                genMehtods.add(new CodeGenMethod("edit"));
                genMehtods.add(new CodeGenMethod("update"));
            }
            if (codeGen.getIsToolbar() && codeGen.getIsToolbarDelBtn()) {
                genMehtods.add(new CodeGenMethod("deleteByIds"));
            }
            if (codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolDel()) {
                genMehtods.add(new CodeGenMethod("delete"));
            }
            if (codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolSort()) {
                genMehtods.add(new CodeGenMethod("up"));
                genMehtods.add(new CodeGenMethod("down"));
                genMehtods.add(new CodeGenMethod("initSortRank"));
            }
            if (codeGen.getIsTreeTable()) {
                genMehtods.add(new CodeGenMethod("tree"));
            }

            if(codeGen.getIsImportExcel()){
                genMehtods.add(new CodeGenMethod("initImportExcel"));
                genMehtods.add(new CodeGenMethod("downloadTpl"));
                genMehtods.add(new CodeGenMethod("importExcel"));
            }
        }
        if(codeGen.getIsExportExcel()){
            if(codeGen.getIsExportExcelByForm()){
                genMehtods.add(new CodeGenMethod("exportExcelByForm"));
            }
            if(codeGen.getIsExportExcelByCheckedIds()){
                genMehtods.add(new CodeGenMethod("exportExcelByCheckedIds"));
            }
            if(codeGen.getIsExportExcelAll()){
                genMehtods.add(new CodeGenMethod("exportExcelAll"));
            }
        }
        return genMehtods;
    }

    private List<CodeGenMethod> getCommonServiceMethods(CodeGen codeGen) {
        List<CodeGenMethod> genMehtods = new ArrayList<CodeGenMethod>();
        genMehtods.add(new CodeGenMethod("getAdminDatas"));
        if (codeGen.getIsCrud()) {
            if (codeGen.getIsPageTitleAddBtn() || (codeGen.getIsToolbar() && codeGen.getIsToolbarAddBtn())) {
                genMehtods.add(new CodeGenMethod("save"));
            }
            if ((codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolEdit()) || (codeGen.getIsToolbar() && codeGen.getIsToolbarEditBtn())) {
                genMehtods.add(new CodeGenMethod("update"));
            }
            if(codeGen.getIsCheckCanDelete()){
                genMehtods.add(new CodeGenMethod("checkCanDelete"));
            }
            if ((codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolDel()) || (codeGen.getIsToolbar() && codeGen.getIsToolbarDelBtn())) {
                genMehtods.add(new CodeGenMethod("afterDelete"));
                genMehtods.add(new CodeGenMethod("checkInUse"));
            }
            if (codeGen.getIsShowOptcol() && codeGen.getIsShowOptcolSort()) {
                genMehtods.add(new CodeGenMethod("up"));
                genMehtods.add(new CodeGenMethod("down"));
                genMehtods.add(new CodeGenMethod("initSortRank"));
            }
            if (codeGen.getIsTreeTable()) {
                genMehtods.add(new CodeGenMethod("tree"));
            }

            if(codeGen.getIsImportExcel()){
                List<CodeGenModelAttr> headers = codeGenModelAttrService.getCodeGenImportColumns(codeGen.getId());
                if(notOk(headers)){
                    throw new RuntimeException("表["+codeGen.getMainTableName()+"]未设置导入列");
                }
                genMehtods.add(new CodeGenMethod("getImportExcelTpl",headers));
                genMehtods.add(new CodeGenMethod("importExcel",headers));
            }

        }
        if(codeGen.getIsExportExcel()){
            List<CodeGenModelAttr> headers = codeGenModelAttrService.getCodeGenExportColumns(codeGen.getId());
            if(notOk(headers)){
                throw new RuntimeException("表["+codeGen.getMainTableName()+"]未设置导出列");
            }
            genMehtods.add(new CodeGenMethod("exportExcel",headers));
        }
        return genMehtods;
    }

    /**
     * 得到package物理路径
     *
     * @param projectPath
     * @param targetPackageName
     * @return
     */
    private String getDirFromPackage(String projectPath, String targetPackageName) {
        return projectPath + "/src/main/java/" + targetPackageName.replace(".", "/");
    }

    /**
     * 得到view物理路径
     *
     * @param projectPath
     * @param viewFolder
     * @return
     */
    private String getDirFromViewForlder(String projectPath, String viewFolder) {
        if (viewFolder.charAt(0) != JBoltConst.SLASH) {
            viewFolder = JBoltConst.SLASH_STR + viewFolder;
        }
        return projectPath + "/src/main/webapp" + viewFolder;
    }

    /**
     * model生成
     *
     * @param codeGenId
     * @param cover
     * @return
     */
    public Ret genModel(Long codeGenId, boolean cover) {
        if (notOk(codeGenId)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        CodeGen codeGen = findById(codeGenId);
        if (codeGen == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        return genModel(codeGen, cover);
    }

    /**
     * 生成model
     *
     * @param codeGen
     * @param cover
     * @return
     */
    private Ret genModel(CodeGen codeGen, boolean cover) {
        //要生成的代码是在哪个项目下面 设置项目跟路径
        String projectRootPath = codeGen.getProjectPath();
        //数据源配置名称 默认主数据源是main 其他的在extend_datasource.setting里配置的
        String configName = codeGen.getDatasourceName();
        //指定本次运行直接生成的表名 忽略其它所有表 数组为空 表示忽略此强制设定 当需要单个指定生成时才需要这个
        String[] tableNames = new String[]{codeGen.getMainTableName()};
        //哪些前缀名的要生成
        String[] tableNamesPrefixes = new String[]{/* "jb_","pl_" */};
        //是否包含数据库视图生成 默认不生
        boolean generateView = false;
        //生成的Model java类需要去掉的前缀 多个用逗号隔开 内置已经去掉了核心表的前缀jb_
        String removedTableNamePrefixes = codeGen.getTableRemovePrefix();
        //默认ID生成模式
        String idGenMode = codeGen.getMainTableIdgenmode();
        //生成Model放在哪个包下
        String modelPackage = codeGen.getModelPackage();

        //是否生成Model和BaseModel 如果设置为false 则只判断是否生成字典文件 直接不进行model和baseModel的生成
        boolean genModel = true;
        //是否生成html格式数据字典
        boolean genHtmlDataDictionary = false;
        //数据库字典文件版本号 自己定义
        String dataDictionaryVersion = "1.0.0";
        //数据流字典文件的简介描述信息
        String dataDictionaryDescription = (configName.equals("main") ? "主数据源" : "扩展数据源[" + configName + "]") + "数据库字典";
        //是否开始启动缓存机制 开启后会在Model上生成@JBoltAutoCache注解
        boolean autoCacheEnable = codeGen.getIsAutoCache();
        //是否开启idCache策略
        boolean idCacheEnable = codeGen.getIsIdCache();
        //是否开启KeyCache策略
        boolean keyCacheEnable = codeGen.getIsKeyCache();
        //KeyCache 使用哪个字段属性
        String keyCacheColumn = codeGen.getKeyCacheColumn();
        //KeyCache 还需额外绑定哪个属性
        String keyCacheBindColumn = codeGen.getKeyCacheBindColumn();

        //下面这个默认是null就行 自定义的数据库字段转驼峰getter属性名的策略，
        //默认使用策略已经够用，如果你有特殊需求就在这里定义它
        JBoltColumnToBuildAttrNameFunction columnTobuildAttrNameFun = null;
//		JBoltColumnToBuildAttrNameFunction columnTobuildAttrNameFun = new JBoltColumnToBuildAttrNameFunction() {
//			@Override
//			public String build(String column) {
//				//这里column就是数据库里的一个字段 然后你通过处理返回一个应有的定制格式即可
//				return null;
//			}
//		};
        //获取翻译设置
        List<CodeGenModelAttr> translates = codeGenModelAttrService.getCodeGenNeedTranslateAttrs(codeGen.getId());
        //设置是否生成baseModel中的字段常量
        JBoltProjectGenConfig.genColConstant = codeGen.getIsBaseModelGenColConstant();
        //设置字段常量的
        JBoltProjectGenConfig.genColConstantToUpperCase = codeGen.getIsBaseModelGenColConstantToUppercase();
        //初始化项目配置
        JBoltProjectGenConfig.init(projectRootPath, modelPackage, genModel, idGenMode, genHtmlDataDictionary, false, removedTableNamePrefixes, columnTobuildAttrNameFun, tableNames, tableNamesPrefixes, generateView, translates,cover);
        //设置自动缓存机制
        JBoltProjectGenConfig.setModelAutoCache(autoCacheEnable, idCacheEnable, keyCacheEnable, keyCacheColumn, keyCacheBindColumn);
        //执行Model、BaseModel、数据字典Html的生成
        boolean success = new JFinalModelGenerator().generate(configName, dataDictionaryVersion, dataDictionaryDescription);
        if (success) {
            processModelPackage(codeGen);
            //更新生成状态
            if(codeGen.getState().intValue() == CodeGenState.ONLY_MAIN_LOGIC_GEN.getValue()){
                codeGen.setState(CodeGenState.GENED.getValue());
            }else{
                codeGen.setState(CodeGenState.ONLY_MODEL_GEN.getValue());
            }
            success = codeGen.update();
        }
        return success ? success("Model生成成功，请刷新项目目录") : fail("Model生成异常，请检查后重试");
    }

    private void processModelPackage(CodeGen codeGen) {
        String dbType = codeGen.getDatabaseType();
        String configFilePath = null;
        String configFilePathPro = null;
        if (codeGen.getIsMainDatasource()) {
            configFilePath = FileUtil.normalize(codeGen.getProjectPath() + SEPARATOR + "src/main/resources/dbconfig/" + dbType.toLowerCase() + "/config.properties");
            configFilePathPro = FileUtil.normalize(codeGen.getProjectPath() + SEPARATOR + "src/main/resources/dbconfig/" + dbType.toLowerCase() + "/config-pro.properties");
            processMainModelPackages(configFilePath, codeGen.getModelPackage());
            processMainModelPackages(configFilePathPro, codeGen.getModelPackage());
        } else {
            configFilePath = FileUtil.normalize(codeGen.getProjectPath() + SEPARATOR + "src/main/resources/dbconfig/extend_datasource.setting");
            configFilePathPro = FileUtil.normalize(codeGen.getProjectPath() + SEPARATOR + "src/main/resources/dbconfig/extend_datasource_pro.setting");
            processExtendModelPackages(configFilePath, codeGen.getDatasourceName(), codeGen.getModelPackage());
            processExtendModelPackages(configFilePathPro, codeGen.getDatasourceName(), codeGen.getModelPackage());
        }

    }

    /**
     * 配置扩展数据源里的model_package
     *
     * @param configFilePath
     * @param datasourceName
     * @param modelPackage
     */
    private void processExtendModelPackages(String configFilePath, String datasourceName, String modelPackage) {
        Setting setting = SettingUtil.get(configFilePath);
        String modelPackageStr = setting.getByGroup("model_package", datasourceName);
        if (isOk(modelPackageStr)) {
            Set<String> tempSet = new LinkedHashSet(Arrays.asList(modelPackageStr.split(",")));
            if (tempSet.contains(modelPackage)) {
                //如果已经存在就忽略
                return;
            }
            modelPackageStr = processModelPackageStr(modelPackageStr, modelPackage);

        } else {
            modelPackageStr = modelPackage;
        }


        List<String> codeLines = FileUtil.readLines(configFilePath, CharsetUtil.UTF_8);
        if (codeLines == null || codeLines.size() == 0) {
            throw new RuntimeException(configFilePath + "文件内容异常");
        }
        int index = -1;
        boolean hasModelPackage = false;
        boolean hasConfigGroup = false;
        Set<String> tempSet;
        for (String line : codeLines) {
            index++;
            if (line.trim().indexOf("[" + datasourceName + "]") != -1) {
                hasConfigGroup = true;
            }
            if (hasConfigGroup && line.trim().indexOf("model_package") != -1) {
                hasModelPackage = true;
                break;
            }
        }


        if (hasModelPackage) {
            codeLines.set(index, "model_package = " + modelPackageStr);
            //执行重新生成
            FileUtil.writeLines(codeLines, configFilePath, CharsetUtil.UTF_8);
            SettingUtil.get(configFilePath).load();
        }


    }

    /**
     * 处理主数据源配置文件中的ModelPackage
     *
     * @param configFilePath
     * @param modelPackage
     */
    private void processMainModelPackages(String configFilePath, String modelPackage) {
        if (!FileUtil.exist(configFilePath)) {
            throw new RuntimeException("配置文件不存在:" + configFilePath);
        }


        List<String> codeLines = FileUtil.readLines(configFilePath, CharsetUtil.UTF_8);
        if (codeLines == null || codeLines.size() == 0) {
            throw new RuntimeException(configFilePath + "文件内容异常");
        }
        int index = -1;
        String modelPackageStr = null;
        boolean hasModelPackage = false;
        Set<String> tempSet;
        for (String line : codeLines) {
            index++;
            if (line.trim().indexOf("model_package") != -1) {
                hasModelPackage = true;
                modelPackageStr = line.trim().replace("model_package", "").replace("=", "").trim();
                tempSet = new LinkedHashSet(Arrays.asList(modelPackageStr.split(",")));
                if (tempSet.contains(modelPackage)) {
                    //已经存在了就不处理了
                    return;
                }
                break;
            }
        }


        if (!hasModelPackage) {
            modelPackageStr = modelPackage;
            codeLines.add("model_package = " + modelPackageStr);
        } else {
            modelPackageStr = processModelPackageStr(modelPackageStr, modelPackage);
            codeLines.set(index, "model_package = " + modelPackageStr);
        }
        //执行重新生成
        FileUtil.writeLines(codeLines, configFilePath, CharsetUtil.UTF_8);

    }

    private String processModelPackageStr(String modelPackageStr, String modelPackage) {
        if (notOk(modelPackageStr)) {
            modelPackageStr = modelPackage;
        } else {
            String[] arrs = JBoltArrayUtil.from3(modelPackageStr);
            if (arrs == null || arrs.length == 0) {
                modelPackageStr = modelPackage;
            } else {
                Set<String> set = new LinkedHashSet(Arrays.asList(arrs));
                set.add(modelPackage);
                String[] newArr = (String[]) set.toArray(new String[0]);
                modelPackageStr = JBoltArrayUtil.join(newArr, ",");
            }
        }
        return modelPackageStr;
    }

    /**
     * 绑定permission
     * @param codeGenId
     * @param permissionId
     * @return
     */
    public Ret bindPermission(Long codeGenId, Long permissionId) {
        if(hasNotOk(codeGenId,permissionId)){
            return fail(JBoltMsg.PARAM_ERROR);
        }
        CodeGen codeGen = findById(codeGenId);
        if(codeGen == null){
            return fail("CodeGen "+JBoltMsg.DATA_NOT_EXIST);
        }
        Permission permission = JBoltPermissionCache.me.get(permissionId);
        if(permission == null){
            return fail("权限资源 "+JBoltMsg.DATA_NOT_EXIST);
        }
        codeGen.setPermissionId(permissionId);
        boolean success = codeGen.update();
        return ret(success);
    }

    /**
     * 更新projectPath
     * @param codeGenId
     * @param projectPath
     * @return
     */
    public Ret updateProjectPath(Long codeGenId, String projectPath) {
        if(hasNotOk(codeGenId,projectPath)){
            return fail(JBoltMsg.PARAM_ERROR);
        }
        CodeGen codeGen = findById(codeGenId);
        if(codeGen == null){
            return fail("CodeGen "+JBoltMsg.DATA_NOT_EXIST);
        }
        if(!FileUtil.isDirectory(FileUtil.normalize(projectPath))){
            return fail("路径："+projectPath+"不可用");
        }
        codeGen.setProjectPath(projectPath);
        boolean success = codeGen.update();
        return ret(success);
    }

    //重新设置mainTablePrimaryKey
    public void reprocessMainTablePrimaryKey(Long codeGenId) {
        CodeGen codeGen = findById(codeGenId);
        if(codeGen == null) {
            throw new RuntimeException("未找到代码生成的基础配置");
        }
        CodeGenModelAttr primarykeyAttr = codeGenModelAttrService.getPrimaryKeyAttr(codeGenId);
        if(primarykeyAttr == null){
            throw new RuntimeException("表未设置主键！");
        }
        String pkName = primarykeyAttr.getColName();
        if(!pkName.equalsIgnoreCase(codeGen.getMainTablePkey())){
            codeGen.setMainTablePkey(pkName);
            if(notOk(codeGen.getTableDefaultSortColumn()) || (codeGen.getTableDefaultSortColumn().equalsIgnoreCase(ID) && !pkName.equalsIgnoreCase(ID))){
                codeGen.setTableDefaultSortColumn(pkName);
            }
            codeGen.update();
        }else{
            if(notOk(codeGen.getTableDefaultSortColumn()) || (codeGen.getTableDefaultSortColumn().equalsIgnoreCase(ID) && !pkName.equalsIgnoreCase(ID))){
                codeGen.setTableDefaultSortColumn(pkName);
                codeGen.update();
            }
        }
    }

    public String getLastMainJavaPackage() {
        CodeGen codeGen = findFirst(selectSql().orderById(true).isDeletedEq(false).select("id",CodeGen.MAIN_JAVA_PACKAGE));
        return codeGen==null?null:codeGen.getMainJavaPackage();
    }

    public CodeGen getLastEnableCodeGen() {
        return findFirst(selectSql().orderById(true).isDeletedEq(false));
    }


    public Long getLastCodegenId(String datasource, String tableName) {
        return queryLong(selectSql().eq(CodeGen.DATASOURCE_NAME, datasource).eq(CodeGen.MAIN_TABLE_NAME, tableName).orderById(true).selectId());
    }
}
