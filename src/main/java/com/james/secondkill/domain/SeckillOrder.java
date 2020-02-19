package com.james.secondkill.domain;

import lombok.Data;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 19:33
 * 秒杀订单
 */
@Data
public class SeckillOrder {

    private Long id;
    private Long userId;
    private Long orderId;
    private Long goodsId;
}
