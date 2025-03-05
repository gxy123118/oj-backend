package com.gxy.ojbackend.judge.rabbitmq.listener;


import com.gxy.ojbackend.exception.BusinessException;
import com.gxy.ojbackend.judge.JudgeService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class JudgeListener {
    @Resource
    private JudgeService judgeService;

    /**
     * 监听消息并消费
     *
     * @param message
     */
    @RabbitListener(queues = "judge.queue")
    @Retryable(
            value = BusinessException.class,  // 当抛出指定异常时会重试
            maxAttempts = 2,  // 最大重试次数
            backoff = @Backoff(delay = 5000)  // 每次重试的延迟时间，单位是毫秒
    )
    public void judgeListener(Long message) {
        log.info("判题者监听到消息：{}", message);
        judgeService.doJudge(message);

    }

    @Recover
    public void recover(Exception e, Long message) {
        log.error("消息消费失败，已重试多次，消息：{}", message, e);
        // 可进行日志记录或将消息发送到死信队列（DLX）
    }
}
