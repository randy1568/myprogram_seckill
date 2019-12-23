package com.hnu.redis;

public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUserIdAndGoodsId = new OrderKey("orderkey");
}
