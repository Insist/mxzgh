package com.mxzgh.uno;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/5/10.
 */
public class GameRoom {

    static final Map<Long,GameRoom> ROOM_MAP = new ConcurrentHashMap<Long, GameRoom>();

    String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
