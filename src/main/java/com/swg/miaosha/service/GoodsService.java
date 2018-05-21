package com.swg.miaosha.service;

import com.swg.miaosha.dao.GoodsDao;
import com.swg.miaosha.model.Goods;
import com.swg.miaosha.model.MiaoshaGoods;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> getGoodsVoList(){
        System.out.println("service"+goodsDao.getGoodsVoList());
        return goodsDao.getGoodsVoList();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public void reduceStock(GoodsVo goods) {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        goodsDao.reduceStock(g);
    }
}
