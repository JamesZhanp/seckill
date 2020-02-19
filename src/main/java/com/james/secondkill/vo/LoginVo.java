package com.james.secondkill.vo;

import com.james.secondkill.validator.IsMobile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 04 20:01
 *
 * 用户接受客户端请求中的表单数据
 * 使用JSR303完成参数校验
 */

@ToString
public class LoginVo implements Serializable {
    @NotNull
    @IsMobile
    @Setter
    @Getter
    private String mobile;

    @NotNull
    @Length(min = 32)
    @Setter
    @Getter
    private String password;


}
