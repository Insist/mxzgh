package com.mxzgh.uno;

import lombok.Data;

/**
 * Created by Administrator on 2016/5/13.
 */
@Data
public class UnoCard {

    private Integer id;
    private Integer color;
    private Integer number;
    private Integer type;
    public UnoCard(Integer i,Integer color,Integer number,Integer type){
        this.id = i;
        this.color=color;
        this.number=number;
        this.type=type;
    }

}
