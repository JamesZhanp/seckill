package com.james.secondkill.redis;

/**
 * 秒杀用户的信息前缀
 * @author: JamesZhan
 * @create: 2020 - 02 - 09 20:31
 */
public class SeckillUserPrefix extends BaseKeyPrefix {

    /**
     * 缓存有效时间为两天
     */
    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    public SeckillUserPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public SeckillUserPrefix(String prefix) {
        super(prefix);
    }

    public static SeckillUserPrefix token = new SeckillUserPrefix(TOKEN_EXPIRE, "token");

    /**
     * 用于存储用户对象到redis的key前缀
     */
     public static SeckillUserPrefix getSekillUserById = new SeckillUserPrefix(0, "id");
}
