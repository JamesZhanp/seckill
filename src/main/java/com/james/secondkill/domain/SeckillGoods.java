package com.james.secondkill.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 19:32
 */

@Data
public class SeckillGoods {
    private Long id;
    private Long goodsId;
    private Double seckillPrice;
    /**
     * 库存数量
     */
    private Integer stockCount;
    /**
     * 开始结束时间
     */
    private Date startDate;
    private Date endDate;
}
