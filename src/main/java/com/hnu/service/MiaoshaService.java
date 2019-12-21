package com.hnu.service;


import com.hnu.domain.MiaoshaUser;
import com.hnu.domain.OrderInfo;
import com.hnu.vo.goodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaService {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    public  OrderInfo miaosha(MiaoshaUser user, goodsVo goods) {

        //减库存
        goodsService.ReduceStock(goods);

        //创建秒杀订单
        return orderService.CreateMiaoshaOrder(user,goods);
    }
}
