package com.hnu.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {
    @Autowired
    JedisPool jp;


    public  <T> T get(String key,Class<T> tClass){
        Jedis jedis = null;
        try {
            jedis  = jp.getResource();
            String str = jedis.get(key);
            T t = stringToBean(key,tClass);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }
    public <T> boolean set(String key,T value){
        Jedis jedis = null;
        try {
            jedis  = jp.getResource();
            String str_value = beanToString(value);
            if(key == null){
                return false;
            }
            jedis.set(key,str_value);
            return true;

        } finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {
        if(value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == Long.class || clazz == long.class){
            return ""+value;
        }else {
            //bean对象
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str,Class<T> clazz) {
        if(str == null || str.length() == 0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class){
            return (T)str;
        }else if(clazz == Long.class || clazz == long.class){
            return (T) Long.valueOf(str);
        }else {
            //bean对象
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis!=null){
            //返回数据库连接池
            jedis.close();
        }
    }


}
