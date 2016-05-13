package com.mxzgh.uno;

import java.util.*;

/**
 * Created by Administrator on 2016/5/13.
 */
public class GameBoardPrivate {

    List<UnoCard> yamaCards = Collections.synchronizedList(new ArrayList<UnoCard>());

    List<UnoCard> usedCards = Collections.synchronizedList(new ArrayList<UnoCard>());

    Map<Long,List<UnoCard>> playerHandCard = new HashMap<>();

    public List<UnoCard> getYamaCards() {
        return yamaCards;
    }

    public void setYamaCards(List<UnoCard> yamaCards) {
        this.yamaCards = yamaCards;
    }

    public List<UnoCard> getUsedCards() {
        return usedCards;
    }

    public void setUsedCards(List<UnoCard> usedCards) {
        this.usedCards = usedCards;
    }

    public Map<Long, List<UnoCard>> getPlayerHandCard() {
        return playerHandCard;
    }

    public void setPlayerHandCard(Map<Long, List<UnoCard>> playerHandCard) {
        this.playerHandCard = playerHandCard;
    }
}
