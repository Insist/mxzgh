package com.mxzgh.controller;

import com.mxzgh.entity.TestEntity;
import com.mxzgh.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2015/11/6.
 */
@Controller
@RequestMapping(name="test")
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/index")
    @ResponseBody
    public String index(Long id){
        TestEntity t = testService.get(id);
        return t.getName();
    }

    @RequestMapping("/show")
    public String show(Long id){
        return "/index";
    }

    @RequestMapping("/demo")
    public String demo(Long id){
        return "/demo";
    }

}
