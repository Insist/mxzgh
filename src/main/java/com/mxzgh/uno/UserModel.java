package com.mxzgh.uno;

import com.mxzgh.entity.UserEntity;
import lombok.Data;

/**
 * Created by Administrator on 2016/5/12.
 */
@Data
public class UserModel {

    Integer index;
    String userName;
    Long userId;
    Boolean isLeader = false;
    Boolean isReady = false;
    Long roomId;

}
