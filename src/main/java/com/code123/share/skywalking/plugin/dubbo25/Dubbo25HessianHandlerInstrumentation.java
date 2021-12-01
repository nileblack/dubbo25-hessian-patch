package com.code123.share.skywalking.plugin.dubbo25;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.NameMatch;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class Dubbo25HessianHandlerInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "com.alibaba.dubbo.rpc.protocol.hessian.HessianProtocol$HessianHandler";
    private static final String INTERCEPT_CLASS = "com.code123.share.skywalking.plugin.dubbo25.Dubbo25HessianHandlerInterceptor";

    @Override
    protected String[] witnessClasses() {
        return new String[]{
                //这个类好像在2.6版本中就删除了，我就拿这个来判断以下dubbo的版本，不知道有没有更好的
                "com.alibaba.dubbo.registry.pages.RegisteredPageHandler"
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("handle");
                }

                @Override
                public String getMethodsInterceptor() {
                    return INTERCEPT_CLASS;
                }

                @Override
                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
