package com.james.secondkill.dao;

import com.james.secondkill.domain.OrderInfo;
import com.james.secondkill.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 13:44
 */
@Mapper
public interface OrderDao {

    /**
     * 通过用户id和商品id返回订单信息
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("SELECT * FROM seckill_order WHERE user_id=#{userId} AND goods_id=#{goodsId}")
    SeckillOrder getSeckillOrderByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") long goodsId);

    /**
     * 将订单信息插入seckill_order表中
     *
     * @param orderInfo
     * @return
     *
     * 查询出插入订单的表id，并返回
     */
    @Insert("INSERT INTO order_info (user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)"
            + "VALUES (#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "SELECT last_insert_id()")
    long insert(OrderInfo orderInfo);


    /**
     * 将秒杀订单信息插入到seckill_order当中
     * @param seckillOrder
     */
    @Insert("INSERT INTO seckill_order(user_id, order_id, goods_id) VALUES (#{userId}, #{orderId}, #{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 使用orderId查询订单的详细信息
     * @param orderId
     * @return
     */
    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
