package com.james.secondkill.controller;

import com.james.secondkill.access.AccessLimit;
import com.james.secondkill.controller.result.CodeMsg;
import com.james.secondkill.controller.result.Result;
import com.james.secondkill.domain.OrderInfo;
import com.james.secondkill.domain.SeckillOrder;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.rabbitmq.MQSender;
import com.james.secondkill.rabbitmq.SeckillMessage;
import com.james.secondkill.redis.GoodsKeyPrefix;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.service.GoodsService;
import com.james.secondkill.service.OrderService;
import com.james.secondkill.service.SeckillService;
import com.james.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 13 18:03
 */
@RestController
@RequestMapping("/miaosha")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;


    /**
     *  用于标记内存， 标记库存是否为空，从而减少对于redis的无效访问
     */
     private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 秒杀逻辑， 用户点击秒杀按钮之后的逻辑控制
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
     @RequestMapping("/do_miaosha")
     public String doMiaoSha(Model model, SeckillUser user, @RequestParam("goodsId") long goodsId){
         model.addAttribute("user", user);
         // 1. 如果用户为空，返回登录姐面
         if (user == null){
             return "login";
         }

         // 判断是否还有库存
         GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
         if (goods.getStockCount() <= 0){
             model.addAttribute("errmsg",CodeMsg.SECKILL_OVER);
             return "miaosha_fail";
         }

         //判断用户是否已经完成秒杀
         SeckillOrder order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
         if (order != null){
             model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL);
            return "miaosha_fail";
         }

         //完成秒杀， 减少库存，下订单，写入秒杀订单表
         OrderInfo orderInfo = seckillService.seckill(user, goods);
         model.addAttribute("orderInfo", orderInfo);
         model.addAttribute("goods", goods);
         return "order_detail";
     }

    /**
     * 秒杀逻辑
     * @param model
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
     @RequestMapping(value = "/{path}/do_miaosha_static", method = RequestMethod.POST)
     public Result<Integer> doMiaoshaStatic(Model model, SeckillUser user,
                                            @RequestParam("goodsId") long goodsId,
                                            @PathVariable("path") String path){
         model.addAttribute("user", user);
         if (user == null){
             return Result.error(CodeMsg.SESSION_ERROR);
         }

         boolean check = seckillService.checkPath(user, goodsId, path);
         if (!check){
             return Result.error(CodeMsg.REQUEST_ILLEGAL);
         }
         //通过内存标记， 减少对于redis的访问，秒杀未结束才能继续
         boolean over = localOverMap.get(goodsId);
         if (over){
             return Result.error(CodeMsg.SECKILL_OVER);
         }

         //预减库存
         Long stock = redisService.decr(GoodsKeyPrefix.seckillGoodsStockPrefix,""+goodsId);
         if (stock < 0){
             //秒杀结束。
             localOverMap.put(goodsId, true);

             return Result.error(CodeMsg.SECKILL_OVER);
         }

         //判断是否重复秒杀
         SeckillOrder order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);
         if (order != null){
             return Result.error(CodeMsg.REPEATE_SECKILL);
         }
         //商品有库存切用户为秒杀用户
         SeckillMessage message = new SeckillMessage();
         message.setUser(user);
         message.setGoodsId(goodsId);

         mqSender.sendSeckillMessage(message);
         //排队中
         return Result.success(0);
     }

    /**
     * 秒杀结果
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
     public Result<Long> misoshaResult(Model model, SeckillUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", user);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
     }

    /**
     * 获取秒杀接口地址
     * @param model
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds = 5, maxAccessCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    public Result<String> getMiaoshaPath(Model model, SeckillUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode){
        //执行下面的逻辑之前，会先对path请求进行拦截啊处理
        model.addAttribute("user", user);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //检验 校验码
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //获取秒杀路径
        String path = seckillService.createSeckillPath(user, goodsId);

        //向客户端回传秒杀地址
        return Result.success(path);
    }

     /**
     * 使用HttpServletResponse的输出流返回客户端异步获取验证码
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
     public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, SeckillUser user,
                                                @RequestParam("goodsId") long goodsId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证码
        try{
            BufferedImage image = seckillService.createVerifyCode(user, goodsId);
            ServletOutputStream out = response.getOutputStream();
            //将图片写入到response对象中
            ImageIO.write(image, "JPEG", out);
            out.close();
            out.flush();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }

     }

    /**
     * 系统初始化的时候执行
     * 系统初始化的时候从数据库中将商品信息查询出来
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        List<GoodsVo> goods = goodsService.listGoodsVo();
        if (goods == null){
            return;
        }

        //将商品库存信息存储到redis当中
        for (GoodsVo goodsVo : goods){
            redisService.set(GoodsKeyPrefix.seckillGoodsStockPrefix, ""+goodsVo.getId(), goodsVo.getStockCount());
            // 系统启动时，库存标记不为空
            localOverMap.put(goodsVo.getId(), false);
        }
    }
}
