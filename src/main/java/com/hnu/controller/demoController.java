package com.hnu.controller;

import com.hnu.redis.RedisService;
import com.hnu.result.CodeMsg;
import com.hnu.result.Result;
import com.hnu.domain.User;
import com.hnu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class demoController {


    @RequestMapping("/")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "yuan");
        return "hello";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> success(){
        return new Result<String>("hello,seckill");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> error(){
        return new Result<>(CodeMsg.SERVER_ERROR);
    }



    @Autowired
    UserService userService;

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbtx(){
        boolean tx = userService.tx();
        return Result.success(tx);
    }

    @Autowired
    RedisService redisService;

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        boolean answer = redisService.set("key1", "hello,redis");
        return Result.success(answer);
    }



}
