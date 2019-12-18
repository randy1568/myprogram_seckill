package com.hnu.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    //此处JedisPool加入IOC依赖Service注解
    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisConfig.getPoolMaxIdle());
        config.setMaxTotal(redisConfig.getPoolMaxTotal());
        config.setMaxWaitMillis(redisConfig.getPoolMaxWait());
        //database指的是redis的哪个数据库源
        //本地redis没设密码
        JedisPool jp = new JedisPool(config,redisConfig.getHost(),redisConfig.getPort(),
                redisConfig.getTimeout()*1000,null,0);
        return jp;
    }
}
