/**
 * JBolt web socket js
 */
import JBoltWebSocketHandler from './jbolt-websocket-handler.js'
	//jbolt websocket
	let JBoltWS     = {};
	JBoltWS.isOk = false;
	JBoltWS.version = "0.1.0";
	JBoltWS.genTokenUrl = "admin/myToken";
	//消息类型定义
	JBoltWS.msgType = {COMMAND:1,COMMAND_RET:2,TEXT:3,FILE:4};
	/**
	 * 初始化
	 * @param protocol              协议 wss or ws
	 * @param host                  hostname:port
	 * @param ctx                   context
	 * @param token                 用户令牌
	 * @param tenantSn              租户SN
	 * @param binaryType            数据类型 'blob' or 'arraybuffer'
	 * @param reconnInterval        重连间隔时间
	 * @param heartbeatTimeout      心跳超时时间
	 */
	JBoltWS.init = function({protocol = "ws", host = "localhost", ctx='',token='',tenantSn='', binaryType = 'arraybuffer', reconnInterval = 1000 , heartbeatTimeout = 10000, callback}){
		this.protocol              = protocol;
		this.host                  = host;
		this.token                 = token;
		this.tenantSn              = tenantSn;
		this.isTenant              = (tenantSn!=null&&tenantSn.length>0);
		if(!ctx || ctx==='/'){
			ctx='';
		}else if(ctx.length > 1){
			if(ctx.endWith('/')){
				ctx = ctx.substring(0,subString.length-1);
			}
			if(!ctx.startWith('/')){
				ctx = '/' + ctx;
			}
		}else if(ctx.length == 1){
			ctx = '/' + ctx;
		}else{
			ctx='';
		}
		this.ctx                   = ctx;
		if(this.isTenant){
			this.url                   = protocol + '://' + host + ctx + '/websocket.ws/' + token + "?jbtenantsn=" + tenantSn;
		}else{
			this.url                   = protocol + '://' + host + ctx + '/websocket.ws/' + token;
		}
		this.binaryType            = binaryType;
		this.reconnInterval        = reconnInterval;
		this.heartbeatTimeout      = heartbeatTimeout;
		this.heartbeatSendInterval = heartbeatTimeout / 2;
		this.callback              = callback;
		this.lastInteractionTime   = function(time) {
			if (time) {
			  this.lastInteractionTimeValue = time;
			}
			return this.lastInteractionTimeValue;
		}
	}


	/**
	 * 开启心跳检测
	 */
	JBoltWS.startHeartBeatCheck = function() {
		jboltlog("JBolt websocket start heartBeatCheck...");
		let self = this;
		//设置启动心跳检测
        this.pingIntervalId = setInterval(()=>self.ping(),self.heartbeatSendInterval);
	}

	/**
	 * 连接服务端
	 */
	JBoltWS.connect = function() {
		jboltlog("JBolt websocket connecting...");
		if (this.ws == null || this.ws.readyState != 1){
	       this.ws = null;
	       try{
	    	   	this.ws = new WebSocket(this.url);
	    	   	this.ws.binaryType = this.binaryType;
	          }catch(e){
	        	this.ws = null;
	        	console.error(e);
	        	this.reconn();// 重连
	         }
	     }

		if(!this.ws){
			jboltlog("connect fail! Websocket连接失败");
			return;
		}
		var self = this;
		//open状态处理
		this.ws.onopen = function(event) {
			jboltlog("JBolt websocket onopen...");
			JBoltWebSocketHandler.onopen(event);
			JBoltWS.isOk = true;
			console.log("JBolt websocket isOk!");
			//设置最后一次交互时间
            self.lastInteractionTime(new Date().getTime());
            //启动心跳检测
            self.startHeartBeatCheck();
            //执行链接后回调函数
            if(self.callback){
            	self.callback();
            }
		};
		//收到服务器消息处理
		this.ws.onmessage = function(event) {
			jboltlog("JBolt websocket onmessage...");
			JBoltWebSocketHandler.onmessage(event);
			//设置最后一次交互时间
			self.lastInteractionTime(new Date().getTime());
		};
		//连接关闭处理
		this.ws.onclose = function (event) {
			jboltlog("JBolt websocket onclose...");
			JBoltWS.isOk = false;
			//关闭心跳检测
			clearInterval(self.pingIntervalId);
			JBoltWebSocketHandler.onclose(event);
			//执行重连
			self.reconn();
		};

		//通讯error 处理
		this.ws.onerror = function(event) {
			jboltlog("JBolt websocket onclose...");
			JBoltWebSocketHandler.onerror(event);
		};
	}

	/**
	 * 重连接服务端
	 */
	JBoltWS.reconn = function() {
		jboltlog("JBolt websocket reconnecting after:" + this.reconnInterval +"ms");
		let self = this;
		setTimeout(function () {
			self.connect();
		}, self.reconnInterval);
	}


	/**
	 * 执行心跳ping
	 */
	JBoltWS.ping = function() {
		jboltlog("JBolt websocket check ping...");
		let iv = new Date().getTime() - this.lastInteractionTime(); // 已经多久没发消息了 // 单位：秒
		if ((this.heartbeatSendInterval + iv) >= this.heartbeatTimeout) {
			jboltlog("JBolt websocket ping doing...");
			this.sendCommand({command:"ping"});
			jboltlog("JBolt websocket ping done...");
		}else{
			jboltlog("JBolt websocket ping cancel...");
		}
	}

	/**
	 * 执行发送指令到服务器
	 */
	JBoltWS.sendCommand = function({command,data,to=-1}) {
		this.send({
			type:JBoltWS.msgType.COMMAND,
			command:command,
			data:data,
			to:to
		});
	}
	/**
	 * 执行发送文本
	 */
	JBoltWS.sendText = function({data,to=-1}) {
		this.send({
			type:JBoltWS.msgType.TEXT,
			data:data,
			to:to
		});
	}

	/**
	 * 执行发送
	 */
	JBoltWS.send = function({type = JBoltWS.msgType.TEXT,command,data,to=-1}) {
		if (this.isOk && this.ws != null && this.ws.readyState == 1) {
			if(command && type != JBoltWS.msgType.COMMAND){
				type = JBoltWS.msgType.COMMAND;
			}
			var msg = JSON.stringify({type:type,command:command,data:data,to:to});
			jboltlog("JBolt websocket send:"+msg);
			this.ws.send(msg);
		}else{
			jboltlog("JBolt websocket send failed!");
		}
	}


	/**
	 * 设置生成token的url
	 * @param url
	 */
	JBoltWS.setGenTokenUrl=function(url){
		this.genTokenUrl = url||"admin/myToken";
	}

	/**
	 * 设置guest模式
	 */
	JBoltWS.setGuestMode=function(guestMode){
		if(guestMode){
			JBoltWS.setGenTokenUrl("api/ws/guest/token/v2/gen");
		}else{
			JBoltWS.setGenTokenUrl("admin/myToken");
		}
	}

	/**
	 * 初始化JBolt webSocket
	 */
	JBoltWS.initJBoltWebsocket =  function(ctx,callback,token,tenantSn) {
		console.log("JBolt websocket v"+this.version + " starting...");
		// 判断当前浏览器是否支持WebSocket或者MozWebSocket
		if (!window.WebSocket) {
			window.WebSocket = window.MozWebSocket;
		}
		// 判断当前浏览器是否支持WebSocket
		if (window.WebSocket) {
			if(token){
				JBoltWS.initTheJBoltWebsocket(ctx,token,tenantSn,callback);
			}else{
				Ajax.get(this.genTokenUrl,function(res){
					if(res.data.token){
						JBoltWS.initTheJBoltWebsocket(ctx,res.data.token,res.data.tenantSn,callback);
					}else{
						LayerMsgBox.alert('初始化系统Websocket-获取系统授权TOKEN失败',2);
					}
				});
			}
		} else {
			LayerMsgBox.alert('当前浏览器不支持websocket',2);
		}
	}

	/**
	 * 初始化
	 * @param ctx
	 * @param token
	 * @param tenantSn
	 * @param callback
	 */
	JBoltWS.initTheJBoltWebsocket=function(ctx,token,tenantSn,callback){
		if(!token){
			LayerMsgBox.alert("未指定用户Token,无法初始化websocket");
			return false;
		}
		let host = window.location.host;
		let protocol = window.location.protocol.split(':')[0];
		if(protocol === "https"){
			protocol = "wss";
		}else{
			protocol = "ws";
		}

		// 执行JBoltWS初始化
		JBoltWS.init({
			protocol:protocol,
			host:host,
			ctx:ctx,
			token:token,
			tenantSn:tenantSn,
			callback:callback
		});
		// 执行connect
		JBoltWS.connect();
	}


	/**
	 * 注册command-handler
	 * @param command 指令名称
	 * @param handler 指令处理器
	 */
	JBoltWS.registerCommand = function(command,handler) {
		JBoltWebSocketHandler.registeredCommands[command] = handler;
	}
	/**
	 * 移除页面自定义注册的command-handler
	 * @param command 指令名称 多个用逗号隔开或者数组
	 */
	JBoltWS.removeCommand = function(command) {
		JBoltWebSocketHandler.removeCommand(command);
	}
window.JBoltWS = JBoltWS;
export default JBoltWS

