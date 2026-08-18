package com.firefox.center.sys.core.message.websocket;

import com.firefox.center.common.R;
import com.firefox.center.sys.common.constant.WebsocketConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("webSocketApi")
public class WebSocketController {
	
    @Autowired
    private WebSocket webSocket;
 
    @PostMapping("/sendAll")
    public R<String> sendAll(@RequestBody JSONObject jsonObject) {
    	R<String> result = new R<String>();
    	String message = jsonObject.getString("message");
    	JSONObject obj = new JSONObject();
    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
		obj.put(WebsocketConst.MSG_ID, "M0001");
		obj.put(WebsocketConst.MSG_TXT, message);
    	webSocket.sendAllMessage(obj.toJSONString());
        result.setData("群发！");
        return result;
    }

    @PostMapping("/sendUser")
    public R<String> sendUser(@RequestBody JSONObject jsonObject) {
    	R<String> result = new R<String>();
    	String userId = jsonObject.getString("userId");
    	String message = jsonObject.getString("message");
    	JSONObject obj = new JSONObject();
    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
    	obj.put(WebsocketConst.MSG_USER_ID, userId);
		obj.put(WebsocketConst.MSG_ID, "M0001");
		obj.put(WebsocketConst.MSG_TXT, message);
        webSocket.sendOneMessage(userId, obj.toJSONString());
        result.setData("单发");
        return result;
    }
    
}