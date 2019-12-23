package com.hnu.service;

import com.hnu.dao.OrderDao;
import com.hnu.domain.MiaoshaOrder;
import com.hnu.domain.MiaoshaUser;
import com.hnu.domain.OrderInfo;
import com.hnu.redis.OrderKey;
import com.hnu.redis.RedisService;
import com.hnu.vo.goodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    //这里做小的优化，不直接访问数据库，而是访问缓存，这样可以减少一层数据库的访问
    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(Long userId, long goodsId) {

//        return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId,goodsId);
       return   redisService.get(OrderKey.getMiaoshaOrderByUserIdAndGoodsId,""+userId+goodsId,MiaoshaOrder.class);

    }

    @Transactional
    public OrderInfo CreateMiaoshaOrder(MiaoshaUser user, goodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setStatus(0); //'订单状态:0,新建未支付,1已支付,2已发货,3已收货,4已退款,5,已完成',
        orderInfo.setOrderChannel(1);
        long orderId =   orderDao.insert(orderInfo);


        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOreder(miaoshaOrder);

        //对应上边的redis里边的 get
        redisService.set(OrderKey.getMiaoshaOrderByUserIdAndGoodsId,""+user.getId()+goods.getId(),miaoshaOrder);
        return orderInfo;
    }
}
