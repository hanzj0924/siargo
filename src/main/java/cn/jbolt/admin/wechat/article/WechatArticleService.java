package cn.jbolt.admin.wechat.article;

import cn.jbolt.common.model.WechatArticle;
import cn.jbolt.core.service.base.JBoltBaseService;

/**
 * 微信图文信息管理
 * 包括本地和公众号内的数据   
 * @ClassName:  WechatArticleService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月9日 上午12:06:52   
 */
public class WechatArticleService extends JBoltBaseService<WechatArticle> {
	private WechatArticle dao = new WechatArticle().dao();
	@Override
	protected WechatArticle dao() {
		return dao;
	}
	@Override
	protected int systemLogTargetType() {
		// TODO Auto-generated method stub
		return 0;
	}

}
