package com.mxzgh.util;

import com.mxzgh.model.BaseModel;
import com.mxzgh.service.GameService;
import com.mxzgh.uno.GameRoom;
import com.mxzgh.uno.UserModel;
import com.mxzgh.uno.manager.ServerManager;

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

    Map<Long,Session> sessions = new ConcurrentHashMap<>();

    public void addSession(Long key,Session session){
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

    public void sendMessageToAll(String type,Object o){
        BaseModel baseModel = new BaseModel();
        baseModel.setType(type);
        baseModel.setData(o);
        sendMessageToAll(FasterJsonTools.writeValueAsString(baseModel));
    }

    public void sendMessage(Long id, String type,Object o) {
        Session session = sessions.get(id);
        if(session!=null){
            try {
                BaseModel baseModel = new BaseModel();
                baseModel.setType(type);
                baseModel.setData(o);
                session.getBasicRemote().sendText(FasterJsonTools.writeValueAsString(baseModel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendMessageToRoom(Long roomId, String type, Object o) {
        GameRoom room = ServerManager.getRoomById(roomId);
        for(UserModel user:room.getPlayers()){
           sendMessage(user.getUserId(),type,o);
        }
    }

    public Session getSessionByUid(Long id) {
        return sessions.get(id);
    }

}
