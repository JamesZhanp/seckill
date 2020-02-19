package com.james.secondkill.vo;

import com.james.secondkill.domain.SeckillUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 21:51
 *
 * 商品详情
 * 用于将数据传递给客户端
 */

@Data
public class GoodsDetailVo implements Serializable {

    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private SeckillUser user;
}
