package com.james.secondkill.controller;

import com.james.secondkill.controller.result.Result;
import com.james.secondkill.domain.SeckillUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 15:54
 */

@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/user_info")
    @ResponseBody
    public Result<SeckillUser> userInfo(SeckillUser user){
        logger.info(user.toString());
        return Result.success(user);
    }
}
