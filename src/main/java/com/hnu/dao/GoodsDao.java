package com.hnu.dao;

import com.hnu.domain.miaoshaGoods;
import com.hnu.vo.goodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosh_goods mg left join goods g on mg.goods_id = g.id")
    public List<goodsVo> listGoodsVo();

    @Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosh_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public goodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);


//    这里利用mysql自身带的锁，因为mysql会禁止同时两个请求操作同一个表数据。加了where stock_count>1就能防止出现超卖现象
    @Update("update  miaosh_goods set stock_count = stock_count-1 where goods_id = #{goodsId} where stock_count>1")
    public int ReduceStock(miaoshaGoods goods);
}
