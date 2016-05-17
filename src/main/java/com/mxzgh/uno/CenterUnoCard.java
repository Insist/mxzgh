package com.mxzgh.uno;

import lombok.Data;

/**
 * Created by Administrator on 2016/5/13.
 */
@Data
public class CenterUnoCard extends UnoCard {

    private Integer tmpColor;
    private Integer tmpNumber;
    private Boolean isFirst= true;

    public void setCard(UnoCard card){
        this.setId(card.getId());
        this.setColor(card.getColor());
        this.setNumber(card.getNumber());
        this.setType(card.getType());
        this.setTmpColor(card.getColor());
        this.setTmpNumber(card.getNumber());
        this.isFirst = true;
    }

}
