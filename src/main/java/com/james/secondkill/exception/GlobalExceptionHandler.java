package com.james.secondkill.exception;

import com.james.secondkill.controller.result.CodeMsg;
import com.james.secondkill.controller.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * 全局异常处理器
 * 在异常发生时，将会调用这边的方法返回给客户端一个响应
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 16:03
 */

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 异常处理类
     * @param request
     * @param e
     * @return
     *
     * ExceptionHandler 这个注解课值定该方法对应何种类型的异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        //打印原始的异常信息
        e.printStackTrace();

        if (e instanceof GlobalException){
            GlobalException exception = (GlobalException) e;
            return Result.error(exception.getCodeMsg());
        }else if (e instanceof BindException){
            BindException bindException = (BindException) e;
            List<ObjectError> errors = bindException.getAllErrors();
            ObjectError error = errors.get(0);
            String message = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(message));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
