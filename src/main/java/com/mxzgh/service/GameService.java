package com.mxzgh.service;

import com.mxzgh.entity.UserEntity;
import com.mxzgh.uno.GameRoom;

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
}
