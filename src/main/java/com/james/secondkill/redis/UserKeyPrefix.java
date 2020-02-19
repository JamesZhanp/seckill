package com.james.secondkill.redis;

/**
 * redis 当中用于管理用户表的key
 * @author: JamesZhan
 * @create: 2020 - 02 - 09 21:25
 */
public class UserKeyPrefix extends BaseKeyPrefix{
    public UserKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public UserKeyPrefix(String prefix) {
        super(prefix);
    }

    public static UserKeyPrefix getById = new UserKeyPrefix("id");

    public static UserKeyPrefix getByName = new UserKeyPrefix("name");

}
