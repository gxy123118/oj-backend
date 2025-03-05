package com.gxy.ojbackend.judge.strategy;


import com.gxy.ojbackend.model.entity.Question;
import com.gxy.ojbackend.model.entity.QuestionSubmit;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JudgeContext {
    private Question question;
    private QuestionSubmit questionSubmit;

}
