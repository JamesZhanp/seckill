package com.james.secondkill.redis;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 08 16:04
 */
public class OrderKeyPrefix extends BaseKeyPrefix {
    public OrderKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public OrderKeyPrefix(String prefix) {
        super(prefix);
    }

    /**
     * 秒杀订单的前缀
     */
    public static OrderKeyPrefix getSeckillOrderByUidGis = new OrderKeyPrefix("getSeckillOrderByUidGid");
}
