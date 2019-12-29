package com.hnu.rabbitMq;

import com.hnu.domain.MiaoshaUser;

public class MiaoshaMessage {
    //哪个miaosha用户
    private MiaoshaUser miaoshaUser;
    //想秒杀哪个商品
    private long goodsId;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
