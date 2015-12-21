package com.mxzgh.main;

import com.mxzgh.main.enums.GamePosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/21.
 */
public class Player {
    private PlayerInfo playerInfo;

    private List<Card> handCards;

    public static Player initPlayer(Integer startScore,GamePosition pp) {
        Player player = new Player();
        PlayerInfo pi = new PlayerInfo();
        pi.setPlayerPosition(pp);
        pi.setScore(startScore);
        player.setPlayerInfo(pi);
        player.handCards = new ArrayList<Card>(13);
        return player;
    }


    public void startTurn(Gameboard gameboard) {
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public void addHandCard(Card card) {
        this.handCards.add(card);
    }
}
