package com.hnu.config;

import com.hnu.domain.MiaoshaUser;
import com.hnu.redis.MiaoshaUserKey;
import com.hnu.redis.RedisService;
import com.hnu.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserArgumentResolvers implements HandlerMethodArgumentResolver {

    @Autowired
    RedisService redisService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        return type == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        String paraToken = request.getParameter(MiaoshaUserService.COOKI_TOKEN_NAME);
        String cookieToken = getCookieToken(request, MiaoshaUserService.COOKI_TOKEN_NAME);

        if (StringUtils.isEmpty(paraToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paraToken) ? cookieToken : paraToken;
        return redisService.get(MiaoshaUserKey.getToken, token, MiaoshaUser.class);

    }

    private String getCookieToken(HttpServletRequest request, String cookiTokenName) {
        Cookie[] cookies =  request.getCookies();
        for (Cookie cookie:cookies){
            if(cookie.getName().equals(cookiTokenName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
