/** JBolt web socket js */
import JBoltWebSocketHandler from './jbolt-websocket-handler.js';

const reconnectDelays = [1000, 2000, 4000, 8000, 16000, 30000];
const stableConnectionTime = 10000;

const JBoltWS = {
    isOk: false,
    version: '0.1.0',
    genTokenUrl: 'admin/myToken',
    msgType: {COMMAND: 1, COMMAND_RET: 2, TEXT: 3, FILE: 4}
};

let connection = null;
let reconnectTimer = null;
let reconnectAttempt = 0;
let tokenRequest = null;
let pageHidden = false;
let sessionSuspended = false;
let lifecycleRegistered = false;

function canConnect() {
    return !pageHidden && !sessionSuspended && navigator.onLine !== false;
}

function isCurrentOpenConnection(current) {
    return connection === current && !current.retired && current.socket.readyState === window.WebSocket.OPEN;
}

function clearConnectionTimers(current) {
    clearInterval(current.pingTimer);
    for (const timer of current.delayedTasks) {
        clearTimeout(timer);
    }
    current.delayedTasks.clear();
}

function stopConnection() {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
    if (tokenRequest) {
        const request = tokenRequest;
        tokenRequest = null;
        request.abort();
    }
    JBoltWS.isOk = false;
    if (connection && !connection.retired) {
        // 旧连接收到 close 前仍占用连接位置，避免两个 socket 并存。
        connection.retired = true;
        clearConnectionTimers(connection);
        connection.socket.close();
    }
}

function registerLifecycle() {
    if (lifecycleRegistered) return;
    lifecycleRegistered = true;
    window.addEventListener('offline', stopConnection);
    window.addEventListener('online', () => JBoltWS.connect());
    window.addEventListener('pagehide', () => {
        pageHidden = true;
        stopConnection();
    });
    window.addEventListener('pageshow', event => {
        if (event.persisted) {
            pageHidden = false;
            JBoltWS.connect();
        }
    });
}

function scheduleForConnection(current, task, delayMs) {
    if (!isCurrentOpenConnection(current)) return;
    const timer = setTimeout(() => {
        current.delayedTasks.delete(timer);
        if (isCurrentOpenConnection(current)) task();
    }, delayMs);
    current.delayedTasks.add(timer);
}

function updateToken(client, token, tenantSn) {
    client.token = token;
    client.tenantSn = tenantSn || '';
    client.isTenant = Boolean(client.tenantSn);
    client.url = token ? client.protocol + '://' + client.host + client.ctx + '/websocket.ws/' + token
        + (client.isTenant ? '?jbtenantsn=' + client.tenantSn : '') : '';
}

function requestToken(client) {
    if (tokenRequest) return;
    const request = Ajax.get(client.genTokenUrl);
    tokenRequest = request;
    request.done(res => {
        if (tokenRequest !== request || !canConnect() || res.state !== 'ok') return;
        if (!res.data?.token) {
            LayerMsgBox.alert('初始化系统Websocket-获取系统授权TOKEN失败', 2);
            return;
        }
        updateToken(client, res.data.token, res.data.tenantSn);
        client.connect();
    }).always(() => {
        if (tokenRequest === request) tokenRequest = null;
    });
}

JBoltWS.init = function({protocol = 'ws', host = 'localhost', ctx = '', token = '', tenantSn = '',
    binaryType = 'arraybuffer', reconnInterval = reconnectDelays[0], heartbeatTimeout = 10000, callback}) {
    stopConnection();
    this.protocol = protocol;
    this.host = host;
    this.ctx = ('/' + (ctx || '')).replace(/^\/+/, '/').replace(/\/+$/, '');
    this.binaryType = binaryType;
    this.reconnInterval = reconnInterval;
    this.heartbeatTimeout = heartbeatTimeout;
    this.heartbeatSendInterval = heartbeatTimeout / 2;
    this.callback = callback;
    updateToken(this, token, tenantSn);
    reconnectAttempt = 0;
    sessionSuspended = false;
    registerLifecycle();
};

JBoltWS.lastInteractionTime = function(time) {
    if (time !== undefined) this.lastInteractionTimeValue = time;
    return this.lastInteractionTimeValue;
};

JBoltWS.startHeartBeatCheck = function() {
    const current = connection;
    if (!current || !isCurrentOpenConnection(current)) return;
    clearInterval(current.pingTimer);
    current.pingTimer = setInterval(() => {
        if (isCurrentOpenConnection(current)) this.ping();
    }, this.heartbeatSendInterval);
};

JBoltWS.connect = function() {
    if (!canConnect() || connection || reconnectTimer !== null) return;
    if (!this.token) {
        requestToken(this);
        return;
    }

    let socket;
    try {
        socket = new window.WebSocket(this.url);
    } catch (error) {
        console.error('JBolt WebSocket 连接失败', error.name);
        this.reconn();
        return;
    }
    socket.binaryType = this.binaryType;
    this.ws = socket;
    const current = {
        socket,
        retired: false,
        pingTimer: null,
        delayedTasks: new Set(),
        openedAt: null,
        receivedMessage: false
    };
    connection = current;

    socket.onopen = event => {
        if (!isCurrentOpenConnection(current)) return;
        current.openedAt = Date.now();
        this.isOk = true;
        this.lastInteractionTime(current.openedAt);
        JBoltWebSocketHandler.onopen(event);
        this.startHeartBeatCheck();
        if (this.callback) this.callback((task, delayMs) => scheduleForConnection(current, task, delayMs));
    };
    socket.onmessage = event => {
        if (!isCurrentOpenConnection(current)) return;
        if (JBoltWebSocketHandler.onmessage(event)) {
            current.receivedMessage = true;
            this.lastInteractionTime(Date.now());
        }
    };
    socket.onerror = event => {
        if (connection === current && !current.retired) JBoltWebSocketHandler.onerror(event);
    };
    socket.onclose = event => {
        if (connection !== current) return;
        clearConnectionTimers(current);
        connection = null;
        this.ws = null;
        this.isOk = false;
        if (current.retired) {
            this.connect();
            return;
        }
        JBoltWebSocketHandler.onclose(event);
        if (current.receivedMessage && Date.now() - current.openedAt >= stableConnectionTime) {
            reconnectAttempt = 0;
        }
        this.reconn();
    };
};

JBoltWS.reconn = function() {
    if (!canConnect() || connection || reconnectTimer !== null) return;
    const base = Math.min(reconnectDelays[reconnectDelays.length - 1],
        this.reconnInterval * reconnectDelays[reconnectAttempt] / reconnectDelays[0]);
    reconnectAttempt = Math.min(reconnectAttempt + 1, reconnectDelays.length - 1);
    const delay = Math.floor(base * (0.8 + Math.random() * 0.2));
    jboltlog('JBolt WebSocket 将在 ' + delay + ' ms 后重连');
    reconnectTimer = setTimeout(() => {
        reconnectTimer = null;
        this.connect();
    }, delay);
};

JBoltWS.ping = function() {
    const idleTime = Date.now() - this.lastInteractionTime();
    if (this.heartbeatSendInterval + idleTime >= this.heartbeatTimeout) {
        this.sendCommand({command: 'ping'});
    }
};

JBoltWS.suspendSession = function() {
    sessionSuspended = true;
    updateToken(this, '', '');
    stopConnection();
};

JBoltWS.refreshSession = function(token, tenantSn) {
    this.suspendSession();
    updateToken(this, token, tenantSn);
    reconnectAttempt = 0;
    sessionSuspended = false;
    this.connect();
};

	/** 执行发送指令到服务器
	 */
	JBoltWS.sendCommand = function({command,data,to=-1}) {
		this.send({
			type:JBoltWS.msgType.COMMAND,
			command:command,
			data:data,
			to:to
		});
	}
	/** 执行发送文本
	 */
	JBoltWS.sendText = function({data,to=-1}) {
		this.send({
			type:JBoltWS.msgType.TEXT,
			data:data,
			to:to
		});
	}

	/** 执行发送
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


	/** 设置生成token的url
	 */
	JBoltWS.setGenTokenUrl=function(url){
		this.genTokenUrl = url||"admin/myToken";
	}

	/** 设置guest模式
	 */
	JBoltWS.setGuestMode=function(guestMode){
		if(guestMode){
			JBoltWS.setGenTokenUrl("api/ws/guest/token/v2/gen");
		}else{
			JBoltWS.setGenTokenUrl("admin/myToken");
		}
	}

/** 初始化后台 WebSocket，保留显式令牌及访客模式入口。 */
JBoltWS.initJBoltWebsocket = function(ctx, callback, token, tenantSn) {
    jboltlog('JBolt WebSocket v' + this.version + ' starting');
    if (!window.WebSocket) window.WebSocket = window.MozWebSocket;
    if (!window.WebSocket) {
        LayerMsgBox.alert('当前浏览器不支持websocket', 2);
        return;
    }
    this.initTheJBoltWebsocket(ctx, token, tenantSn, callback);
};

JBoltWS.initTheJBoltWebsocket = function(ctx, token, tenantSn, callback) {
    this.init({
        protocol: window.location.protocol === 'https:' ? 'wss' : 'ws',
        host: window.location.host,
        ctx,
        token,
        tenantSn,
        callback
    });
    this.connect();
};

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

