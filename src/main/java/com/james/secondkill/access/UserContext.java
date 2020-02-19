package com.james.secondkill.access;

import com.james.secondkill.domain.SeckillUser;

/**
 * 用于保护用户
 * 使用ThreadLocal保存用户，因为ThreadLocal是线程安全的，每个用户对应一个线程
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 14:43
 */
public class UserContext {

    private static ThreadLocal<SeckillUser> userThreadLocal = new ThreadLocal<>();

    public static void setUser(SeckillUser user){
        userThreadLocal.set(user);
    }

    public static SeckillUser getUser(){
        return userThreadLocal.get();
    }
}
