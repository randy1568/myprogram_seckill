package com.hnu.controller;

import com.hnu.domain.MiaoshaUser;
import com.hnu.redis.RedisService;
import com.hnu.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_list")
    public String list(Model model,MiaoshaUser user){
        model.addAttribute("user",user);
        return "good_list";
    }
}
