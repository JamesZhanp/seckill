package com.james.secondkill.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 20:26
 * 手机号码验证校验工具
 */
public class ValidatorUtil {
    /**
     *   国内手机号码正则表达式
     */
    private static final Pattern mobilePattern = Pattern.compile("1(?:3\\d|4[4-9]|5[0-35-9]|6[67]|7[013-8]|8\\d|9\\d)\\d{8}");

    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = mobilePattern.matcher(mobile);
        return matcher.matches();
    }
}
