package com.james.secondkill.rabbitmq;

import com.james.secondkill.domain.SeckillOrder;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.service.GoodsService;
import com.james.secondkill.service.OrderService;
import com.james.secondkill.service.SeckillService;
import com.james.secondkill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MQ消息接收者
 * 消费者绑定在队列监听，既可以接收到队列中的消息
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 18:17
 */

@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        logger.info("MQ: message: " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        logger.info("topic queue1 message: " + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){
        logger.info("topic queue2 message: " + message);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiveHeaderQueue(byte[] message){
        logger.info("header queue message: " + new String(message));
    }

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveSeckillInfo(String message){
        logger.info("MQ: message: " + message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);

        //获取秒杀用户的信息与商品id
        SeckillUser user = seckillMessage.getUser();
        long goodsId = seckillMessage.getGoodsId();

        //获取商品的库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goodsVo.getStockCount();
        if (stockCount <= 0){
            return ;
        }

        //判断是否秒杀到
        SeckillOrder order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (order == null){
            return ;
        }

        //减少库存，下订单，写入秒杀订单
        seckillService.seckill(user, goodsVo);
    }
}
