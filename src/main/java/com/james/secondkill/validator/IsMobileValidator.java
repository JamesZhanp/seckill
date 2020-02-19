package com.james.secondkill.validator;

import com.james.secondkill.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 20:06
 *
 * 真正的手机号码校验的工具，会被注解@IsMobile所使用
 *
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    /**
     * 用于获取校验字段是否可以为空
     */
    private boolean required = false;

    /**
     * 用于获取注解
     * @param constraintAnnotation
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    /**
     * 用于检验字段是否合法
     * @param s
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidatorUtil.isMobile(s);
        }else {
            if (StringUtils.isEmpty(s)){
                return true;
            }else{
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
