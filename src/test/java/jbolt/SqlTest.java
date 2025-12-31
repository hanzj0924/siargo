package jbolt;

import cn.jbolt.core.converter.htmltojson.JBoltHtmlToJson;
import cn.jbolt.core.converter.htmltojson.JBoltHtmlToJsonParams;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.util.JBoltStringUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class SqlTest{
	@Test
	public void test() {
		Sql sql;
		String var = "a'";
		final String SAFE_VAR = "'a'''";
		final String LIKE_SAFE_VAR = "'%a''%'";
		final String STARTWITH_SAFE_VAR = "'a''%'";
		final String ENDWITH_SAFE_VAR = "'%a'''";
		
//		sql = Sql.mysql().insert("jb_user(username,sex)").values(var, 1);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").in("username", var);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").instr("username", var, true);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").startWith("username", var);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(STARTWITH_SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").endWith("username", var);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(ENDWITH_SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").like("username", var);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(LIKE_SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").findInSet(var,"username",true);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));
//
//		sql = Sql.mysql().select().from("jb_user").findInSet(var,"fdfda'",false);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));
//
//		sql = Sql.mysql().update("jb_user").set("username", var).set("sex", 1).eq("id", 1);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));
//
//		sql = Sql.mysql().delete().from("jb_user").in("username", var);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains(SAFE_VAR));

//		sql=Sql.sqlserver().instr("name","222,333,444",false);
//		System.out.println(sql);
//		assertTrue(sql.toString().contains("charindex"));


//		sql = Sql.mysql().select("a.*").from("jb_user","uu").count();
//		System.out.println(sql);
	}

//	@Test
//	public void test2() {
//		String html="<div class=\"_2Zphx \"> <div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">新华社北京12月10日电题：携手构建更加紧密的中阿命运共同体</span></p><!--29--><!--30--><!--31--><!--32--><!--33--><!--34--><!--35--><!--36--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">新华社评论员</span></p><!--37--><!--38--><!--39--><!--40--><!--41--><!--42--><!--43--><!--44--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">美丽的花园城市利雅得，见证中阿关系发展史上具有划时代意义的里程碑。</span></p><!--45--><!--46--><!--47--><!--48--><!--49--><!--50--><!--51--><!--52--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">当地时间12月9日下午，首届中国－阿拉伯国家峰会在沙特首都利雅得举行。习近平主席出席峰会并发表主旨讲话，站在历史高度回望中阿友谊，着眼时代大势擘画合作蓝图，为构建面向新时代的中阿命运共同体指明正确方向，注入强劲动力。</span></p><!--53--><!--54--><!--55--><!--56--><!--57--><!--58--><!--59--><!--60--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">千年友谊传佳话，登高望远谱新篇。习近平主席出席首届中阿峰会，是新中国成立以来中国面向阿拉伯世界规模最大、规格最高的外交行动。当前，百年变局加速演进，世纪疫情反复延宕，中国和阿拉伯国家都面临实现民族振兴、加快国家发展的历史任务。首届中阿峰会的召开，是双方在当前形势下加强团结协作的战略抉择，标志双方友好合作进入新阶段，将为中阿战略伙伴关系开辟更为广阔的前景，引领中国和阿拉伯国家在构建中阿命运共同体的道路上大踏步前进。</span></p><!--61--><!--62--><!--63--><!--64--><!--65--><!--66--><!--67--><!--68--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">“未之见而亲焉，可以往矣；久而不忘焉，可以来矣。”中国和阿拉伯国家友好交往源远流长，在丝绸古道中相知相交，在民族解放斗争中患难与共，在经济全球化浪潮中合作共赢，在国际风云变幻中坚守道义，凝聚成“守望相助、平等互利、包容互鉴”的中阿友好精神。守望相助是中阿友好的鲜明特征，平等互利是中阿友好的不竭动力，包容互鉴是中阿友好的价值取向——习近平主席的精辟总结，道出中阿友谊牢不可破、历久弥新的奥秘所在，为中阿合作携手前行、开创未来指引正确方向。</span></p><!--69--><!--70--><!--71--><!--72--><!--73--><!--74--><!--75--><!--76--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">一路走来，中阿关系在世界发展进步的大潮中不断发展，在人类历史前进的步伐中不断前进。从提出弘扬丝路精神的中国倡议，到贡献破解“中东之问”的中国方案，习近平主席始终关注中东局势，推动中阿关系提质升级。2014年，习近平主席在中阿合作论坛第六届部长级会议开幕式上首次提出打造中阿利益共同体和命运共同体，得到阿拉伯国家积极响应。当前，世界进入新的动荡变革期，中东地区正在发生新的深刻变化。阿拉伯人民要求和平与发展的愿望更加迫切，追求公平正义的呼声更加强烈。着眼继承和发扬中阿友好精神、构建更加紧密的中阿命运共同体，习近平主席提出四点倡议：坚持独立自主，维护共同利益；聚焦经济发展，促进合作共赢；维护地区和平，实现共同安全；加强文明交流，增进理解信任。</span></p><!--77--><!--78--><!--79--><!--80--><!--81--><!--82--><!--83--><!--84--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">从践行真正的多边主义，维护广大发展中国家正当权益，到加强发展战略对接，高质量共建“一带一路”；从支持阿方运用阿拉伯智慧推动政治解决热点难点问题，到扩大人员往来，深化人文合作，开展治国理政经验交流，四点倡议既有一以贯之的理念，也有与时俱进的创新。它体现了“重和平、尚和谐、讲信义、求真知”的文明真谛，彰显着中国对国际公道正义的不懈坚守、对中东地区和平安宁的真诚期盼，在新形势下发出了促进发展中国家团结合作的时代强音，必将为推动构建人类命运共同体汇聚起更广泛而弘大的力量。</span></p><!--85--><!--86--><!--87--><!--88--><!--89--><!--90--><!--91--><!--92--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">阿拉伯谚语讲，“语言是叶子，行动才是果实”。中国古人讲，“锲而不舍，金石可镂”。此次峰会决定全力构建面向新时代的中阿命运共同体，并制定《中阿全面合作规划纲要》。作为构建中阿命运共同体和落实《中阿全面合作规划纲要》的第一步，未来3到5年，中方将携手阿方推进“八大共同行动”，涵盖支持发展、粮食安全、卫生健康、绿色创新、能源安全、文明对话、青年成才、安全稳定等8个领域。一系列务实举措，诠释着双方传承历史友好、深化战略合作的决心与行动。“很高兴有中国这样的伟大朋友”“同中方开展合作没有上限”“中方提出的一系列重要倡议、推出的重大举措有利于世界”……各国领导人的热忱话语，涌动真挚的情谊，展现中阿合作的大势所趋、人心所向。</span></p><!--93--><!--94--><!--95--><!--96--><!--97--><!--98--><!--99--><!--100--></div><div class=\"_3ygOc lg-fl \"><p><span class=\"bjh-p\">坚志而勇为，载道以日新。新形势下，弘扬中阿友好精神，携手构建面向新时代的中阿命运共同体，定能开创中阿关系更加辉煌灿烂的明天，为人类和平与发展的崇高事业作出新的更大贡献。传承数千年的中阿友好情谊，正在翻开新的篇章。（完）</span></p><!--101--><!--102--><!--103--><!--104--><!--105--><!--106--><!--107--><!--108--></div><!--28--><!--109--><!--110--><div class=\"_213jB\"><div class=\"_1LjGN\"><span>举报/反馈</span></div></div></div>";
//		String ret = JBoltStringUtil.processHtmlToTextContent(html,6,200,255,new String[]{});
//		System.out.println(ret);
//		System.out.println(ret.length());
//	}

//	@Test
//	public void test3() {
//		String html="<p>书友们，大家好！</p>\n" +
//				"<p>为了鼓励我们的读书会学员能积极参与读书交流，真正的学有所获。我们现举办一期优秀读书心得评选活动。</p>\n" +
//				"<p>本次共评选<span style=\"color:hsl(0,75%,60%);\">【一等奖】1名；【二等奖】1名；【三等奖】1名。</span></p>\n" +
//				"<p>每人可以投一票，请点击要投票的读书心得链接，在读书心得帖子下方点赞。本次评选以截止时间终了，最终的点赞数进行排名。</p>\n" +
//				"<p><span style=\"color:hsl(0,75%,60%);\">【截止时间】本周五2023年2月10日晚20:00分。</span></p>\n" +
//				"<p>希望各位积极参与。以下是候选读书心得链接：</p>\n" +
//				"<p>1.来自哄一哄：</p>\n" +
//				"<section class=\"grouppost-item\" id=\"1602254523913244672_1_1607755004966113280\" type=\"1\" groupid=\"1602254523913244672\">\n" +
//				" <div class=\"grouppost-wrapper\">\n" +
//				"  <div class=\"grouppost-type\">\n" +
//				"   文章\n" +
//				"  </div>\n" +
//				"  <h1 class=\"grouppost-title\">陶叔领读贴一 个人理解</h1>\n" +
//				" </div>\n" +
//				"</section>\n" +
//				"<p>2.来自任孟春：</p>\n" +
//				"<section class=\"grouppost-item\" id=\"1602254523913244672_1_1610619179216179200\" type=\"1\" groupid=\"1602254523913244672\">\n" +
//				" <div class=\"grouppost-wrapper\">\n" +
//				"  <div class=\"grouppost-type\">\n" +
//				"   文章\n" +
//				"  </div>\n" +
//				"  <h1 class=\"grouppost-title\">第一遍读《重读马克思》有一点思考</h1>\n" +
//				" </div>\n" +
//				"</section>\n" +
//				"<p>3.来自灯塔：</p>\n" +
//				"<section class=\"grouppost-item\" id=\"1602254523913244672_1_1610967162369642496\" type=\"1\" groupid=\"1602254523913244672\">\n" +
//				" <div class=\"grouppost-wrapper\">\n" +
//				"  <div class=\"grouppost-type\">\n" +
//				"   文章\n" +
//				"  </div>\n" +
//				"  <h1 class=\"grouppost-title\">为认知的颠覆起个序章</h1>\n" +
//				" </div>\n" +
//				"</section>\n" +
//				"<p>4.来自半糖：</p>\n" +
//				"<section class=\"grouppost-item\" id=\"1602254523913244672_3_1611566751984422912\" type=\"3\" groupid=\"1602254523913244672\">\n" +
//				" <div class=\"grouppost-wrapper\">\n" +
//				"  <div class=\"grouppost-type\">\n" +
//				"   动态\n" +
//				"  </div>\n" +
//				"  <h1 class=\"grouppost-title\">资本如何收割全世界？以下是我的理解，欢迎大家来指点讨论。 1.国王要发动战争，掠夺别国财富，但是一时没钱，于是向银行家借钱，银行家收取 8%的利息。国家用什么还钱？用未来的税收还。同时出售职位，让银</h1>\n" +
//				" </div>\n" +
//				"</section>\n" +
//				"<p>5.来自书海一舟：</p>\n" +
//				"<section class=\"grouppost-item\" id=\"1602254523913244672_1_1612291132637351936\" type=\"1\" groupid=\"1602254523913244672\">\n" +
//				" <div class=\"grouppost-wrapper\">\n" +
//				"  <div class=\"grouppost-type\">\n" +
//				"   文章\n" +
//				"  </div>\n" +
//				"  <h1 class=\"grouppost-title\">23.1.9《重读马克思》读书打卡第1天</h1>\n" +
//				" </div>\n" +
//				"</section>";
//		JBoltHtmlToJsonParams params = new JBoltHtmlToJsonParams();
//		params.setType(JBoltHtmlToJsonParams.TYPE_HTML);
//		params.setSkipEmptyTextNode(true);
//		JBoltHtmlToJson json = JBoltHtmlToJson.by(html,params);
//		String ret = json.get();
//		System.out.println(ret);
//		System.out.println(ret.length());
//	}

}
