package com.james.secondkill.rabbitmq;

import com.james.secondkill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MQ消息的发送者
 * @author: JamesZhan
 * @create: 2020 - 02 - 13 14:58
 */

@Service
public class MQSender {
    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message){
        String msg = RedisService.beanToString(message);
        logger.info("MQ send message: " + msg);
        // 队列名，消息
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    /**
     * 将为消息投递到topic exchange上
     *
     * @param message
     */
    public void sendTopic(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("Send topic message: " + msg);
        // 将消息投递到topic exchange
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }


    /**
     * 将消息投递到fanout exchange上
     *
     * @param message
     */
    public void sendFanout(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("Send fanout message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
    }

    /**
     * 将消息投递到header exchange上
     *
     * @param message
     */
    public void sendHeader(Object message) {
        String msg = RedisService.beanToString(message);
        logger.info("Send fanout message:" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1", "value1");
        properties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(), properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
    }

    public void sendSeckillMessage(SeckillMessage message){
        String msg = RedisService.beanToString(message);
        logger.info("MQ send message: " + message);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
    }
}
