package cn.jbolt._admin.msgcenter;

import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;

import cn.jbolt.common.model.SysNoticeReader;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;

public class SysNoticeReaderService extends JBoltBaseService<SysNoticeReader> {
	private SysNoticeReader dao =new SysNoticeReader();
	
	@Override
	protected SysNoticeReader dao() {
		return dao;
	}
	/**
	 * 添加一个通知读者
	 * @param sysNoticeId
	 * @return
	 */
	public Ret addReader(Long sysNoticeId) {
		boolean exist = existsReader(sysNoticeId, JBoltUserKit.getUserId());
		if(exist) {
			return SUCCESS;
		}
		SysNoticeReader noticeReader=new SysNoticeReader();
		noticeReader.setSysNoticeId(sysNoticeId);
		noticeReader.setUserId(JBoltUserKit.getUserId());
		boolean success=noticeReader.save();
		return ret(success);
	}
	/**
	 * 是否存在指定通知读者
	 * @param sysNoticeId
	 * @param userId
	 * @return
	 */
	public boolean existsReader(Long sysNoticeId, Long userId) {
		return exists(selectSql().eq("sys_notice_id", sysNoticeId).eq("user_id", userId));
	}
	@Override
	protected int systemLogTargetType() {
		return 0;
	}

	
	/**
	 * 删除系统通知已读人
	 * @param sysNoticeId
	 */
	public void deleteBySysNoticeId(Long sysNoticeId) {
		if(notOk(sysNoticeId)) {
			throw new RuntimeException("sysNoticeId can not be null");
		}
		deleteBy(Okv.by("sys_notice_id", sysNoticeId));
	}
}
