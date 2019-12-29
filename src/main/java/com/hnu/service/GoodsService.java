package com.hnu.service;


import com.hnu.dao.GoodsDao;
import com.hnu.domain.miaoshaGoods;
import com.hnu.vo.goodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<goodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public goodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    //减秒杀商品表的库存
    public boolean ReduceStock(goodsVo goods) {
        miaoshaGoods miaoshaGoods = new miaoshaGoods();
        miaoshaGoods.setGoodsId(goods.getId());
        int res = goodsDao.ReduceStock(miaoshaGoods);
        return res>0;
    }
}
