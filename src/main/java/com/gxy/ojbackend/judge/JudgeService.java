package com.gxy.ojbackend.judge;


import com.gxy.ojbackend.model.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge(Long id);
}
