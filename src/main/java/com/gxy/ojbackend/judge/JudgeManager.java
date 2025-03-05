package com.gxy.ojbackend.judge;


import com.gxy.ojbackend.judge.strategy.DefaultJudgeStrategy;
import com.gxy.ojbackend.judge.strategy.JavaJudgeStrategy;
import com.gxy.ojbackend.judge.strategy.JudgeContext;
import com.gxy.ojbackend.model.dto.questionsubmit.JudgeInfo;
import com.gxy.ojbackend.model.entity.QuestionSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {
    @Autowired
    private DefaultJudgeStrategy defaultJudgeStrategy;
    @Autowired
    private JavaJudgeStrategy javaJudgeStrategy;


    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        if (language.equals("java")) {
            return javaJudgeStrategy.doJudge(judgeContext);
        }
        return defaultJudgeStrategy.doJudge(judgeContext);
    }

}
