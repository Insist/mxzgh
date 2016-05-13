package com.mxzgh.uno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/5/10.
 */
public class GameRoom {

    List<UserModel> players = Collections.synchronizedList(new ArrayList<UserModel>());

    String roomName;

    Long roomId;

    public AtomicInteger playerId = new AtomicInteger(0);

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public List<UserModel> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserModel> players) {
        this.players = players;
    }
}
