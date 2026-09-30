package cn.jbolt.admin.siargo.prodmodel;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.ProdModelDimension;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.imageio.ImageIO;

/** 一产品系列多张机械尺寸图；文件发布失败补偿，删除在事务提交后执行。 */
public class ProdModelDimensionService extends JBoltBaseService<ProdModelDimension> {
    private static final Log LOG=Log.getLog(ProdModelDimensionService.class);
    private final ProdModelDimension dao=new ProdModelDimension().dao();
    @Override protected ProdModelDimension dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.PROD_MODEL_DIMENSION.getValue(); }
    public SiargoStorage storage() { return SiargoStorage.forBusiness(SiargoStorage.Business.PROD_MODEL); }
    public List<Record> listForModel(Long modelId) {
        return Db.find("SELECT CAST(id AS CHAR) id,CAST(model_id AS CHAR) modelId,title,image_path imagePath,sort_rank sortRank,remark FROM siargo_prod_model_dimension WHERE model_id=? ORDER BY sort_rank,id",modelId);
    }
    public boolean belongsTo(Long id,Long modelId) {
        return id==null || (modelId!=null && Db.queryLong("SELECT COUNT(*) FROM siargo_prod_model_dimension WHERE id=? AND model_id=?",id,modelId)>0);
    }
    public boolean hasForModel(Long id) { return Db.queryLong("SELECT COUNT(*) FROM siargo_prod_model_dimension WHERE model_id=?",id)>0; }
    /** 编辑数据聚合与资料弹窗共用；id 字符串化防雪花精度丢失。 */
    public JSONArray listJsonForModel(Long modelId) {
        JSONArray arr=new JSONArray();
        for(Record r:listForModel(modelId)) {
            JSONObject o=new JSONObject();
            o.put("id",r.getStr("id"));
            o.put("title",r.getStr("title"));
            o.put("imagePath",r.getStr("imagePath"));
            o.put("sortRank",r.getInt("sortRank"));
            o.put("remark",r.getStr("remark")==null?"":r.getStr("remark"));
            arr.add(o);
        }
        return arr;
    }
    /** 接收 JSON 体（modelId/id/title/imagePath/sortRank/remark），字段缺省时由 persist 校验拒绝。 */
    public Ret persistFromJson(JSONObject json,boolean update) {
        if(json==null) return fail("参数错误");
        ProdModelDimension input=new ProdModelDimension().set("model_id",json.getLong("modelId"))
                .set("title",json.getString("title")).set("image_path",json.getString("imagePath"))
                .set("sort_rank",json.getInteger("sortRank")).set("remark",json.getString("remark"));
        if(update) input.set("id",json.getLong("id"));
        return persist(input,update);
    }
    public int nextRank(Long modelId) { Integer rank=Db.queryInt("SELECT MAX(sort_rank) FROM siargo_prod_model_dimension WHERE model_id=?",modelId);return rank==null?1:rank+1; }
    public boolean validImage(Path path) {
        try {
            String name=path.getFileName().toString().toLowerCase(Locale.ROOT);
            if(!name.matches(".*\\.(png|jpe?g|gif|bmp)$") || Files.size(path)>20L*1024*1024) return false;
            try(var input=ImageIO.createImageInputStream(path.toFile())) {
                if(input==null) return false;
                var readers=ImageIO.getImageReaders(input);
                if(!readers.hasNext()) return false;
                var reader=readers.next();
                try {reader.setInput(input);return reader.getWidth(0)>0 && reader.getHeight(0)>0 && (long)reader.getWidth(0)*reader.getHeight(0)<=40_000_000L;}
                finally {reader.dispose();}
            }
        } catch(Exception e) {return false;}
    }
    public Ret persist(ProdModelDimension input,boolean update) {
        if(input==null || notOk(input.getLong("model_id")) || (update && notOk(input.getLong("id"))) || (!update && input.getLong("id")!=null)) return fail("参数错误");
        String title=clean(input.getStr("title")),remark=clean(input.getStr("remark")),url=clean(input.getStr("image_path"));
        if(title.isEmpty() || title.length()>200 || remark.length()>500) return fail("图名称不能为空且不超过200字，备注不超过500字");
        Integer rank=input.getInt("sort_rank");
        if(rank==null || rank<0) return fail("排序须为非负整数");
        Long modelId=input.getLong("model_id"),id=update?input.getLong("id"):JBoltSnowflakeKit.me.nextId();
        ProdModelDimension previous=update?findById(id):null;
        if(update && (previous==null || !modelId.equals(previous.getLong("model_id")))) return fail("机械尺寸图不存在或不属于当前系列");
        String oldUrl=previous==null?null:previous.getStr("image_path");
        boolean changed=!url.equals(oldUrl);
        SiargoStorage storage=storage();
        SiargoUploadFiles.Moves moves=new SiargoUploadFiles.Moves(storage);
        try {
            Path source=changed?SiargoUploadFiles.temp(storage,url,true):storage.resolveUrl(url);
            if(!validImage(source)) return fail("请选择有效的 PNG/JPG/GIF/BMP 图片（不超过20MB、4000万像素）");
            Path target=changed?storage.path(modelId.toString(),UUID.randomUUID()+"_"+SiargoStorage.safeSegment(source.getFileName().toString())):source;
            if(changed) moves.move(source,target);
            String publishedUrl=storage.toUrl(target);
            Ret[] result={fail("保存失败")};
            boolean ok=Db.tx(()->{
                if(Db.queryLong("SELECT id FROM siargo_prod_model WHERE id=? FOR UPDATE",modelId)==null) {result[0]=fail("产品系列不存在");return false;}
                ProdModelDimension row=update?findById(id):new ProdModelDimension().set("id",id).set("model_id",modelId);
                if(row==null || (update && !java.util.Objects.equals(oldUrl,row.getStr("image_path")))) {result[0]=fail("尺寸图已被修改，请刷新后重试");return false;}
                row.set("title",title).set("image_path",publishedUrl).set("sort_rank",rank).set("remark",remark);
                if(!(update?row.update():row.save())) return false;
                if(update) addUpdateSystemLog(id,JBoltUserKit.getUserId(),title);else addSaveSystemLog(id,JBoltUserKit.getUserId(),title);
                result[0]=Ret.ok().set("id",id.toString());return true;
            });
            if(!ok) return moves.rollback(result[0].getStr("msg"));
            if(changed && oldUrl!=null) deletePhysical(oldUrl);
            return result[0];
        } catch(Exception e) {LOG.error("机械尺寸图保存失败",e);return moves.rollback("机械尺寸图保存失败，请检查图片路径并重试");}
    }
    public Ret remove(Long id) {
        ProdModelDimension previous=notOk(id)?null:findById(id);
        if(previous==null) return fail("机械尺寸图不存在");
        Ret[] result={fail("删除失败")};String[] path={null};
        boolean ok=Db.tx(()->{
            Db.queryLong("SELECT id FROM siargo_prod_model WHERE id=? FOR UPDATE",previous.getLong("model_id"));
            ProdModelDimension row=findById(id);
            if(row==null) {result[0]=fail("机械尺寸图不存在");return false;}
            String error=checkCanDelete(row,null);
            if(error!=null) {result[0]=fail(error);return false;}
            path[0]=row.getStr("image_path");
            if(!row.delete()) return false;
            addDeleteSystemLog(id,JBoltUserKit.getUserId(),row.getStr("title"));result[0]=Ret.ok();return true;
        });
        if(!ok) return result[0].isFail()?result[0]:fail("删除失败");
        deletePhysical(path[0]);return result[0];
    }
    /** 级联删除：参与调用方事务，返回待清理图片URL；事务内不删磁盘文件。 */
    public List<String> deleteForModelCascade(Long modelId) {
        List<String> urls=Db.query("SELECT image_path FROM siargo_prod_model_dimension WHERE model_id=?",modelId);
        Db.delete("DELETE FROM siargo_prod_model_dimension WHERE model_id=?",modelId);
        return urls;
    }
    /** 事务提交后清理物理图片，沿用引用计数保护。 */
    public void deleteFilesAfterCommit(List<String> urls) {
        if(urls==null) return;
        for(String url:urls) deletePhysical(url);
    }
    private void deletePhysical(String url) {
        try {
            if(Db.queryLong("SELECT COUNT(*) FROM siargo_prod_model_dimension WHERE image_path=?",url)==0) storage().deleteFile(storage().resolveUrl(url));
        } catch(Exception e) {LOG.error("机械尺寸图记录已提交，物理文件清理失败："+url,e);}
    }
    private static String clean(String value) {return value==null?"":value.trim();}
}
