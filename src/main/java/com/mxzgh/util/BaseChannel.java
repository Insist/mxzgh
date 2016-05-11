package com.mxzgh.util;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/5/5.
 */
public class BaseChannel {

    Map<String,Session> sessions = new ConcurrentHashMap<String, Session>();

    public void addSession(String key,Session session){
        this.sessions.put(key,session);
    }

    public void sendMessageToAll(String message){
        for(Session session:sessions.values()){
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
