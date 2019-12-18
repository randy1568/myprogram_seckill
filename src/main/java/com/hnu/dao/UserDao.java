package com.hnu.dao;


import com.hnu.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select * from user where id = #{pid}")
    public User getById(@Param("pid") int id);

    @Insert("insert into user(id,name) values(#{id},#{name})")
    public int insert(User user);
}
