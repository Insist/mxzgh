package com.mxzgh.entity;

import javax.persistence.*;

/**
 * Created by Administrator on 2015/11/6.
 */
public class TestModel implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6980093847795726310L;
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
