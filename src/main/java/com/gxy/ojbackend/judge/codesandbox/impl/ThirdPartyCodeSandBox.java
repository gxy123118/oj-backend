package com.gxy.ojbackend.judge.codesandbox.impl;



import com.gxy.ojbackend.judge.codesandbox.CodeSandBox;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeRequest;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeResponse;
import com.gxy.ojbackend.model.dto.questionsubmit.JudgeInfo;

import java.util.ArrayList;

public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return new ExecuteCodeResponse(
                new ArrayList<>(),
                0,
                "编译成功",
                new JudgeInfo(10L, 10L, "")
        );
    }
}
