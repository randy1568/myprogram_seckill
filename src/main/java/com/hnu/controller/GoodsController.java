package com.hnu.controller;

import com.hnu.domain.MiaoshaUser;
import com.hnu.redis.RedisService;
import com.hnu.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    /**通过cookie从缓存里得到miaoshaUser，这样写不雅观，改成下面那种方式
    @RequestMapping("/to_list")
    public String list(Model model,HttpServletResponse response,
                       @CookieValue(value =MiaoshaUserService.COOKI_TOKEN_NAME,required = false) String cookieToken,
                       @RequestParam(value = MiaoshaUserService.COOKI_TOKEN_NAME,required = false) String paraToken) {

        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paraToken)){
            //返回登陆页面
            return "login";
        }
        String Token = StringUtils.isEmpty(cookieToken) ? paraToken:cookieToken;
        MiaoshaUser user =miaoshaUserService.geByToken(response,Token);
        model.addAttribute("user",user);
        if(user!=null){
            System.out.println(user.getNickname());
        }
        return "goods_list";
    }
     **/
    /**
     * 根据springmvc怎么把Model这些参数赋实例值的原理，给user赋值
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/to_list")
    public String list(Model model,MiaoshaUser user){
        model.addAttribute("user",user);
        if(user!=null){
            System.out.println(user.getNickname());
        }
        return "goods_list";
    }
}
