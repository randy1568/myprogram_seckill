package com.hnu.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executors;

@Service
public class RedisService {
    @Autowired
    JedisPool jp;

    public <T> T get(KeyPrefix prefix, String key, Class<T> tClass) {
        Jedis jedis = null;
        try {
            jedis = jp.getResource();
            String real_key = prefix.getPrefix()+key;
            String str = jedis.get(real_key);
            T t = stringToBean(str, tClass);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jp.getResource();
            String str_value = beanToString(value);
            if (key == null) {
                return false;
            }
            String real_key = prefix.getPrefix()+key;
            if(prefix.expireSeconds() <=0){
                //永不过期
                jedis.set(real_key, str_value);
            }else {
                jedis.setex(real_key,prefix.expireSeconds(),str_value);
            }
            return true;

        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean exists(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            jedis = jp.getResource();
            String real_key = keyPrefix.getPrefix()+key;
            return jedis.exists(real_key);
        } finally {
            jedis.close();
        }
    }


    /**
     *s删除键值对
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            jedis = jp.getResource();
            String real_key = keyPrefix.getPrefix()+key;
            Long del = jedis.del(real_key);
            return del > 0;
        } finally {
            jedis.close();
        }
    }
    /**
     * 增加键值
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long incr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            jedis = jp.getResource();
            String real_key = keyPrefix.getPrefix() +key;
             return jedis.incr(real_key);
        } finally {
            jedis.close();
        }
    }

    /**
     * 删除键值
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long decr(KeyPrefix keyPrefix,String key){
        Jedis jedis = null;
        try {
            jedis = jp.getResource();
            String real_key = keyPrefix.getPrefix() +key;
            return jedis.decr(real_key);
        } finally {
            jedis.close();
        }
    }

    public   <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == Long.class || clazz == long.class) {
            return "" + value;
        } else {
            //bean对象
            return JSON.toJSONString(value);
        }
    }

    public    <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() == 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(str);
        } else {
            //bean对象
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            //返回数据库连接池
            jedis.close();
        }
    }


}
