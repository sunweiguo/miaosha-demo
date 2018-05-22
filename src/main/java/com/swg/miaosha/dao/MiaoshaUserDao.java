package com.swg.miaosha.dao;

import com.swg.miaosha.model.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoshaUserDao {
	
	@Select("select * from miaosha_user where id = #{id}")
	MiaoshaUser getById(@Param("id") long id);

	@Update("update miaosha_user set nickname = #{nickname} where id = #{id}")
    void update(MiaoshaUser toBeUpdate);
}
