package com.mxzgh.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/5/5.
 */
public class ChannelUtils {

    public static BaseChannel GAME_CHANNEL = new BaseChannel();

    public static Map<Long,BaseChannel> ROOM_CHANNEL = new ConcurrentHashMap<>();
}
