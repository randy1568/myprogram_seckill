package com.hnu.access;

import com.hnu.domain.MiaoshaUser;

public class UserContext {


    private static ThreadLocal<MiaoshaUser> userHold = new ThreadLocal<>();

    public static void setUser(MiaoshaUser user){
        userHold.set(user);
    }

    public static MiaoshaUser getUser(){
        return userHold.get();
    }

}
