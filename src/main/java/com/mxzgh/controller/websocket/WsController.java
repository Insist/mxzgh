package com.mxzgh.controller.websocket;

import com.mxzgh.entity.UserEntity;
import com.mxzgh.model.BaseModel;
import com.mxzgh.model.BaseResponse;
import com.mxzgh.service.GameService;
import com.mxzgh.service.UserService;
import com.mxzgh.util.ChannelUtils;
import com.mxzgh.util.FasterJsonTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/26.
 */
@ServerEndpoint(value = "/websocket")
public class WsController {

    Logger logger = Logger.getLogger(this.getClass());

    Session session;
    UserEntity user;

    @Autowired
    UserService userService;

    @Autowired
    GameService gameService;

    @OnOpen
    public void open(Session s) {
        logger.info(s.getId()+" has login");
        this.session = s;
//        ChannelUtils.chatChannel.addSession(s.getId(),s);
//        ChannelUtils.chatChannel.sendMessageToAll(s.getId()+" has login");
    }
    @OnClose
    public void close(CloseReason c) {
    }
    @OnError
    public void error(Throwable t) {
    }
    @OnMessage
    public String receiveMessage(String message) {
//        ChannelUtils.chatChannel.sendMessageToAll(message);
        logger.info("receive Message:"+message);
        Map<String,String> dataMap = FasterJsonTools.readValue2Map(message, String.class, String.class);
        BaseModel baseModel = new BaseModel();
        baseModel.setType(dataMap.get("type"));
        baseModel.setData(handlerMessage(dataMap));
        return FasterJsonTools.writeValueAsString(baseModel);
    }

    public Object handlerMessage(Map<String,String> dataMap){
        if(dataMap.get("type")==null){
            return "";
        }
        if("auth".equals(dataMap.get("type"))){
            UserEntity user = userService.checkLogin(dataMap);
            this.user = user;
            return user==null?BaseResponse.createResponse(-1,"error"): BaseResponse.SUCCESS;
        }
        if("getRooms".equals(dataMap.get("type"))){
            return gameService.getRooms();
        }
        if("createRoom".equals(dataMap.get("type"))){
            return gameService.createRoom(dataMap,this.user);
        }
        return null;
    }

}
