package com.james.secondkill.service;

import com.james.secondkill.dao.OrderDao;
import com.james.secondkill.domain.OrderInfo;
import com.james.secondkill.domain.SeckillOrder;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.redis.OrderKeyPrefix;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 15:52
 */

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     *
     * 通过用户id与商品id从订单列表中获取信息
     * 在生成订单的时候，将订单信息保存到redis中，再次读取信息只需要通过redis读取就行
     * @param userId
     * @param goodsId
     * @return
     */
    public SeckillOrder getSeckillOrderByUserIdAndGoodsId(Long userId, Long goodsId){
        //从redis中取缓存
        SeckillOrder seckillOrder = redisService.get(OrderKeyPrefix.getSeckillOrderByUidGis, ":"+userId + "_" +goodsId, SeckillOrder.class);

        if  (seckillOrder != null){
            return seckillOrder;
        }

        return orderDao.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
    }

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    public OrderInfo getOrderById(long orderId){
        return orderDao.getOrderById(orderId);
    }


    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVo goodsVo){
        OrderInfo orderInfo = new OrderInfo();
        SeckillOrder seckillOrder = new SeckillOrder();

        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
//        订单商品数量
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        //秒杀价格
        orderInfo.setGoodsPrice(goodsVo.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        // 将订单信息插入order_info表中
        long orderId = orderDao.insert(orderInfo);

        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        // 将秒杀订单插入miaosha_order表中
        orderDao.insertSeckillOrder(seckillOrder);

        // 将秒杀订单信息存储于redis中
        redisService.set(OrderKeyPrefix.getSeckillOrderByUidGis, ":" + user.getId() + "_" + goodsVo.getId(), seckillOrder);

        return orderInfo;

    }


}
