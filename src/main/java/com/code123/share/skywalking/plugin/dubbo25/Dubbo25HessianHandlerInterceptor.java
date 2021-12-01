package com.code123.share.skywalking.plugin.dubbo25;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * @author nile
 */
public class Dubbo25HessianHandlerInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) throws Throwable {
        HttpServletRequest request = (HttpServletRequest) allArguments[0];
        if(request != null) {
            Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                if (key.startsWith(Constants.DEFAULT_EXCHANGER)) {
                    RpcContext.getContext().setAttachment(key.substring(Constants.DEFAULT_EXCHANGER.length()),
                            request.getHeader(key));
                }
            }
        }
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
        Object ret) throws Throwable {
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
        Class<?>[] argumentsTypes, Throwable t) {
    }
}
