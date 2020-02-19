package com.james.secondkill.service;

import com.james.secondkill.dao.GoodsDao;
import com.james.secondkill.domain.SeckillGoods;
import com.james.secondkill.domain.SeckillOrder;
import com.james.secondkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 15:48
 */

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    /**
     * 查出所有的商品信息
     * @return
     */
    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    /**
     * 根据商品id查出商品的详细信息
     * @param goodsId
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(Long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * order减少商品库存
     * @param goodsVo
     * @return
     */
    public boolean reduceStock(GoodsVo goodsVo){
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsVo.getId());
        int res = goodsDao.reduceStock(seckillGoods);
        return res>0;
    }
}
