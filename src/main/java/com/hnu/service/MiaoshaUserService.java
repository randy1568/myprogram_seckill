package com.hnu.service;

import com.hnu.dao.MiaoshaUserDao;
import com.hnu.domain.MiaoshaUser;
import com.hnu.exception.GlobalException;
import com.hnu.redis.MiaoshaUserKey;
import com.hnu.redis.RedisService;
import com.hnu.result.CodeMsg;
import com.hnu.util.MD5Util;
import com.hnu.util.UUIDUtil;
import com.hnu.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public static final String COOKI_TOKEN_NAME = "token" ;

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw  new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        //判断手机号码是否存在
        MiaoshaUser miaoshaUser = miaoshaUserDao.getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw  new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)){
            throw  new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //登陆成功之后
        //生成token
        String token = UUIDUtil.uuid();
        //存到缓存
        redisService.set(MiaoshaUserKey.getToken,token,miaoshaUser);

        //生成对应的Cookie
        addCookie(token,response);
        return true;
    }

    public MiaoshaUser geByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getToken, token, MiaoshaUser.class);
        //这里是为了延长cookie（分布式sesseion）的有效时间，比如，每登陆一次，有效时间就成登陆那刻起，有30分钟
        if(user !=null){
            //得到的cookie有效才延长
            addCookie(token,response);
        }
        return  user;
    }

    public void addCookie(String token,HttpServletResponse response){
        Cookie cookie = new Cookie(COOKI_TOKEN_NAME,token);
        cookie.setMaxAge(MiaoshaUserKey.getToken.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
