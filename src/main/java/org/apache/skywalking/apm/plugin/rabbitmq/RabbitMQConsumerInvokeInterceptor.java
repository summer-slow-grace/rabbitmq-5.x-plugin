package org.apache.skywalking.apm.plugin.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.tag.StringTag;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;




import java.lang.reflect.Method;
import java.util.HashMap;

public class RabbitMQConsumerInvokeInterceptor implements InstanceMethodsAroundInterceptor {
    public static final String OPERATE_NAME_PREFIX = "RabbitMQ/";
    public static final String CONSUMER_OPERATE_NAME_SUFFIX = "/Consumer/invoke/";
 
    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        ContextCarrier contextCarrier = new ContextCarrier();

        Connection connection = ((Channel)allArguments[0]).getConnection();
        String url =  connection.getAddress().toString().replace("/","") + ":" + connection.getPort();
        Message message = (Message) allArguments[1];
        MessageProperties msgProperties = message.getMessageProperties();
        String msgTopic = msgProperties.getConsumerQueue();
        AbstractSpan activeSpan = ContextManager.createEntrySpan(OPERATE_NAME_PREFIX + "Queue/" + msgTopic +
                CONSUMER_OPERATE_NAME_SUFFIX , null).start(System.currentTimeMillis());
        Tags.MQ_BROKER.set(activeSpan, url);
        Tags.MQ_TOPIC.set(activeSpan, msgProperties.getReceivedExchange());
        Tags.MQ_QUEUE.set(activeSpan, msgProperties.getConsumerQueue());
        activeSpan.setComponent(ComponentsDefine.RABBITMQ_CONSUMER);
        SpanLayer.asMQ(activeSpan);
        CarrierItem next = contextCarrier.items();
        while (next.hasNext()) {
            next = next.next();
            if (msgProperties.getHeaders() != null && msgProperties.getHeaders().get(next.getHeadKey()) != null) {
                next.setHeadValue(msgProperties.getHeaders().get(next.getHeadKey()).toString());
            }
        }
        ContextManager.extract(contextCarrier);
    }

    @Override
    public Object afterMethod(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Object o) throws Throwable {
        ContextManager.stopSpan();
        return o;
    }

    @Override
    public void handleMethodException(EnhancedInstance enhancedInstance, Method method, Object[] objects, Class<?>[] classes, Throwable throwable) {
        ContextManager.activeSpan().log(throwable);
    }
}
