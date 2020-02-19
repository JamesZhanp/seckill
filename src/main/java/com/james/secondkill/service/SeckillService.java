package com.james.secondkill.service;

import com.james.secondkill.domain.OrderInfo;
import com.james.secondkill.domain.SeckillOrder;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.redis.SeckillKeyPrefix;
import com.james.secondkill.util.MD5Util;
import com.james.secondkill.util.UUIDUtil;
import com.james.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 秒杀业务处理
 * @author: JamesZhan
 * @create: 2020 - 02 - 11 15:05
 */

@Service
public class SeckillService {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 用于生成验证码中的运算符
     */
    private char[] ops = new char[]{'+', '-', '*'};

    /**
     * 执行秒杀操作，事务包含以下两部分
     * 1. 从goods表中减少库存
     * 2. 将生成的订单写入秒杀订单表中
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo){

        //1. 减少库存
        boolean success = goodsService.reduceStock(goodsVo);
        //避免出现超卖问题
        if (!success){
            setGoodsOver(goodsVo.getId());
            return null;
        }
        return orderService.createOrder(user,goodsVo);
    }

    /**
     * 获取秒杀成功
     * @param userId
     * @param goodsId
     * @return
     */
    public long getSeckillResult(Long userId, long goodsId){
        SeckillOrder order = orderService.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        if (order!=null){
            //秒杀成功
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }


    /**
     * 创建验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(SeckillUser user, long goodsId){
        if (user == null || goodsId <= 0){
            return null;
        }
        // 验证码的宽高
        int width = 80;
        int height = 32;

        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();

        // 计算表达式值，并把把验证码值存到redis中
        int expResult = calc(verifyCode);
        redisService.set(SeckillKeyPrefix.seckillVerifyCode, user.getId() + "," + goodsId, expResult);
        //输出图片
        return image;
    }

    /**
     * 生成验证码
     * 随机生成三个数字，然后生成表达式
     * @param rdm
     * @return
     */
    private String generateVerifyCode(Random rdm){
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);

        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = "" + num1 + op1 + num2 + op2+ num3;
        return exp;
    }

    /**
     * 使用ScriptEngine计算验证码中的数学表达式
     * @param exp
     * @return
     */
    private int calc( String exp){
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 校验检验码的计算结果
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode){
        if (user == null || goodsId <= 0){
            return false;
        }
        //从redis中获取验证码计算结果
        Integer oldCode = redisService.get(SeckillKeyPrefix.seckillVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (oldCode == null ||oldCode - verifyCode != 0){
            return false;
        }
        //如果校验不成功，则说明校验码已经过期
        redisService.delete(SeckillKeyPrefix.seckillVerifyCode, user.getId() + "," + goodsId);
        return true;
    }

    /**
     * 校验路径是否正确
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(SeckillUser user, long goodsId, String path){
        if (user == null || path == null){
            return false;
        }
        String oldPath = redisService.get(SeckillKeyPrefix.seckillPath, "" + user.getId()+"_"+goodsId, String.class);
        return path.equals(oldPath);
    }

    /**
     * 创建秒杀地址，并将其存储在redis中
     * @param user
     * @param goodsId
     * @return
     */
    public String createSeckillPath(SeckillUser user, long goodsId){
        if (user == null || goodsId < 0){
            return null;
        }

        //随机生成秒杀地址
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(SeckillKeyPrefix.seckillPath, ""+user.getId() + "_" + goodsId, path);
        return path;
    }

    private boolean getGoodsOver(long goodsId){
        return redisService.exists(SeckillKeyPrefix.isGoodsOver, ""+goodsId);
    }


    /**
     * 解决超卖问题
     * @param goodsId
     */
    public void setGoodsOver(long goodsId){
        redisService.set(SeckillKeyPrefix.isGoodsOver, ""+goodsId, true);
    }
}
