package com.swg.miaosha.dao.mapper;

import com.swg.miaosha.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from t_user")
    public User getUser();

    @Insert("insert into t_user(id,name) values(#{id},#{name})")
    public void insertUser(@Param("id") int id, @Param("name") String name);
}
