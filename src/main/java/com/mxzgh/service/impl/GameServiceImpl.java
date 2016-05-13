package com.mxzgh.service.impl;

import com.mxzgh.dao.UserDao;
import com.mxzgh.entity.UserEntity;
import com.mxzgh.service.GameService;
import com.mxzgh.uno.GameBoardManager;
import com.mxzgh.uno.GameRoom;
import com.mxzgh.uno.UserModel;
import com.mxzgh.uno.manager.ServerManager;
import com.mxzgh.util.ChannelUtils;
import com.mxzgh.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/3.
 */
@Service
public class GameServiceImpl implements GameService {
    @Autowired
    private UserDao userDao;

    public Object save(UserEntity test) {
        return userDao.save(test);
    }

    public UserEntity get(Long id) {
        return userDao.getById(id);
    }

    public boolean exists(String username) {
        return userDao.getByProperty("username", username) != null;
    }

    public String checkLogin(Map<String, String> dataMap) {
        String uid = dataMap.get("uid");
        String token = dataMap.get("token");
        Map<String ,Object> map = new HashMap<String, Object>();
        map.put("id",uid);
        map.put("token",token);
        return userDao.getByProperty(map) == null?"error":"success";
    }

    public UserEntity checkLogin(String username, String password) {
        Map<String ,Object> map = new HashMap<String, Object>();
        map.put("username",username);
        map.put("password",password);
        UserEntity user = userDao.getByProperty(map);
        user.setToken(MD5Util.MD5("COMIAI_UNOGAME"));
        userDao.update(user);
        return user;
    }

    public List<GameRoom> getRooms() {
        return new ArrayList<GameRoom>(ServerManager.ROOM_MAP.values());
    }

    public GameRoom createRoom(Map<String, String> dataMap, UserEntity user) {
        return ServerManager.createRoom(dataMap,user);
    }

    @Override
    public UserModel joinRoom(Map<String, String> dataMap, UserEntity user) {
        return ServerManager.joinRoom(dataMap,user);
    }

    @Override
    public Object readyRoom(Map<String, String> dataMap, UserEntity user) {
        UserModel userModel = ServerManager.USER_MAP.get(user.getId());
        userModel.setIsReady(true);
        ChannelUtils.GAME_CHANNEL.sendMessageToRoom(userModel.getRoomId(),"getReady",userModel);
        return null;
    }

    @Override
    public Object startRoom(Map<String, String> dataMap, UserEntity user) {
        UserModel userModel = ServerManager.USER_MAP.get(user.getId());
        GameRoom room = ServerManager.ROOM_MAP.get(userModel.getRoomId());
        ChannelUtils.GAME_CHANNEL.sendMessageToRoom(userModel.getRoomId(),"startGame",room);
        GameBoardManager manager = new GameBoardManager(room);
        ServerManager.GAME_BOARD_MANAGER_MAP.put(room.getRoomId(),manager);
        new Thread(manager).start();
        return null;
    }

    @Override
    public Object playCard(Map<String, String> dataMap, UserEntity user) {
        Long roomId = ServerManager.USER_MAP.get(user.getId()).getRoomId();
        GameBoardManager manager = ServerManager.GAME_BOARD_MANAGER_MAP.get(roomId);
        dataMap.put("action","0");
        manager.doAction(dataMap,user.getId());
        manager.unlock();
        return null;
    }
}
