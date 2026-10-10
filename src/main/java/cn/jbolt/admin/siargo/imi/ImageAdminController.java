package cn.jbolt.admin.siargo.imi;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Image;

/** 来料到货单管理 Controller */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/imi", viewPath = "/_view/admin/siargo/imi")
public class ImageAdminController extends JBoltBaseController {

    /** 图片管理 Service */
    @Inject
    private ImageService service;

    /** 上传至来料图片存储的独立临时目录，返回路径及文件名；正式入库由保存入口处理。 */
    public void uploadImages() {
        SiargoStorage storage = SiargoStorage.forBusiness(SiargoStorage.Business.IMI);
        try {
            List<UploadFile> files = getFiles(SiargoUploadFiles.newUploadDirectory(storage));
            if (files == null || files.isEmpty()) {
                renderJsonFail("请选择图片后上传");
                return;
            }
            for (UploadFile file : files) {
                if (notImage(file)) {
                    renderJsonFail("仅支持图片文件");
                    return;
                }
            }
            List<String> urls = new ArrayList<>(), names = new ArrayList<>();
            for (UploadFile file : files) {
                urls.add(SiargoUploadFiles.accept(storage, file));
                names.add(service.getFileName(storage.resolveUrl(urls.get(urls.size() - 1)).toFile()));
            }
            if (Boolean.TRUE.equals(getParaToBoolean("save"))) {
                renderJsonData(urls, "");
            }
            else {
                Map<String, Object> result = new HashMap<>();
                result.put("filesName", names);
                result.put("files", urls);
                result.put("message", "");
                renderJsonData(result);
            }
        } catch (Exception e) {
            renderJsonFail("上传失败：" + e.getMessage());
        }
    }

    /**
     * 首页
     * URL路径: GET /admin/siargo/imi
     */
    public void index() {
        render("index.html");
    }

    /**
     * 数据源分页查询
     * URL路径: GET /admin/siargo/imi/datas
     */
    public void datas() {
        renderJsonData(service.paginateAdminDatas(
                getPageNumber(), getPageSize(), getKeywords(),
                getPara("supplierId"), getPara("yearMonth")));
    }

    /**
     * 新增页面
     * URL路径: GET /admin/siargo/imi/add
     */
    public void add() {
        render("add.html");
    }

    /**
     * 编辑页面
     * URL路径: GET /admin/siargo/imi/edit/{id}
     *
     * @param id 图片ID（路径参数）
     */
    public void edit() {
        Image image = service.findById(getLong(0));
        if (image == null) {
            renderFail(JBoltMsg.DATA_NOT_EXIST);
            return;
        }
        set("image", image);
        render("edit.html");
    }

    /**
     * 批量保存图片记录，将临时目录的文件移动到正式目录并写入数据库。
     * URL路径: POST /admin/siargo/imi/save
     *
     * @param image        图片元数据（包含 supplierId、description）
     * @param imgUploadUrl 逗号分隔的临时文件路径列表
     * @return JSON 操作结果
     */
    public void save() {
        Image image = getModel(Image.class, "image");
        String uploadPathJson = getPara("imgUploadUrl");
        List<String> uploadPaths = Arrays.stream(uploadPathJson.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        renderJson(service.saveBatch(image, uploadPaths));
    }

    /**
     * 更新图片记录，支持修改供应商、文件名、备注或更换图片文件。
     * URL路径: POST /admin/siargo/imi/update
     *
     * @param image 图片更新数据
     * @return JSON 操作结果
     */
    public void update() {
        renderJson(service.update(getModel(Image.class, "image")));
    }

    /**
     * 批量删除（逗号分隔的 ID）。
     * <p>
     * 检查每条删除结果，任一失败则整体返回失败。
     * URL路径: POST /admin/siargo/imi/deleteByIds
     *
     * @param ids 逗号分隔的图片ID列表
     * @return JSON 操作结果
     */
    public void deleteByIds() {
        String idsJson = getPara("ids");
        if (StrKit.isBlank(idsJson)) {
            renderFail("参数错误");
            return;
        }
        List<Long> ids = Arrays.stream(idsJson.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            renderFail("参数错误");
            return;
        }
        // 1. 事务外收集待删物理文件路径（文件删除不可回滚）
        List<String> filePaths = service.queryFilePathsByIds(ids);
        // 2. 事务内仅软删 DB 记录
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            for (Long id : ids) {
                retHolder[0] = service.delete(id);
                if (retHolder[0].isFail()) {
                    return false;
                }
            }
            return true;
        });
        if (!txOk) {
            renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "删除失败");
            return;
        }
        // 3. afterCommit: 删除物理文件
        service.deletePhysicalFiles(filePaths);
        renderJsonSuccess();
    }

    /**
     * 批量删除（框架 deleteByIds 方式）
     * URL路径: POST /admin/siargo/imi/deleteByIds1
     *
     * @param ids 逗号分隔的图片ID列表
     * @return JSON 操作结果
     */
    @Before(Tx.class)
    public void deleteByIds1() {
        renderJson(service.deleteByBatchIds(get("ids")));
    }

    /** 校验文件属于来料图片临时目录后删除；路径由上传结果提供。 */
    public void deleteTempFile() {
        String url = getPara("filePath");
        if (url == null || url.isBlank()) {
            renderJsonFail("文件路径不能为空");
            return;
        }
        renderJson(
                SiargoUploadFiles.delete(SiargoStorage.forBusiness(SiargoStorage.Business.IMI), java.util.List.of(url),
                        true));
    }

    /**
     * 批量删除临时文件。
     * <p>
     * 接收逗号分隔的多个文件路径，同时删除多个 temp 目录下的临时文件。
     * URL路径: POST /admin/siargo/imi/deleteTempFiles
     *
     * @param filePaths 逗号分隔的临时文件相对路径列表
     * @return JSON 操作结果，包含成功/失败计数及失败文件列表
     */
    public void deleteTempFiles() {
        String urls = getPara("filePaths");
        renderJson(SiargoUploadFiles.delete(SiargoStorage.forBusiness(SiargoStorage.Business.IMI),
                urls == null ? java.util.List.of() : java.util.Arrays.stream(urls.split(",")).map(String::trim)
                        .filter(v -> !v.isEmpty()).toList(), true));
    }
}
