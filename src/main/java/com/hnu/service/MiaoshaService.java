package com.hnu.service;


import com.hnu.domain.MiaoshaOrder;
import com.hnu.domain.MiaoshaUser;
import com.hnu.domain.OrderInfo;
import com.hnu.redis.MiaoshaKey;
import com.hnu.redis.RedisService;
import com.hnu.util.MD5Util;
import com.hnu.util.UUIDUtil;
import com.hnu.vo.goodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {


    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, goodsVo goods) {
        //减库存
        boolean success = goodsService.ReduceStock(goods);
        if (success) {
            //创建秒杀订单
            return orderService.CreateMiaoshaOrder(user, goods);
        } else {
            //设置秒杀已经结束，没库存了
            setGoodsOver(goods.getId());
            //减库存失败，直接返回null
            return null;
        }
    }


    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else if (getGoodsOver(goodsId)) {
            return -1;
        } else {
            return 0;
        }
    }

    //
    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.goodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.goodsOver, "" + goodsId);
    }

    //生成唯一的path
    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, str);
        return str;
    }

    //检查URL传过来的path
    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user == null || StringUtils.isEmpty(path)){
            return false;
        }
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, String.class);
        return path.equals(oldPath);
    }
}
