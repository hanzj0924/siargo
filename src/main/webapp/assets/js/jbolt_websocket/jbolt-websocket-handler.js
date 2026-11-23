/** JBolt websocket 消息 handler处理
 */
import JBoltWS from "./jbolt-websocket.js"
import JBoltCommands from "./jbolt-websocket-command.js"
import JBoltCommandsExtend from "./extend/jbolt-websocket-command-extend.js"
import JBoltTextMsgHandler from "./jbolt-websocket-textmsg-handler.js"
import JBoltTextMsgHandlerExtend from "./extend/jbolt-websocket-textmsg-handler-extend.js"


export default {
	/** 页面自定义注册command {command:handler}
	 */
	registeredCommands:{},
	/**
	 * 移除注销指定command 只能移除页面自定义注册的 不能移除commandhandler里直接写的
	 * @param command 自定义注册的command 多个用逗号隔开
	 */
	removeCommand(command){
		if(!command){return;}
		if(isArray(command)){
			var cmd;
			for(var i in command){
				cmd = command[i];
				if(cmd){
					this.registeredCommands[cmd] = null;
				}
			}
		}else if(command.indexOf(",") != -1){
			var cmds = command.split(",");
			var cmd;
			for(var i in cmds){
				cmd = cmds[i];
				if(cmd){
					this.registeredCommands[cmd] = null;
				}
			}
		}else{
			this.registeredCommands[command] = null;
		}
		console.log("removeCommand:" , command);
	},
	/**
	 * 触发command-handler
	 * @param command 指令名称
	 * @param eventData 事件数据
	 */
	emitCommand(command,eventData) {
		if(!command){
			console.error("emitCommand指定command为空");
			return;
		}
    	let handler = JBoltCommands[command] || JBoltCommandsExtend[command] || this.registeredCommands[command];
        if (handler) {
            handler(eventData);
        }else{
        	jboltlog("emitCommand没有找到指令["+command+"]对应处理handler 如果是自定义注册command可能是移除了");
        }
	},
    /** onopen事件
     */
    onopen(event){
        jboltlog("handler:websocket connection open.");
        jboltlog(event);
    },
    /** onmessage事件
     */
    onmessage(event) {
        jboltlog("handler:websocket message received.");
        jboltlog(event.data);
        if (!event.data) return false;
        const edata = JSON.parse(event.data);
        if (!edata) return false;
        if (edata.type == JBoltWS.msgType.COMMAND || edata.type == JBoltWS.msgType.COMMAND_RET) {
            this.emitCommand(edata.command, edata);
            return Boolean(edata.command);
        }
        if (edata.type == JBoltWS.msgType.TEXT) {
            JBoltTextMsgHandler.process(edata);
            JBoltTextMsgHandlerExtend.process(edata);
            return true;
        }
        return false;
    },
    /** onclose事件
     */
    onclose(event) {
        jboltlog("handler:websocket connection close.");
        jboltlog(event.code);
    },
    /** onerror事件
     */
    onerror(event) {
        jboltlog("handler:websocket connection error.");
        jboltlog(event);
    }
}
