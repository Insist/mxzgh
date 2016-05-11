package com.mxzgh.uno.manager;

import com.mxzgh.entity.UserEntity;
import com.mxzgh.uno.GameRoom;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ServerManager {

    public static final Map<Long,GameRoom> ROOM_MAP = new ConcurrentHashMap<Long, GameRoom>();

    public static GameRoom getRoomById(Long roomId){
        return ROOM_MAP.get(roomId);
    }

    public static final AtomicInteger MAX_ROOM_ID = new AtomicInteger(0);

    public static GameRoom createRoom(Map<String, String> dataMap, UserEntity user) {
        String roomName = dataMap.get("roomName");
        Long roomId = Long.valueOf(ServerManager.MAX_ROOM_ID.getAndAdd(1));
        GameRoom room = new GameRoom();
        room.setRoomName(roomName);
        ServerManager.ROOM_MAP.put(roomId,room);
        return room;
    }
}
