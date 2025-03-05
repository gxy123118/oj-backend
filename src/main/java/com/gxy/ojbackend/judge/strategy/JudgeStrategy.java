package com.gxy.ojbackend.judge.strategy;


import com.gxy.ojbackend.model.dto.questionsubmit.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
