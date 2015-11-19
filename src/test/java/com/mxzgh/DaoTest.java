package com.mxzgh;

import com.mxzgh.entity.TestEntity;
import com.mxzgh.service.TestService;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;

import java.util.Objects;

/**
 * 创建时间：2015-2-6 下午3:31:07
 *
 * @author andy
 * @version 2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
        "classpath:spring-hibernate.xml" })
public class DaoTest {

    private static final Logger LOGGER = Logger
            .getLogger(DaoTest.class);

    @Autowired
    private TestService testService;

    @Test
    public void save() {
        TestEntity test = new TestEntity();
        test.setId(1L);
        test.setName("123456");
        Object id = testService.save(test);
        LOGGER.info(JSON.toJSONString(id));
    }


}