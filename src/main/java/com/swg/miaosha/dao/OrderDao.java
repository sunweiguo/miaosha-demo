package com.swg.miaosha.dao;

import com.swg.miaosha.model.MiaoshaOrder;
import com.swg.miaosha.model.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userId,@Param("goodsId") long goodsId) ;

    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_price,goods_count,order_channel,status,create_date) " +
            "values(#{userId},#{goodsId},#{goodsName},#{goodsPrice},#{goodsCount},#{orderChannel},#{status},#{" +
            "createDate})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order(user_id,goods_id,order_id)values(#{userId},#{goodsId},#{orderId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo getOrderById(long orderId);
}
