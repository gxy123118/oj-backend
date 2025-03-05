package com.gxy.ojbackend.judge.controller.inner;


import com.gxy.ojbackend.judge.JudgeService;
import com.gxy.ojbackend.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/judge/inner")
public class JudgeInnerController {
    @Resource
    private JudgeService judgeService;

    @GetMapping("/do")
    public QuestionSubmit doJudge(Long id) {
        return judgeService.doJudge(id);
    }
}
