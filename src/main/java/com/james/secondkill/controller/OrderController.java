package com.james.secondkill.controller;

import com.james.secondkill.controller.result.CodeMsg;
import com.james.secondkill.controller.result.Result;
import com.james.secondkill.domain.OrderInfo;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.service.GoodsService;
import com.james.secondkill.service.OrderService;
import com.james.secondkill.vo.GoodsVo;
import com.james.secondkill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 13 17:56
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    /**
     * 获取订单详情
     * @param model
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    public Result<OrderDetailVo> orderInfo(Model model, SeckillUser user, @RequestParam("orderId") long orderId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }


        //获取订单信息
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        long goosId = order.getId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goosId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrderInfo(order);
        orderDetailVo.setGoods(goodsVo);
        return Result.success(orderDetailVo);
    }
}
