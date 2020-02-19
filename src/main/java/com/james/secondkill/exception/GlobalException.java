package com.james.secondkill.exception;

import com.james.secondkill.controller.result.CodeMsg;

/**
 * 全局异常处理器
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 15:53
 */

public class GlobalException extends RuntimeException {
    private CodeMsg codeMsg;

    /**
     * 使用构造器接受CodeMsg
     * @param codeMsg
     */
    public GlobalException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}

