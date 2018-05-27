package com.swg.miaosha.service;

import com.swg.miaosha.dao.MiaoshaUserDao;
import com.swg.miaosha.exception.GlobalException;
import com.swg.miaosha.key.MiaoshaKey;
import com.swg.miaosha.key.MiaoshaUserKey;
import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.MiaoshaUser;
import com.swg.miaosha.model.OrderInfo;
import com.swg.miaosha.redis.RedisService;
import com.swg.miaosha.result.CodeMsg;
import com.swg.miaosha.util.MD5Util;
import com.swg.miaosha.util.UUIDUtil;
import com.swg.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存、下订单、写入秒杀订单
        boolean success =goodsService.reduceStock(goods);
        if(success){
            return orderService.createOrder(user,goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public MiaoshaUser getById(long id){
        //先去缓存取
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user != null){
            return user;
        }
        //缓存没有则去数据库取
        user = miaoshaUserDao.getById(id);
        if(user != null){
            redisService.set(MiaoshaUserKey.getById,""+user.getId(),user);
        }
        return user;
    }

    public boolean updateUsername(String token,long id,String newUsername){
        MiaoshaUser user = getById(id);
        if(user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setNickname(newUsername);
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.del(MiaoshaUserKey.getById,""+id);
        user.setNickname(newUsername);
        redisService.set(MiaoshaUserKey.token,token,user);//token不能直接删除，否则会要求重新登录
        return true;
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder orderInfo = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if(orderInfo != null){
            return orderInfo.getId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                //库存已经没了
                return -1;
            }else{
                //表示还没入库，继续等待结果
                return 0;
            }
        }
    }

    private void setGoodsOver(long goodId){
        redisService.set(MiaoshaKey.isGoodsOver,""+goodId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }

    public boolean check(String path, MiaoshaUser user, long goodsId) {
        if(user == null || path == null || goodsId <= 0){
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath,user.getId()+"_"+goodsId,String.class);
        return path.equals(pathOld);
    }

    public String createPath(Long userId, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,userId+"_"+goodsId,str);
        return str;
    }


    /*图形验证码*/
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
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
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
}
