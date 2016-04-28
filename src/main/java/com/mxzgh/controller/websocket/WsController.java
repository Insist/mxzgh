package com.mxzgh.controller.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by Administrator on 2016/4/26.
 */
@ServerEndpoint(value = "/websocket")
public class WsController {
    @OnOpen
    public void open(Session s) {
    }
    @OnClose
    public void close(CloseReason c) {
    }
    @OnError
    public void error(Throwable t) {
    }
    @OnMessage
    public String receiveMessage(String message) {
        return "123";
    }
}
