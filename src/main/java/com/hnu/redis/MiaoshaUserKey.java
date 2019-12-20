package com.hnu.redis;

public class MiaoshaUserKey extends BasePrefix {

    private static final int TOKEN_EXPIRATION_TIME = 3600*24*2;

    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey getToken = new MiaoshaUserKey(TOKEN_EXPIRATION_TIME,"token");

}
