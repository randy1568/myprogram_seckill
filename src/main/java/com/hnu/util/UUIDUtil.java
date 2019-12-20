package com.hnu.util;

import java.util.UUID;

public class UUIDUtil {

    public static String uuid(){
        //原生的UUID里边是带横杠的，这里作为token值
        return UUID.randomUUID().toString().replace("-","");
    }

    public static void main(String[] args) {
        //fae4f31e-2b2a-4740-bd81-50a50e0796ce
        System.out.println(uuid());
    }
}
