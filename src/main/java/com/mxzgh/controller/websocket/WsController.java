package com.mxzgh.controller.websocket;

import com.mxzgh.entity.UserEntity;
import com.mxzgh.model.BaseModel;
import com.mxzgh.model.BaseResponse;
import com.mxzgh.util.ChannelUtils;
import com.mxzgh.util.FasterJsonTools;
import com.mxzgh.util.ServiceUtil;
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/26.
 */
@ServerEndpoint(value = "/websocket")
public class WsController {

    Logger logger = Logger.getLogger(this.getClass());

    Session session;

    UserEntity user;

    @OnOpen
    public void open(Session s) {
        this.session = s;
    }
    @OnClose
    public void close(CloseReason c) {
        logger.warn(c);
    }
    @OnError
    public void error(Throwable t) {
    }
    @OnMessage
    public void receiveMessage(String message,Session session) {
        try {
//        ChannelUtils.GAME_CHANNEL.sendMessageToAll(message);
            logger.info("receive Message:" + message);
            Map<String,String> dataMap = FasterJsonTools.readValue2Map(message, String.class, String.class);
            BaseModel baseModel = new BaseModel();
            baseModel.setType(dataMap.get("type"));
            baseModel.setData(handlerMessage(dataMap));
            if(!"runAction".equals(baseModel.getType())){
                String response = FasterJsonTools.writeValueAsString(baseModel);
                logger.info("response Message:" + response);
                session.getAsyncRemote().sendText(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object handlerMessage(Map<String,String> dataMap){
        try {
            if(dataMap.get("type")==null){
                return "";
            }
            if("auth".equals(dataMap.get("type"))){
                UserEntity user = ServiceUtil.getUserService().checkLogin(dataMap);
                this.user = user;
                logger.info(user.getUsername()+" has login");
                ChannelUtils.GAME_CHANNEL.addSession(user.getId(),session);
                return user==null?BaseResponse.createResponse(-1,"error"): BaseResponse.SUCCESS;
            }
            if("getRooms".equals(dataMap.get("type"))){
                return ServiceUtil.getGameService().getRooms();
            }
            if("createRoom".equals(dataMap.get("type"))){
                return ServiceUtil.getGameService().createRoom(dataMap, this.user);
            }
            if("joinRoom".equals(dataMap.get("type"))){
                return ServiceUtil.getGameService().joinRoom(dataMap, this.user);
            }
            if("readyRoom".equals(dataMap.get("type"))){
                return ServiceUtil.getGameService().readyRoom(dataMap, this.user);
            }
            if("startRoom".equals(dataMap.get("type"))){
                return ServiceUtil.getGameService().startRoom(dataMap, this.user);
            }
            if("runAction".equals(dataMap.get("type"))){
                return ServiceUtil.getGameService().runAction(dataMap, this.user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
