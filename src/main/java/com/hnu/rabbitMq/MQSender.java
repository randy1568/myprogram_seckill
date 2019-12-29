package com.hnu.rabbitMq;

import com.hnu.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    RedisService redisService;

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    public void send(Object message) {
        String msg = redisService.beanToString(message);
        //这种是direct的方式
        logger.info("msg send: "+msg);
        amqpTemplate.convertAndSend(MQconfig.Queue_Name, msg);
    }
}
