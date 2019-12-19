package com.hnu.controller;

import com.hnu.domain.MiaoshaUser;
import com.hnu.redis.UserKey;
import com.hnu.result.CodeMsg;
import com.hnu.result.Result;
import com.hnu.service.MiaoshaUserService;
import com.hnu.util.ValidatorUtil;
import com.hnu.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    MiaoshaUserService miaoshaUserService;


    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(LoginVo loginVo){
        logger.info(loginVo.toString());
        //参数校验
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if(StringUtils.isEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(StringUtils.isEmpty(password)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        //判断手机号的格式
        if(!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }
        //登陆
        CodeMsg msg = miaoshaUserService.login(loginVo);
        if(msg.getCode() == 0){
            System.out.println("success");
            return Result.success(msg.getMsg());
        }else {
            return Result.error(msg);
        }
    }

}
