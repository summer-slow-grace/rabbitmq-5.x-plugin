package org.apache.skywalking.apm.plugin.rabbitmq.define;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.MultiClassNameMatch;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.description.method.MethodDescription;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.matcher.ElementMatcher;

import static org.apache.skywalking.apm.dependencies.net.bytebuddy.matcher.ElementMatchers.named;

import static org.apache.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;

public class RabbitMQConsumerInvokeInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    public static final String INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.rabbitmq.RabbitMQConsumerInvokeInterceptor";
    public static final String ENHANCE_CLASS_PRODUCER = "org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer";
    public static final String ENHANCE_METHOD_DISPATCH = "invokeListener";
    public static final String INTERCEPTOR_CONSTRUCTOR = "org.apache.skywalking.apm.plugin.rabbitmq.RabbitMQProducerAndConsumerConstructorInterceptor";
    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {
            new ConstructorInterceptPoint() {
                    @Override public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return takesArgumentWithType(0,"com.rabbitmq.client.impl.AMQConnection");
                    }

                    @Override public String getConstructorInterceptor() {
                        return INTERCEPTOR_CONSTRUCTOR;
                    }
                }
        };
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                    @Override public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named(ENHANCE_METHOD_DISPATCH).and(takesArgumentWithType(1,"org.springframework.amqp.core.Message"));
                    }


                    @Override public String getMethodsInterceptor() {
                        return INTERCEPTOR_CLASS;
                    }

                    @Override public boolean isOverrideArgs() {
                        return true;
                    }
                }
        };
    }

    @Override
    protected ClassMatch enhanceClass() {
        return MultiClassNameMatch.byMultiClassMatch(ENHANCE_CLASS_PRODUCER);
    }
}
