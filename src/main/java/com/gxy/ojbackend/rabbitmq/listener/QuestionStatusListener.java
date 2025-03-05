package com.gxy.ojbackend.rabbitmq.listener;

import cn.hutool.json.JSONUtil;

import com.gxy.ojbackend.model.dto.questionsubmit.JudgeInfo;
import com.gxy.ojbackend.model.entity.Question;

import com.gxy.ojbackend.model.entity.QuestionSubmit;
import com.gxy.ojbackend.model.enums.JudgeInfoMessageEnum;
import com.gxy.ojbackend.rabbitmq.DelayConstant;
import com.gxy.ojbackend.service.QuestionService;

import com.gxy.ojbackend.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
public class QuestionStatusListener {

    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private QuestionService questionService;

    @RabbitListener(queues = DelayConstant.DELAY_QUEUE)
    public void questionStatusListener(Long message) {
        log.info("状态判断者收到消息：{}", message);
        //todo 更新题目状态
        QuestionSubmit questionSubmit = questionSubmitService.getById(message);
        String judgeInfo = questionSubmit.getJudgeInfo();
        JudgeInfo bean = JSONUtil.toBean(judgeInfo, JudgeInfo.class);
        // 如果判题成功，更新题目的通过数
        Question oldQuestion = questionService.getById(questionSubmit.getQuestionId());
        log.info("判题状态信息{}", bean.getMessage());

        if (Objects.equals(bean.getMessage(), JudgeInfoMessageEnum.ACCEPTED.toString())) {

            questionService.update()
                    .eq("id", questionSubmit.getQuestionId())
                    .set("acceptNum", oldQuestion.getAcceptNum() + 1)
                    .update();
        }

    }
}
