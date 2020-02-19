package com.james.secondkill.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 19:28
 * 订单详情列表
 */
@Data
public class OrderInfo {
    private Long id;
    private Long userId;
    private Long goodsId;
    private Long deliveryAddrId;
    private String goodsName;
    private Integer goodsCount;
    private Double goodsPrice;
    private Integer orderChannel;
    /**
     * 订单状态
     */
    private Integer status;
    /**
     * 订单创建时间
     */
    private Date createDate;
    /**
     * 付款时间
     */
    private Date payDate;
}
