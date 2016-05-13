package com.mxzgh.uno.manager;

import com.mxzgh.entity.UserEntity;
import com.mxzgh.uno.GameBoardManager;
import com.mxzgh.uno.GameRoom;
import com.mxzgh.uno.UserModel;
import com.mxzgh.util.BaseChannel;
import com.mxzgh.util.ChannelUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ServerManager {

    public static final Map<Long,GameRoom> ROOM_MAP = new ConcurrentHashMap<Long, GameRoom>();

    public static final Map<Long,UserModel> USER_MAP = new ConcurrentHashMap<Long, UserModel>();

    public static final Map<Long,GameBoardManager> GAME_BOARD_MANAGER_MAP = new ConcurrentHashMap<Long, GameBoardManager>();

    public static GameRoom getRoomById(Long roomId){
        return ROOM_MAP.get(roomId);
    }

    public static final AtomicInteger MAX_ROOM_ID = new AtomicInteger(0);

    public static GameRoom createRoom(Map<String, String> dataMap, UserEntity user) {
        String roomName = dataMap.get("roomName");
        Long roomId = Long.valueOf(ServerManager.MAX_ROOM_ID.getAndAdd(1));
        GameRoom room = new GameRoom();
        room.setRoomName(roomName);
        room.setRoomId(roomId);
        UserModel player = new UserModel();
        player.setIndex(room.playerId.getAndAdd(1));
        player.setUserId(user.getId());
        player.setUserName(user.getNickname());
        player.setIsLeader(true);
        player.setIsReady(true);
        player.setRoomId(roomId);
        room.getPlayers().add(player);
        ServerManager.ROOM_MAP.put(roomId, room);
        ServerManager.USER_MAP.put(user.getId(),player);
        ChannelUtils.GAME_CHANNEL.sendMessageToAll("addRoom", room);
        ChannelUtils.GAME_CHANNEL.sendMessage(user.getId(), "showRoom", room);
        BaseChannel channel = new BaseChannel();
        channel.addSession(user.getId(),ChannelUtils.GAME_CHANNEL.getSessionByUid(user.getId()));
        ChannelUtils.ROOM_CHANNEL.put(roomId,channel);
        return room;
    }

    public static UserModel joinRoom(Map<String, String> dataMap, UserEntity user) {
        Long roomId = Long.valueOf(dataMap.get("roomId"));
        GameRoom gameRoom = ServerManager.ROOM_MAP.get(roomId);

        UserModel player = new UserModel();
        player.setIndex(gameRoom.playerId.getAndAdd(1));
        player.setUserId(user.getId());
        player.setUserName(user.getNickname());
        player.setRoomId(roomId);
        ServerManager.USER_MAP.put(user.getId(),player);

        ChannelUtils.GAME_CHANNEL.sendMessageToRoom(roomId, "joinRoom", player);
        ChannelUtils.ROOM_CHANNEL.get(roomId).addSession(user.getId(), ChannelUtils.GAME_CHANNEL.getSessionByUid(user.getId()));
        ChannelUtils.GAME_CHANNEL.sendMessage(user.getId(), "showRoom", gameRoom);

        gameRoom.getPlayers().add(player);
        return player;
    }
}
