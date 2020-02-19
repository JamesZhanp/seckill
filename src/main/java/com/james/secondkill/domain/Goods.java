package com.james.secondkill.domain;

import lombok.Data;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 19:13
 * 商品
 */

@Data
public class Goods {

    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetails;
    private Double goodsPrice;
    private Long goodsStock;
}
