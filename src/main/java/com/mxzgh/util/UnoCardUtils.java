package com.mxzgh.util;

import com.mxzgh.uno.UnoCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/13.
 */
public class UnoCardUtils {

    private static final List<UnoCard> totalCards = new ArrayList<>();
    private static final Map<Integer,UnoCard> cardMap = new HashMap<>();
    private static int cardId = 0;
    static {
        UnoCard card;
        for(int i=1;i<5;i++){
            card = new UnoCard(getCardId(),i,0,1);
            totalCards.add(card);
            for(int j =1;j<10;j++){
                card = new UnoCard(getCardId(),i,j,1);
                totalCards.add(card);
                card = new UnoCard(getCardId(),i,j,1);
                totalCards.add(card);
            }
            for(int j =10;j<13;j++){
                card = new UnoCard(getCardId(),i,j,2);
                totalCards.add(card);
                card = new UnoCard(getCardId(),i,j,2);
                totalCards.add(card);
            }
        }
        for(int i=0;i<4;i++){
            card = new UnoCard(getCardId(),0,13,3);
            totalCards.add(card);
            card = new UnoCard(getCardId(),0,14,3);
            totalCards.add(card);
        }
        for(UnoCard tmp:totalCards){
            cardMap.put(tmp.getId(),tmp);
        }
    }

    public static void main(String[] args) {
        System.out.println(totalCards);
    }

    private static Integer getCardId(){
        return cardId++;
    }


    public static List<UnoCard> getNewCards(){
        return new ArrayList<>(totalCards);
    }

    public static UnoCard getById(Integer cardId) {
        return cardMap.get(cardId);
    }
}
