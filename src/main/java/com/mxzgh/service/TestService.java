package com.mxzgh.service;

import com.mxzgh.entity.TestEntity;

/**
 * Created by Administrator on 2015/11/6.
 */
public interface TestService {

    public Object save(TestEntity test);

    TestEntity get(Long id);
}
