package com.hnu.rabbitMq;

import com.hnu.domain.MiaoshaOrder;
import com.hnu.domain.MiaoshaUser;
import com.hnu.domain.goods;
import com.hnu.redis.RedisService;
import com.hnu.service.GoodsService;
import com.hnu.service.MiaoshaService;
import com.hnu.service.MiaoshaUserService;
import com.hnu.service.OrderService;
import com.hnu.vo.goodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQconfig.Queue_Name)
    public void  Receive(String message){
        logger.info("msg receive: "+message);
        MiaoshaMessage msg = redisService.stringToBean(message, MiaoshaMessage.class);
        long goodsId = msg.getGoodsId();
        MiaoshaUser miaoshaUser = msg.getMiaoshaUser();

        //到这一步，其实只有很少的请求能进来
        //判断是否还有库存
        goodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stock = goods.getStockCount();
        if(stock <=0){
            return ;
        }
        //判断是否重复秒杀，不加没关系，因为miaoshaorder id字段加了unique
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(miaoshaUser.getId(), goodsId);
        if(order!=null){
            return;
        }
        miaoshaService.miaosha(miaoshaUser,goods);
    }
}
