package com.james.secondkill.controller;

import com.james.secondkill.controller.result.Result;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.redis.GoodsKeyPrefix;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.service.GoodsService;
import com.james.secondkill.service.SeckillUserService;
import com.james.secondkill.vo.GoodsDetailVo;
import com.james.secondkill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 13 15:52
 */
@Controller
@RequestMapping("/goods")
public class GoodListController {

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    /**
     *     因为在redis缓存中不存页面缓存时需要手动渲染，所以注入一个视图解析器，自定义渲染（默认是由SpringBoot完成的）
     */
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 获取秒杀对象， 并将其传递到页面解析
     * @param request
     * @param response
     * @param model
     * @param seckillUser 通过自定义参数解析器
     * @return
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
                         SeckillUser seckillUser){
        model.addAttribute("user", seckillUser);

        //1. 从redis缓存中取出html
        String html = redisService.get(GoodsKeyPrefix.goodsListKeyPrefix, "", String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        //2. 如果redis中不存在该缓存，手动渲染
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsVoList);

        //3. 渲染html
        WebContext webContext = new WebContext(request, response,request.getServletContext(),request.getLocale(), model.asMap());
        // 第一个参数为渲染的html的文件名称，第二个为web上下文，里面封装了web应用的上下文
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.goodsListKeyPrefix, "", "html");
        }
        return html;
    }

    /**
     * 处理商品详情页
     * URL级缓存： 从redis中取商品详情页面，如果没有则需要手动渲染页面，并将页面缓存到redis中
     * @param request
     * @param response
     * @param model
     * @param user 用户信息
     * @param goodsId 商品id
     * @return
     */
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            SeckillUser user,
            @PathVariable("goodsId") long goodsId){
        String html  = redisService.get(GoodsKeyPrefix.goodsDetailKeyPrefix,""+goodsId, String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        //获取商品的开始结束时间
        long startDate = goodsVo.getStartDate().getTime();
        long endDate = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态 0：未开始， 1：正在进行，2： 结束
        int seckillStatus = 0;

        //据时间的间距
        int remainSeconds = 0;
        if (now < startDate){
            //秒杀还未开始
            seckillStatus = 0;
            remainSeconds = (int)((startDate - now)/1000);
        }else if (now > endDate){
            //秒杀结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else{
            seckillStatus = 1;
            remainSeconds = 0;
        }
         // 将秒杀状态和剩余时间传递给页面
        model.addAttribute("miaoshaStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        // 渲染html
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);

        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.goodsDetailKeyPrefix, ""+goodsId, html);
        }
        return html;
    }

    /**
     * * 处理商品详情页（页面静态化处理, 直接将数据返回给客户端，交给客户端处理）
     * <p>
     * c5: URL级缓存实现；从redis中取商品详情页面，如果没有则需要手动渲染页面，并且将渲染的页面存储在redis中供下一次访问时获取
     * 实际上URL级缓存和页面级缓存是一样的，只不过URL级缓存会根据url的参数从redis中取不同的数据
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/to_detail_static/{goodsId}")// 注意这种写法
    @ResponseBody
    public Result<GoodsDetailVo> toDetailStatic(SeckillUser user, @PathVariable("goodsId") long goodsId) {

        // 通过商品id再数据库查询
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        // 获取商品的秒杀开始与结束的时间
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        // 秒杀状态; 0: 秒杀未开始，1: 秒杀进行中，2: 秒杀已结束
        int miaoshaStatus = 0;
        // 秒杀剩余时间
        int remainSeconds = 0;

        if (now < startDate) { // 秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int) ((startDate - now) / 1000);
        } else if (now > endDate) { // 秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else { // 秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        // 服务端封装商品数据直接传递给客户端，而不用渲染页面
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setUser(user);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(miaoshaStatus);

        return Result.success(goodsDetailVo);
    }
}
