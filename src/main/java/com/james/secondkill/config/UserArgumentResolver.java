package com.james.secondkill.config;

import org.springframework.stereotype.Service;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.List;

/**
 * 解析请求，并将请求的参数设备知道方法参数当中
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 15:44
 */

@Service
public class UserArgumentResolver implements HandlerResolver {
    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        return null;
    }
}
