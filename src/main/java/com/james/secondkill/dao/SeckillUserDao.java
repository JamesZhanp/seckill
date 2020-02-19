package com.james.secondkill.dao;

import com.james.secondkill.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 14:39
 */

@Mapper
public interface SeckillUserDao {

    /**
     * 根据id查询秒杀用户信息
     * @param id
     * @return
     */
    @Select("SELECT * FROM seckill_user WHERE id=#{id}")
    SeckillUser getById(@Param("id") Long id);

    /**
     *
     * @param updatedUser
     */
    @Update("UPDATE seckill_user SET password=#{password} WHERE id=#{id}")
    void updatePassword(SeckillUser updatedUser);
}
