package com.code123.share.skywalking.plugin.dubbo25;

import com.alibaba.dubbo.rpc.RpcContext;
import com.caucho.hessian.client.HessianConnection;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.DEFAULT_EXCHANGER;

/**
 * @author nile
 */
public class Dubbo25HessianProxyInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) throws Throwable {
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                              Object ret) throws Throwable {
        HessianConnection conn = (HessianConnection) allArguments[0];
        final Map<String, String> attachments = RpcContext.getContext().getAttachments();
        if (attachments != null && attachments.size() > 0) {
            for (Map.Entry<String, String> entry : attachments.entrySet()) {
                conn.addHeader(DEFAULT_EXCHANGER + entry.getKey(), entry.getValue());
            }
        }
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                      Class<?>[] argumentsTypes, Throwable t) {
    }
}
