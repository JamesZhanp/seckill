package com.james.secondkill.redis;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 08 15:59
 */
public class AccessKeyPrefix extends BaseKeyPrefix {
    public AccessKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public AccessKeyPrefix(String prefix) {
        super(prefix);
    }

    /**
     * 灵活设置过期时间
     * */
    public static AccessKeyPrefix withExpire(int expireSeconds){
        return new AccessKeyPrefix(expireSeconds, "access");
    }

}
