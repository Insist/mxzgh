package com.mxzgh.uno;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
@Data
public class PlayerInfo {
    Integer index;
    String userName;
    Long userId;
    Integer handCardNum = 0;
    Boolean isUno = false;
    Integer score = 0;
}
