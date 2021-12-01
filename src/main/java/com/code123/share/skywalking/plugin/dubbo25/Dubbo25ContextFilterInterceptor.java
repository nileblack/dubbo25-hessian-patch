package com.code123.share.skywalking.plugin.dubbo25;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.*;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nile
 */
public class Dubbo25ContextFilterInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) throws Throwable {
        Invoker<?> invoker = (Invoker<?>) allArguments[0];
        Invocation invocation = (Invocation) allArguments[1];
        //以下是从2.6版本拷贝的代码，修复了contextFilter会导致attachment丢失的问题
        //假如invocation中有attachment，从里面清楚一些系统的标签
        Map<String, String> attachments = invocation.getAttachments();
        if (attachments != null) {
            attachments = new HashMap<String, String>(attachments);
            attachments.remove(Constants.PATH_KEY);
            attachments.remove(Constants.GROUP_KEY);
            attachments.remove(Constants.VERSION_KEY);
            attachments.remove(Constants.DUBBO_VERSION_KEY);
            attachments.remove(Constants.TOKEN_KEY);
            attachments.remove(Constants.TIMEOUT_KEY);
            attachments.remove(Constants.ASYNC_KEY);// Remove async property to avoid being passed to the following invoke chain.
        }
        RpcContext.getContext()
                .setInvoker(invoker)
                .setInvocation(invocation)
                //2.5版本中这个是设置的，会导致之前的filter设置的值会被清理掉，这里注释掉
                //注释掉的结果不是不需要，而是保留了上下文中的attachment
                //.setAttachments(attachments)
                .setLocalAddress(invoker.getUrl().getHost(),
                        invoker.getUrl().getPort());

        // we may already added some attachments into RpcContext before this filter (e.g. in rest protocol)
        // 假如已经有attachment的情况下，会把invocation和RpcContext中的attachment合并
        if (attachments != null) {
            if (RpcContext.getContext().getAttachments() != null) {
                RpcContext.getContext().getAttachments().putAll(attachments);
            } else {
                RpcContext.getContext().setAttachments(attachments);
            }
        }

        if (invocation instanceof RpcInvocation) {
            ((RpcInvocation) invocation).setInvoker(invoker);
        }
        Result dubboResult = null;
        try {
            dubboResult = invoker.invoke(invocation);
        } finally {
            //在final里面设置返回值，不在执行后面的方法
            result.defineReturnValue(dubboResult);
            RpcContext.removeContext();
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
