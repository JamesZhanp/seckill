package com.james.secondkill.redis;

/**
 * redis 键的前缀
 * 定义接口为了给模板方法提供一套规范
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 19:16
 */
public interface KeyPrefix {

    /**
     * key 的过期时间
     * @return
     */
    int expireSeconds();

    /**
     * key的前缀
     */

    String getPrefix();
}
