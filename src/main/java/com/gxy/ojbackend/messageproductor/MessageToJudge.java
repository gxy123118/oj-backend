package com.gxy.ojbackend.messageproductor;


import com.gxy.ojbackend.rabbitmq.DelayConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageToJudge {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessageToJudge(Long id) {
        rabbitTemplate.convertAndSend("judge.exchange", "judgeKey", id);
    }

    public void sendMessageToCheckStatus(Long id) {
        rabbitTemplate.convertAndSend(DelayConstant.DELAY_EXCHANGE, DelayConstant.DELAY_ROUTING_KEY, id,
                messagePostProcessor -> {
                    // 设置延迟时间（以毫秒为单位），这就是消息的过期时间
                    messagePostProcessor.getMessageProperties().setDelay(10000);
                    return messagePostProcessor;
                }


        );
    }


}
