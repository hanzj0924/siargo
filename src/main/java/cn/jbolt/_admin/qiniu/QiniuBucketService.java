package cn.jbolt._admin.qiniu;

import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.jbolt._admin.cache.JBoltQiniuCache;
import cn.jbolt.core.db.sql.Sql;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.qiniu.storage.model.BucketInfo;

import cn.jbolt.common.model.Qiniu;
import cn.jbolt.common.model.QiniuBucket;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.common.enums.JBoltSystemLogTargetType;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltQiniuUtil;
import cn.jbolt.core.util.JBoltRandomUtil;
/**
 * 七牛Bucket管理 Service
 * @ClassName: QiniuBucketService   
 * @author: JBolt-Generator
 * @date: 2021-10-14 11:27  
 */
public class QiniuBucketService extends JBoltBaseService<QiniuBucket> {
	private final QiniuBucket dao=new QiniuBucket().dao();
	@Inject
	private QiniuService qiniuService;
	@Override
	protected QiniuBucket dao() {
		return dao;
	}
		
  /**
	 * 后台管理分页查询true
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param qiniuId
	 * @param zone
	 * @return
	 */
	public Page<QiniuBucket> paginateAdminDatas(int pageNumber, int pageSize, String keywords,Long qiniuId,String zone) {
		Sql sql = selectSql().page(pageNumber,pageSize);
		sql.likeMulti(keywords,"name","sn","remark");
		sql.eq("qiniu_id",qiniuId);
		sql.eq("region",zone);
		sql.orderById(true);
		return paginate(sql);
	}
	
  /**
	 * 保存
	 * @param qiniuBucket
	 * @return
	 */
	public Ret save(QiniuBucket qiniuBucket) {
		if(qiniuBucket==null || isOk(qiniuBucket.getId()) || notOk(qiniuBucket.getName()) || notOk(qiniuBucket.getSn()) || notOk(qiniuBucket.getQiniuId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		if(exists("name", qiniuBucket.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		if(exists("sn", qiniuBucket.getSn())) {return fail(JBoltMsg.DATA_SAME_SN_EXIST);}
		if(qiniuBucket.getIsDefault() == null) {
			qiniuBucket.setIsDefault(false);
		}
		qiniuBucket.setEnable(false);
		qiniuBucket.setCreateUserId(JBoltUserKit.getUserId());
		qiniuBucket.setUpdateUserId(JBoltUserKit.getUserId());
		boolean success=qiniuBucket.save();
		if(success) {
			//如果设置为default 就把其它的已经default的设置为false
			if(qiniuBucket.getIsDefault()) {
				changeIsDefaultFalse(qiniuBucket.getQiniuId(),qiniuBucket.getId());
				//删除default缓存
				JBoltQiniuCache.me.removeDefaultBucket(qiniuBucket.getQiniuId());
			}
			//添加日志
			addSaveSystemLog(qiniuBucket.getId(), JBoltUserKit.getUserId(),qiniuBucket.getName());
		}
		return ret(success);
	}
	
	/**
	 * 将现在ID不为指定ID的数据的is_default数据转为false
	 * @param qiniuId
	 * @param excludedId
	 */
	private void changeIsDefaultFalse(Long qiniuId,Long excludedId) {
		if(isOk(qiniuId) && isOk(excludedId)) {
			changeColumnFalse(IS_DEFAULT,excludedId, updateSql().eq("qiniu_id", qiniuId));
		}
	}
	
   /**
	 * 更新
	 * @param qiniuBucket
	 * @return
	 */
	public Ret update(QiniuBucket qiniuBucket) {
		if(qiniuBucket==null || notOk(qiniuBucket.getId()) || notOk(qiniuBucket.getName()) || notOk(qiniuBucket.getSn()) || notOk(qiniuBucket.getQiniuId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		QiniuBucket dbQiniuBucket=findById(qiniuBucket.getId());
		if(dbQiniuBucket==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		if(exists("name", qiniuBucket.getName(), qiniuBucket.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		if(exists("sn", qiniuBucket.getSn(), qiniuBucket.getId())) {return fail(JBoltMsg.DATA_SAME_SN_EXIST);}
		qiniuBucket.setUpdateUserId(JBoltUserKit.getUserId());
		qiniuBucket.remove("enable","create_user_id");
		if(qiniuBucket.getIsDefault() == null) {
			qiniuBucket.setIsDefault(false);
		}
		boolean success=qiniuBucket.update();
		if(success) {
			//如果设置为default 就把其它的已经default的设置为false
			//如果前端传了true 并且数据库里的不是true
			if((qiniuBucket.getIsDefault() != null && qiniuBucket.getIsDefault()) && !dbQiniuBucket.getIsDefault()) {
				changeIsDefaultFalse(qiniuBucket.getQiniuId(),qiniuBucket.getId());
				//删除default缓存
				JBoltQiniuCache.me.removeDefaultBucket(qiniuBucket.getQiniuId());
			}
			//添加日志
			addUpdateSystemLog(qiniuBucket.getId(), JBoltUserKit.getUserId(),qiniuBucket.getName());
		}
		return ret(success);
	}
	
   /**
	  * 删除
	 * @param id
	 * @return
	 */
	public Ret delete(Long id) {
		return deleteById(id,true);
	}
	
	/**
	  * 删除数据后执行的回调
	 * @param qiniuBucket 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(QiniuBucket qiniuBucket, Kv kv) {
		addDeleteSystemLog(qiniuBucket.getId(), JBoltUserKit.getUserId(),qiniuBucket.getName());
		qiniuService.updateBucketCount(qiniuBucket.getQiniuId());
		if(qiniuBucket.getIsDefault()){
			JBoltQiniuCache.me.removeDefaultBucket(qiniuBucket.getQiniuId());
		}
		return null;
	}
	
	/**
	 * 额外需要处理toggle操作
	 * @param qiniuBucket
	 * @param column
	 * @param kv
	 * @return
	 */
	@Override
	protected String afterToggleBoolean(QiniuBucket qiniuBucket, String column, Kv kv) {
		switch (column) {
		case "enable":
			addUpdateSystemLog(qiniuBucket.getId(), JBoltUserKit.getUserId(),qiniuBucket.getName(),"的启用状态:"+qiniuBucket.getEnable());
			break;
		case "is_default":
			if(qiniuBucket.getIsDefault()) {
				changeIsDefaultFalse(qiniuBucket.getQiniuId(), qiniuBucket.getId());
			}
			addUpdateSystemLog(qiniuBucket.getId(), JBoltUserKit.getUserId(),qiniuBucket.getName(),"的是否默认属性:"+qiniuBucket.getIsDefault());
			break;

		default:
			break;
		}
		//删除default缓存
		JBoltQiniuCache.me.removeDefaultBucket(qiniuBucket.getQiniuId());
	return null;
	}
	
	
	
	/**
	 * 从线上更新同步bucket信息
	 * @param qiniuId
	 * @return
	 */
	public Ret syncFromQiniu(Long qiniuId) {
		Qiniu qiniu = qiniuService.findById(qiniuId);
		if(qiniu == null) {
			return fail("指定ID的七牛账号"+JBoltMsg.DATA_NOT_EXIST);
		}
		List<BucketInfo> bucketInfos = JBoltQiniuUtil.getBucketInfoList(qiniu.getAk(),qiniu.getSk());
		if(notOk(bucketInfos)) {
			return fail("七牛账号["+qiniu.getName()+"("+qiniu.getSn()+")"+"]中尚未添加bucket");
		}
		List<String> onlineBuckets = JBoltQiniuUtil.getBucketNameList(qiniu.getAk(),qiniu.getSk());
		if(isOk(onlineBuckets)) {
			syncDbAndQiniuBuckets(qiniu,onlineBuckets);
		}
		//删除default缓存
		JBoltQiniuCache.me.removeDefaultBucket(qiniuId);
		return SUCCESS;
	}
	
	/**
	 * 同步所有账号的bucket
	 * @return
	 */
	public Ret syncAllQiniuBuckets() {
		List<Qiniu> qinius = qiniuService.findAll();
		if(notOk(qinius)) {
			return fail("请先添加七牛账号");
		}
		List<String> onlineBuckets;
		for(Qiniu qiniu:qinius) {
			onlineBuckets = JBoltQiniuUtil.getBucketNameList(qiniu.getAk(),qiniu.getSk());
			if(isOk(onlineBuckets)) {
				syncDbAndQiniuBuckets(qiniu,onlineBuckets);
			}
			//删除default缓存
			JBoltQiniuCache.me.removeDefaultBucket(qiniu.getId());
		}

		return SUCCESS;
	}
	
	public List<String> getQiniuBucketNames(Long qiniuId){
		if(notOk(qiniuId)) {return null;}
		return query(selectSql().select("name").eq("qiniu_id", qiniuId));
	}
	
	private void syncDbAndQiniuBuckets(Qiniu qiniu, List<String> onlineBuckets) {
		if(qiniu == null || notOk(onlineBuckets)) {
			return;
		}
		Long qiniuId = qiniu.getId();
		List<String> dbNames = getQiniuBucketNames(qiniuId);
		if(notOk(dbNames)) {
			List<QiniuBucket> saveQiniuBuckets = new ArrayList<QiniuBucket>();
			onlineBuckets.forEach(bucket->saveQiniuBuckets.add(covertToQiniuBucket(qiniu,bucket)));
			if(saveQiniuBuckets.size()>0) {
				batchSave(saveQiniuBuckets);
				qiniuService.updateBucketCount(qiniuId);
			}
			return;
		}
		//线上需要保存到数据库的list
		List<String> saveList = onlineBuckets.stream().filter(bucket->(!dbNames.contains(bucket))).collect(Collectors.toList());
		if(isOk(saveList)) {
			List<QiniuBucket> saveQiniuBuckets = new ArrayList<QiniuBucket>();
			saveList.forEach(bucket->saveQiniuBuckets.add(covertToQiniuBucket(qiniu,bucket)));
			if(saveQiniuBuckets.size()>0) {
				batchSave(saveQiniuBuckets);
				qiniuService.updateBucketCount(qiniuId);
			}
		}
	}

 
 
	/**
	 * 将线上的转为数据库的
	 * @param qiniu
	 * @param bucket
	 * @return
	 */
	private QiniuBucket covertToQiniuBucket(Qiniu qiniu, String bucket) {
		QiniuBucket qiniuBucket = new QiniuBucket();
		qiniuBucket.setQiniuId(qiniu.getId());
		qiniuBucket.setName(bucket);
		qiniuBucket.setSn(JBoltRandomUtil.randomNumber(6));
		qiniuBucket.setEnable(true);
		qiniuBucket.setIsDefault(false);
		qiniuBucket.setCreateUserId(JBoltUserKit.getUserId());
		qiniuBucket.setUpdateUserId(JBoltUserKit.getUserId());
		String region = JBoltQiniuUtil.getBucketRegion(qiniu.getAk(),qiniu.getSk(),bucket);
		if(isOk(region)) {
			qiniuBucket.setRegion(region);
		}
		String domainUrl = JBoltQiniuUtil.getDomainStr(qiniu.getAk(),qiniu.getSk(),bucket);
		if(isOk(domainUrl)) {
			qiniuBucket.setDomainUrl(domainUrl);
		}
		return qiniuBucket;
	}

	/**
	 * 获得一个账号下的所有buckets
	 * @param qiniuId
	 * @return
	 */
	public List<QiniuBucket> getQiniuBuckets(Long qiniuId) {
		if(notOk(qiniuId)) {
			return null;
		}
		return find(selectSql().eq("qiniu_id", qiniuId));
	}
	/**
	 * 检测是否存在指定账号下的bucket
	 * @param qiniuId
	 * @return
	 */
	public boolean checkQiniuHasBucket(Long qiniuId) {
		return existsSon("qiniu_id", qiniuId);
	}
	/**
	 * 
	 * @param qiniuId
	 * @return
	 */
	public QiniuBucket getQiniuBucketDefault(Object qiniuId) {
		return findFirst(selectSql().eq("qiniu_id", qiniuId).eq("is_default", TRUE));
	}

	@Override
	protected int systemLogTargetType() {
		return JBoltSystemLogTargetType.QINIU_BUCKET.getValue();
	}
	
}