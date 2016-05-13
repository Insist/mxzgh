package com.mxzgh.uno;

import com.mxzgh.main.Card;
import com.mxzgh.util.BaseChannel;
import com.mxzgh.util.ChannelUtils;
import com.mxzgh.util.UnoCardUtils;

import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/5/13.
 */
public class GameBoardManager implements Runnable {

    GameBoardPublic gameBoardPublic = new GameBoardPublic();
    GameBoardPrivate gameBoardPrivate = new GameBoardPrivate();
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    Map<Long,Session> userSessionMap = new HashMap<>();
    List<UserModel> userModelList;
    Long roomId ;
    BaseChannel roomChannel;
    List<Integer> tmpAction = new ArrayList<>();
    Map<Long,Integer> playerIndex = new HashMap<>();

    public GameBoardManager(GameRoom room){
        this.roomId = room.getRoomId();
        this.userModelList = room.getPlayers();
        this.roomChannel = ChannelUtils.ROOM_CHANNEL.get(roomId);
    }

    @Override
    public void run() {
        initGame();
        do{
            runRound();
        }while (checkGameEnd());
//        endGame();
    }

    private boolean checkGameEnd() {
        for(PlayerInfo info:gameBoardPublic.getPlayers()){
            if(info.getScore()>200){
                return false;
            }
        }
        return true;
    }

    private void initGame() {
        Collections.shuffle(userModelList);
        Integer index = 0;
        for(UserModel model:userModelList){
            userSessionMap.put(model.getUserId(), ChannelUtils.GAME_CHANNEL.getSessionByUid(model.getUserId()));
            playerIndex.put(model.getUserId(),index);
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setIndex(index);
            playerInfo.setUserName(model.getUserName());
            playerInfo.setUserId(model.getUserId());
            gameBoardPublic.getPlayers().add(playerInfo);
            gameBoardPrivate.getPlayerHandCard().put(playerInfo.getUserId(),new ArrayList<UnoCard>());
            index++;
        }
        roomChannel.sendMessageToAll("initGame", gameBoardPublic);
    }

    private UnoCard popCard(){
        if(gameBoardPrivate.yamaCards.size()<1){
            Collections.shuffle(gameBoardPrivate.getUsedCards());
            gameBoardPrivate.yamaCards.addAll(gameBoardPrivate.getUsedCards());
            gameBoardPrivate.getUsedCards().clear();
        }
        UnoCard card = gameBoardPrivate.yamaCards.get(0);
        gameBoardPrivate.yamaCards.remove(0);
        return card;
    }

    private void runRound() {
        initRound();
        do{
            runAction();
        }while(roundEnd());
//        endRound();
    }

    private boolean roundEnd() {
        for(PlayerInfo info:gameBoardPublic.getPlayers()){
            if(info.getHandCardNum()<1){
                return false;
            }
        }
        return true;
    }

    private void initRound() {
        gameBoardPrivate.setYamaCards(UnoCardUtils.getNewCards());
        Collections.shuffle(gameBoardPrivate.getYamaCards());
        for(PlayerInfo playerInfo:this.gameBoardPublic.getPlayers()){
            List<UnoCard> handCards = gameBoardPrivate.getPlayerHandCard().get(playerInfo.getUserId());
            for(int i=0;i<7;i++){
                addHandCard(playerInfo.getUserId(),popCard(),handCards);
            }
            roomChannel.sendMessage(playerInfo.getUserId(),"startHandCard",handCards);
            roomChannel.sendMessageToAll("updatePlayerInfo", playerInfo);
        }
        gameBoardPublic.setActionIndex(0);
        gameBoardPublic.setCenterCard(popCard());
        roomChannel.sendMessageToAll("updateCenterCard", gameBoardPublic.getCenterCard());
    }

    private void runAction() {
        Integer actionIndex = gameBoardPublic.getActionIndex();
        PlayerInfo playerInfo = gameBoardPublic.getPlayers().get(actionIndex);
        roomChannel.sendMessage(playerInfo.getUserId(),
                "doAction", getCanActions(actionIndex));
        lock.lock();
        try {
            condition.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return;
        } finally {
            lock.unlock();
        }
        if(actionIndex==gameBoardPublic.getActionIndex()){
            Map<String,String> data = new HashMap<>();
            data.put("action","1");
            doAction(data, playerInfo.getUserId());
        }
    }

    private List<Integer> getCanActions(Integer actionIndex) {
        tmpAction.clear();
        tmpAction.add(1);
        tmpAction.add(2);
        tmpAction.add(3);
        return tmpAction;
    }

    private Integer getNextIndex(){
        if(gameBoardPublic.direction){
            gameBoardPublic.actionIndex++;
        }else{
            gameBoardPublic.actionIndex--;
        }
        if(gameBoardPublic.actionIndex<0){
            gameBoardPublic.actionIndex = userModelList.size()-1;
        }
        if( gameBoardPublic.actionIndex>=userModelList.size()){
            gameBoardPublic.actionIndex=0;
        }
        return gameBoardPublic.actionIndex;
    }

    public void doAction(Map<String,String> data,Long userId){
        String action = data.get("action");
        List<UnoCard> handCard = this.gameBoardPrivate.getPlayerHandCard().get(userId);
        PlayerInfo playerInfo = gameBoardPublic.getPlayer(playerIndex.get(userId));
        switch (Integer.valueOf(action)){
            case 0:
                Integer cardId = Integer.valueOf(data.get("cardId"));
                UnoCard card = UnoCardUtils.getById(cardId);
                gameBoardPrivate.usedCards.add(gameBoardPublic.centerCard);
                gameBoardPublic.centerCard=card;
                handCard.remove(card);
                playerInfo.setHandCardNum(playerInfo.getHandCardNum() - 1);
                this.roomChannel.sendMessageToAll("updatePlayerInfo", gameBoardPublic.getPlayer(playerIndex.get(userId)));
                this.roomChannel.sendMessage(userId,"updateHandCard", handCard);
                break;
            case 1:
                for(int i=0;i<gameBoardPublic.needAddCard;i++){
                    addHandCard(userId,popCard(),handCard);
                }
                gameBoardPublic.needAddCard = 1;
                this.gameBoardPublic.actionIndex = getNextIndex();
                this.roomChannel.sendMessageToAll("updatePlayerInfo", gameBoardPublic.getPlayer(playerIndex.get(userId)));
                this.roomChannel.sendMessage(userId,"updateHandCard", handCard);
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    public void unlock(){
        lock.lock();
        try {
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    private void addHandCard(Long userId,UnoCard addCard,List<UnoCard> handCard){
        PlayerInfo playerInfo = gameBoardPublic.getPlayer(playerIndex.get(userId));
        playerInfo.setHandCardNum(playerInfo.getHandCardNum()+1);
        playerInfo.setIsUno(false);
        handCard.add(popCard());
    }
}
