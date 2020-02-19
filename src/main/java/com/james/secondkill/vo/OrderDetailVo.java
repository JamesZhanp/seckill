package com.james.secondkill.vo;

import com.james.secondkill.domain.OrderInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 21:57
 */

@Data
public class OrderDetailVo implements Serializable {

    private GoodsVo goods;

    private OrderInfo orderInfo;
}
