package com.james.secondkill.access;

import com.alibaba.fastjson.JSON;
import com.james.secondkill.controller.result.CodeMsg;
import com.james.secondkill.controller.result.Result;
import com.james.secondkill.domain.SeckillUser;
import com.james.secondkill.redis.AccessKeyPrefix;
import com.james.secondkill.redis.RedisService;
import com.james.secondkill.service.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 *
 * 用户接口拦截器， 限制用户对于某个接口的频繁问题
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 14:50
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;


    /**
     * 目标方法执行前的处理
     * 查询访问次数，进行防刷请求拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     *
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 指明拦截的是方法
        if (handler instanceof HandlerMethod){
            SeckillUser user = this.getUser(request, response);
//            保存用户到ThreadLocal， 这样同一个线程访问的是同一个用户
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod)handler;
            //获取AccessLimit方法
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //如果没有添加@AccessLimit注解， 直接发行
            if (accessLimit == null){
                return true;
            }

            //获取注解的元素
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxAccessCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin){
                if (user == null){
                    this.render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }else{

            }

            //设置过期时间
            AccessKeyPrefix accessKeyPrefix = AccessKeyPrefix.withExpire(seconds);
            //在redis中存储的访问次数的key为请求的uri
            Integer count = redisService.get(accessKeyPrefix, key, Integer.class);
            //第一次重复点击秒杀
            if (count == null){
                redisService.set(accessKeyPrefix, key, 1);
            }
            else if (count < maxCount){
                // 自增
                redisService.incr(accessKeyPrefix, key);
            }else{
                this.render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    /**
     * 秒杀按钮有对应的点击次数，当请求过于频繁，向客户端提出提示
     * @param response
     * @param cm
     * @throws Exception
     */
    private void render(HttpServletResponse response, CodeMsg cm) throws Exception{
        response.setContentType("application/json; charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * 用于拦截请求，获取对应的秒杀对象
     * @param request
     * @param response
     * @return
     */
    private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response){
        //从请求当中获取token
        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken:paramToken;
        return userService.getSeckillUserByToken(response, token);
    }

    /**
     * 从众多的cookie中找出指定cookie Name的cookie
     * @param request
     * @param cookieName
     * @return
     */

    private String getCookieValue(HttpServletRequest request, String cookieName){
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0){
            return null;
        }
        for (Cookie cookie: cookies){
            if (cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
