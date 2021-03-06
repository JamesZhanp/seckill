package com.james.secondkill.dao;

import com.james.secondkill.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 22:06
 */
@Mapper
public interface UserDao {
    @Select("select * from user where id=#{id}")
    User getById(@Param("id") int id);

    @Insert("insert into user(id, name) values(#{id},#{name})")
    int insert(User user);
}
