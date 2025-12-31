/**
 * JBolt websocket 文本消息handler处理
 */
export default {
	/**
	 * 核心方法处理文本消息返回事件
	 * @param res  {type:3,from:xxx,to:xxx,command:xxx,data:xxx}
	 */
	process:(res) => {
		alert(res.data);
	}
};