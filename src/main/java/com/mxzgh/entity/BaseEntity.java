package com.mxzgh.entity;

import javax.persistence.Id;

/**
 * Created by Administrator on 2015/11/6.
 */
public class BaseEntity implements java.io.Serializable {
    private String id;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
