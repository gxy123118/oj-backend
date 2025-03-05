package com.gxy.ojbackend.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayExchangeAndQueue {

    // 创建延迟交换机
    @Bean
    public Exchange delayExchange() {
        return ExchangeBuilder
                .directExchange(DelayConstant.DELAY_EXCHANGE)
                .durable(true) // 设置交换机持久化
                .delayed().build();
    }

    // 创建延迟队列
    @Bean
    public Queue delayQueue() {
        return new Queue(DelayConstant.DELAY_QUEUE, true);
    }

    // 绑定延迟队列到延迟交换机
    @Bean
    public Binding bindingDelay() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DelayConstant.DELAY_ROUTING_KEY).noargs();
    }


}
