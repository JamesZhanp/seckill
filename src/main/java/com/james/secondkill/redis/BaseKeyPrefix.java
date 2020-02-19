package com.james.secondkill.redis;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 19:22
 */
public abstract class BaseKeyPrefix implements KeyPrefix{
    /**
     *     过期时间
      */
    int expireSeconds;

    String prefix;

    public BaseKeyPrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BaseKeyPrefix(String prefix){
        this(0, prefix);
    }

    /**
     * 默认0代表永不过期
     */

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName + ":" + prefix;
    }
}
