package com.james.secondkill.util;

import java.util.UUID;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 20:34
 * 用于生成Session
 */
public class UUIDUtil {
    public static  String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
