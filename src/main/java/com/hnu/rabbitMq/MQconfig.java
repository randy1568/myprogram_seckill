package com.hnu.rabbitMq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQconfig {

    public static final String  Queue_Name = "seckill_queue";

    @Bean
    public Queue getQueue() {
        return new Queue(Queue_Name,true);
    }
}
