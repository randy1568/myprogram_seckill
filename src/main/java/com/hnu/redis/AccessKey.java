package com.hnu.redis;

public class AccessKey extends BasePrefix {

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKey withAccess(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }

}
