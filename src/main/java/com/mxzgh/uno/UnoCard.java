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
    public UnoCard(){

    }
    public UnoCard(Integer i,Integer color,Integer number,Integer type){
        this.id = i;
        this.color=color;
        this.number=number;
        this.type=type;
    }
    public Integer score(){
        if(type==1){
            return this.number;
        }else if(type==2){
            return 10;
        }else if(type==3){
            if(number==13){
                return 20;
            }else if(number==14){
                return 50;
            }
        }
        return 0;
    }

}
