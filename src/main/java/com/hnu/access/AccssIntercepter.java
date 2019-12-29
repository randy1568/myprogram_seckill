package com.hnu.access;


import com.alibaba.fastjson.JSON;
import com.hnu.domain.MiaoshaUser;
import com.hnu.redis.AccessKey;
import com.hnu.redis.RedisService;
import com.hnu.result.CodeMsg;
import com.hnu.result.Result;
import com.hnu.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

//定义拦截器
@Service
public class AccssIntercepter extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    //方法执行之前执行下边方法,且该方法会在UserArgumentResolvers之前运行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {

            MiaoshaUser miaoshaUser = getUser(request, response);
            UserContext.setUser(miaoshaUser);

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            String key = request.getRequestURI();
            boolean needLogin = accessLimit.needLogin();
            if (needLogin) {
                if (miaoshaUser == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + miaoshaUser.getId();
            } else {
                //do nothing
            }
            AccessKey ak = AccessKey.withAccess(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count < 5) {
                redisService.incr(ak, key);
            } else {
                render(response, CodeMsg.REQUEST_LIMIT);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        OutputStream write = response.getOutputStream();
        String s = JSON.toJSONString(Result.error(codeMsg));
        write.write(s.getBytes("UTF-8"));
        write.flush();
        write.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String paraToken = request.getParameter(MiaoshaUserService.COOKI_TOKEN_NAME);
        String cookieToken = getCookieToken(request, MiaoshaUserService.COOKI_TOKEN_NAME);

        if (StringUtils.isEmpty(paraToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paraToken) ? cookieToken : paraToken;
        //此处将更新token在redis里边的过期时间
        return miaoshaUserService.geByToken(response, token);

    }

    private String getCookieToken(HttpServletRequest request, String cookiTokenName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiTokenName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
