package com.gxy.ojbackend.judge.rabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class QueueAndExchange {


    // 定义交换机（Direct 类型）
    @Bean
    public DirectExchange judgeExchange() {
        return new DirectExchange("judge.exchange"); // 持久化，不自动删除
    }

    @Bean
    public Queue judgeQueue() {
        return new Queue("judge.queue");
    }

    // 绑定队列到交换机，并指定路由键
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(judgeQueue())
                .to(judgeExchange())
                .with("judgeKey");
    }

}
