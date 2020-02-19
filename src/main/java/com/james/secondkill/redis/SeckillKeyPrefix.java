package com.james.secondkill.redis;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 08 16:21
 */
public class SeckillKeyPrefix extends BaseKeyPrefix {
    public SeckillKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public SeckillKeyPrefix(String prefix) {
        super(prefix);
    }

    public static SeckillKeyPrefix isGoodsOver = new SeckillKeyPrefix("isGoodsOver");
    public static SeckillKeyPrefix seckillPath = new SeckillKeyPrefix(60, "seckillPath");
    /**
     *  验证码5分钟有效
     * */
    public static SeckillKeyPrefix seckillVerifyCode = new SeckillKeyPrefix(300, "seckillVerifyCode");

}
