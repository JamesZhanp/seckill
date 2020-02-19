package com.james.secondkill.controller;

import com.james.secondkill.controller.result.Result;
import com.james.secondkill.service.SeckillUserService;
import com.james.secondkill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 13 15:29
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @RequestMapping("/to_login")
    public String toLogin(){
        //login 页面
        return "login";
    }

    /**
     * 用户登录
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        logger.info(loginVo.toString());

        seckillUserService.login(response, loginVo);
        return Result.success(true);
    }

    /**
     * 生成token
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/create_token")
    @ResponseBody
    public Result<String> createToken(HttpServletResponse response, @Valid LoginVo loginVo){
        logger.info(loginVo.toString());
        String token = seckillUserService.login(response, loginVo);
        return Result.success(token);
    }
}
