package com.james.secondkill.service;

import com.james.secondkill.controller.result.CodeMsg;
import com.james.secondkill.dao.SeckillUserDao;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.exception.GlobalException;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.redis.SeckillUserPrefix;
import com.james.secondkill.util.MD5Util;
import com.james.secondkill.util.UUIDUtil;
import com.james.secondkill.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 秒杀用户业务层逻辑
 * @author: JamesZhan
 * @create: 2020 - 02 - 12 11:34
 */
@Service
public class SeckillUserService {
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    SeckillUserDao seckillUserDao;

    @Autowired
    RedisService redisService;

    /**
     * 用户登录
     * @param response
     * @param loginVo 封装了客户端传递过来的参数信息
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo){
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //获取用户提交的手机号码和密码
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //判断用户手机号是否存在
        SeckillUser user = this.getSeckillUserById(Long.parseLong(mobile));
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //判断手机号对应的密码是否一直
        String dbPassword = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPass = MD5Util.formPassToDbPass(password, dbSalt);
        if (!calcPass.equals(dbPassword)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成cookie
        String token = UUIDUtil.uuid();
        //每次访问都会生成一个新的session存储于redis和反馈给客户端，一个session对应存储一个user对象
        redisService.set(SeckillUserPrefix.token, token, user);
        //将token写入cookie中，然后传递给客户端
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //保持与redis中的session一致
        cookie.setMaxAge(SeckillUserPrefix.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);

        return token;
    }



    private SeckillUser getSeckillUserById(Long id){
        // 1. 从redis中获取用户缓存数据
        SeckillUser user = redisService.get(SeckillUserPrefix.getSekillUserById,"" + id, SeckillUser.class);
        if (user != null){
            return user;
        }

        //2. 如果缓存中没有用户数据，则将数据从数据库中取出，写入缓存
        user = seckillUserDao.getById(id);
        if (user != null){
            redisService.set(SeckillUserPrefix.getSekillUserById, ""+id, user);
        }
        return user;
    }

    /**
     * 根据token取出redis中的SeckillUser值
     * @param response
     * @param token
     * @return
     */
    public SeckillUser getSeckillUserByToken(HttpServletResponse response, String token){
        if (StringUtils.isEmpty(token)){
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserPrefix.token, token, SeckillUser.class);
        // 在有效期内从redis获取到key之后，需要将key重新设置一下从而达到延长有效期的效果
        if (user != null){
            addCookie(response, token, user);
        }
        return user;
    }


    /**
     * 将cookie存入redis， 并将cookie写入到请求的响应中
     * @param response
     * @param token
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, SeckillUser user){
        redisService.set(SeckillUserPrefix.token,token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setPath("/");
        cookie.setMaxAge(SeckillUserPrefix.token.expireSeconds());
        response.addCookie(cookie);
    }


    /**
     * 更新密码
     * 1. 查询该用户是否存在
     * 2. 更新数据库中的密码字段
     * 3. 更新缓存中的相关信息
     * @param token
     * @param id
     * @param updatedPassword
     * @return
     */
    public boolean updatePassword(String token, long id, String updatedPassword){
        // 1. 从缓存或者数据库中取出对应id的用户数据
        SeckillUser user = this.getSeckillUserById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //更新数据库中的数据
        SeckillUser updateUser = new SeckillUser();
        updateUser.setId(id);
        updateUser.setPassword(MD5Util.formPassToDbPass(updatedPassword, user.getSalt()));
        seckillUserDao.updatePassword(updateUser);

        //更新缓存中的数据（先删除在添加）
        redisService.delete(SeckillUserPrefix.getSekillUserById, ""+id);
        user.setPassword(updateUser.getPassword());
        redisService.set(SeckillUserPrefix.token, token, user);
        return true;
    }
}
