package cn.jbolt.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.BucketInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

public class QiniuUtil {
	private static final Client CLIENT = new Client();
	/**
	 * 七牛生TOKEN
	 * @param accessKey          七牛AK
	 * @param secretKey          七牛SK
	 * @param bucket             空间名称
	 * @return
	 */
	public static String genToken(String accessKey,String secretKey,String bucket) {
		return genToken(accessKey, secretKey, bucket,null);
	}
	/**
	 * 七牛生TOKEN
	 * @param accessKey          七牛AK
	 * @param secretKey          七牛SK
	 * @param bucket             空间名称
	 * @param callbackUrl        上传后的回调服务器地址 可以null
	 * @return
	 */
	private static String genToken(String accessKey, String secretKey, String bucket, String callbackUrl) {
		return genToken(accessKey, secretKey, bucket, callbackUrl, null);
	}
	/**
	 * 七牛生TOKEN
	 * @param accessKey          七牛AK
	 * @param secretKey          七牛SK
	 * @param bucket             空间名称
	 * @param callbackUrl        上传后的回调服务器地址 可以null
	 * @param callbackBody       上传后的回调服务器 返回body定义 
	 * @return
	 */
	private static String genToken(String accessKey, String secretKey, String bucket, String callbackUrl, String callbackBody) {
		return genToken(accessKey, secretKey, bucket, callbackUrl, callbackBody, null);
	}
	/**
	 * 七牛生TOKEN
	 * @param accessKey          七牛AK
	 * @param secretKey          七牛SK
	 * @param bucket             空间名称
	 * @param callbackUrl        上传后的回调服务器地址 可以null
	 * @param callbackBody       上传后的回调服务器 返回body定义 
	 * @param callbackBodyType   上传后的回调服务器 body的类型 默认application/json
	 * @return
	 */
	private static String genToken(String accessKey, String secretKey, String bucket, String callbackUrl, String callbackBody, String callbackBodyType) {
		return genToken(accessKey, secretKey, bucket, callbackUrl, callbackBody, callbackBodyType, null);
	}
	/**
	 * 七牛生TOKEN
	 * @param accessKey          七牛AK
	 * @param secretKey          七牛SK
	 * @param bucket             空间名称
	 * @param callbackUrl        上传后的回调服务器地址 可以null
	 * @param callbackBody       上传后的回调服务器 返回body定义 
	 * @param callbackBodyType   上传后的回调服务器 body的类型 默认application/json
	 * @param expires            有效期 默认3600 传null用默认
	 * @return
	 */
	private static String genToken(String accessKey, String secretKey, String bucket, String callbackUrl, String callbackBody, String callbackBodyType, Long expires) {
		Auth auth = Auth.create(accessKey, secretKey);
		if(expires == null) {
			expires = 3600L;
		}
		if(StrKit.notBlank(callbackUrl)) {
			StringMap putPolicy = new StringMap();
			putPolicy.put("callbackUrl", callbackUrl);
			putPolicy.put("callbackBody", StrKit.defaultIfBlank(callbackBody, "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}"));
			putPolicy.put("callbackBodyType", StrKit.defaultIfBlank(callbackBodyType, "application/json"));
			return auth.uploadToken(bucket, null, expires, putPolicy,false);
		}
		return auth.uploadToken(bucket, null, expires,null);
	}
	/**
	 * 通过API获取指定空间bucket的domains
	 * @param accessKey
	 * @param secretKey
	 */
	public static String[] getDomains(String accessKey, String secretKey,String bucket) {
		Auth auth = Auth.create(accessKey, secretKey);
		BucketManager bucketManager = new BucketManager(auth,CLIENT);
		try {
			return bucketManager.domainList(bucket);
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 通过API获取七牛buckets
	 * @param accessKey
	 * @param secretKey
	 */
	public static String[] getBucketNames(String accessKey, String secretKey) {
		Auth auth = Auth.create(accessKey, secretKey);
		BucketManager bucketManager = new BucketManager(auth,CLIENT);
		try {
			return bucketManager.buckets();
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取指定七牛账号的buckets
	 * @param accessKey
	 * @param secretKey
	 * @return
	 */
	public static List<String> getBucketNameList(String accessKey, String secretKey) {
		String[] buckets = getBucketNames(accessKey, secretKey);
		if(buckets==null || buckets.length==0) {
			return null;
		}
		return Arrays.asList(buckets);
	}
	/**
	 * 获取指定bucket的domainList
	 * @param accessKey
	 * @param secretKey
	 * @param bucket
	 * @return
	 */
	public static List<String> getDomainList(String accessKey, String secretKey,String bucket) {
		String[] domains = getDomains(accessKey, secretKey, bucket);
		if(domains==null || domains.length==0) {
			return null;
		}
		return Arrays.asList(domains);
	}
	/**
	 * 获取单个Bucket信息
	 * @param accessKey
	 * @param secretKey
	 * @param bucket
	 * @return
	 */
	public static BucketInfo getBucketInfo(String accessKey,String secretKey,String bucket) {
		Auth auth = Auth.create(accessKey, secretKey);
		BucketManager bucketManager = new BucketManager(auth,CLIENT);
		try {
			return bucketManager.getBucketInfo(bucket);
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取一个七牛的所有BucketInfo信息
	 * @param accessKey
	 * @param secretKey
	 * @return
	 */
	public static List<BucketInfo> getBucketInfoList(String accessKey,String secretKey) {
		String[] buckets = getBucketNames(accessKey, secretKey);
		List<BucketInfo> bucketInfos = new ArrayList<BucketInfo>();
		BucketInfo bucketInfo;
		if(buckets!=null && buckets.length>0) {
			for(String bucket:buckets) {
				bucketInfo =   getBucketInfo(accessKey,secretKey,bucket);
				if(bucketInfo != null) {
					bucketInfos.add(bucketInfo);
				}
			}
		}
		bucketInfos.forEach(i->{System.out.println(JSON.toJSONString(i));});
		return bucketInfos;
	}
	/**
	 * 获取指定bucket的region
	 * @param accessKey
	 * @param secretKey
	 * @param bucket
	 * @return
	 */
	public static String getBucketRegion(String accessKey, String secretKey,String bucket) {
		BucketInfo bucketInfo=getBucketInfo(accessKey, secretKey, bucket);
		return bucketInfo==null?null:bucketInfo.getRegion();
	}
	
	public static void main(String[] args) {
		String accessKey = "JNclLYjtZ0HXtIla0A9wX5WJbhjfwt6QAU0xcWBV";
		String secretKey = "Dh_7KnavW8oCulCp5F8DlY1spiCcKIW4FQFlauzW";
		getBucketInfoList(accessKey, secretKey);
//		List<String> bucketList = getBucketNameList(accessKey,secretKey);
//		if(bucketList!=null) {
//			bucketList.forEach(bucket->{
//				System.out.println("Bucket:"+bucket);
//				System.out.println("====================");
//				List<String> domains = getDomainList(accessKey, secretKey, bucket);
//				if(domains!=null) {
//					domains.forEach(System.out::println);
//				}
//				System.out.println("");
//			});
//		}
	}
	/**
	 * 获取指定bucket的绑定域名
	 * @param accessKey
	 * @param secretKey
	 * @param bucket
	 * @return
	 */
	public static String getDomainStr(String accessKey, String secretKey, String bucket) {
		String[] domains = getDomains(accessKey, secretKey, bucket);
		if(domains == null || domains.length==0) {return null;}
		return ArrayUtil.join(domains, ",");
	}
//	//区域map
//	public static final Map<String, String> qiniuRegionMap = new HashMap<String, String>();
//	static {
//		qiniuRegionMap.put("z0", "华东");
//		qiniuRegionMap.put("z1", "华北");
//		qiniuRegionMap.put("z2", "华南");
//		qiniuRegionMap.put("na0", "北美");
//		qiniuRegionMap.put("as0", "东南亚");
//	}
//	/**
//	 * 拿到区域options
//	 * @return
//	 */
//	public static List<Option> getQiniuRegions(){
//		return qiniuRegionMap.entrySet().stream().map(e-> new OptionBean(e.getValue(), e.getKey())).collect(Collectors.toList());
//	}
//	
//	/**
//	 * 通过区域CODE拿到区域名
//	 * @param regionCode
//	 * @return
//	 */
//	public static String getRegionName(String regionCode){
//		return qiniuRegionMap.get(regionCode);
//	}
	
}
