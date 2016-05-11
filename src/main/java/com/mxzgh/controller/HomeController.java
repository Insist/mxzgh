package com.mxzgh.controller;

import com.mxzgh.entity.TestEntity;
import com.mxzgh.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2015/11/6.
 */
@Controller
@RequestMapping
public class HomeController {

    String baseUrl = "http://localhost:8090/";

    @RequestMapping("/index")
    public String index(Long id){
        return "/index";
    }

    @RequestMapping("/game")
    public String game(Long id,HttpServletRequest req, HttpServletResponse resp){
        req.setAttribute("baseUrl",baseUrl);
        return "/game";
    }

}
