package com.james.secondkill.vo;

import com.james.secondkill.domain.Goods;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 19:56
 *
 * 商品信息
 * 商品信息和商品的秒杀信息是存储在两个不同的数据表
 * 集成Goods 类就具有了goods中的所有字段
 */

public class GoodsVo extends Goods implements Serializable {

    private Double seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;


    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
