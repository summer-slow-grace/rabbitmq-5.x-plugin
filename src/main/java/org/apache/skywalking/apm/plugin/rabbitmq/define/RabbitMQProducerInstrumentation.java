//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.skywalking.apm.plugin.rabbitmq.define;

import org.apache.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.MultiClassNameMatch;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.description.method.MethodDescription;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.dependencies.net.bytebuddy.matcher.ElementMatchers;

public class RabbitMQProducerInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {
    public static final String INTERCEPTOR_CLASS = "org.apache.skywalking.apm.plugin.rabbitmq.RabbitMQProducerInterceptor";
    public static final String ENHANCE_CLASS_PRODUCER = "com.rabbitmq.client.impl.ChannelN";
    public static final String ENHANCE_METHOD_DISPATCH = "basicPublish";
    public static final String INTERCEPTOR_CONSTRUCTOR = "org.apache.skywalking.apm.plugin.rabbitmq.RabbitMQProducerAndConsumerConstructorInterceptor";

    public RabbitMQProducerInstrumentation() {
    }

    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[]{new ConstructorInterceptPoint() {
            public ElementMatcher<MethodDescription> getConstructorMatcher() {
                return ArgumentTypeNameMatch.takesArgumentWithType(3, "com.rabbitmq.client.MetricsCollector");
            }

            public String getConstructorInterceptor() {
                return "org.apache.skywalking.apm.plugin.rabbitmq.RabbitMQProducerAndConsumerConstructorInterceptor";
            }
        }};
    }

    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{new InstanceMethodsInterceptPoint() {
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return ElementMatchers.named("basicPublish").and(ArgumentTypeNameMatch.takesArgumentWithType(4, "com.rabbitmq.client.AMQP$BasicProperties"));
            }

            public String getMethodsInterceptor() {
                return "org.apache.skywalking.apm.plugin.rabbitmq.RabbitMQProducerInterceptor";
            }

            public boolean isOverrideArgs() {
                return true;
            }
        }};
    }

    protected ClassMatch enhanceClass() {
        return MultiClassNameMatch.byMultiClassMatch(new String[]{"com.rabbitmq.client.impl.ChannelN"});
    }
}
