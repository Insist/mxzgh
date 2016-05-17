package com.mxzgh.uno;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/5/13.
 */
public class GameBoardPublic {

    CenterUnoCard centerCard = new CenterUnoCard();

    Integer round = 0;

    Integer actionIndex;

    Boolean direction = true;

    Boolean autoAction = false;

    List<PlayerInfo> players = Collections.synchronizedList(new ArrayList<PlayerInfo>());
    public Integer needAddCard = 1;

    public CenterUnoCard getCenterCard() {
        return centerCard;
    }

    public void setCenterCard(CenterUnoCard centerCard) {
        this.centerCard = centerCard;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getActionIndex() {
        return actionIndex;
    }

    public void setActionIndex(Integer actionIndex) {
        this.actionIndex = actionIndex;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
    public PlayerInfo getPlayer(int index) {
        return players.get(index);
    }

    public void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }

    public Boolean getDirection() {
        return direction;
    }

    public void setDirection(Boolean direction) {
        this.direction = direction;
    }
}
