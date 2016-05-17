package com.mxzgh.service;

import com.mxzgh.entity.UserEntity;
import com.mxzgh.uno.GameRoom;
import com.mxzgh.uno.UserModel;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/3.
 */
public interface GameService {

    public Object save(UserEntity test);

    UserEntity get(Long id);

    boolean exists(String username);

    String checkLogin(Map<String, String> dataMap);

    UserEntity checkLogin(String username, String password);

    List<GameRoom> getRooms();

    GameRoom createRoom(Map<String, String> dataMap, UserEntity user);

    UserModel joinRoom(Map<String, String> dataMap, UserEntity user);

    Object readyRoom(Map<String, String> dataMap, UserEntity user);

    Object startRoom(Map<String, String> dataMap, UserEntity user);

    Object runAction(Map<String, String> dataMap, UserEntity user);
}
