package com.james.secondkill.dao;

import com.james.secondkill.domain.SeckillGoods;
import com.james.secondkill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 05 20:24
 *
 * goods 表的数据访问层
 */

@Mapper
public interface GoodsDao {


    /**
     * 查出秒杀商品的信息， 左外连接的方式查询
     */
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();


    /**
     * 通过秒杀商品的id，查询所有商品的信息
     * @param goodsId
     * @return
     */
    @Select("SELECT g.*, mg.stock_count, mg.start_date, mg.end_date, mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON mg.goods_id = g.id WHERE g.id=#{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);


    /**
     * 减少seckill_order中的库存
     * @param seckillGoods
     * @return
     */
    @Update("UPDATE seckill_goods SET stock_count=stock_count-1 WHERE goods_id=#{goodsId} AND stock_count > 0")
    int reduceStock(SeckillGoods seckillGoods);

}