package com.james.secondkill.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 19:24
 * 参与秒杀的用户列表
 */

@Data
public class SeckillUser {
    private Long id;
    private String nikeName;
    private String password;
    private String salt;
    /**
     * 用户头像
     */
    private String head;
    /**
     * 注册时间
     */
    private Date registerDate;
    /**
     * 上次登录的时间
     */
    private Date lastLoginDate;
    /**
     * 登录的次数
     */
    private Integer loginCount;
}
