package com.gxy.ojbackend.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.gxy.ojbackend.judge.codesandbox.CodeSandBox;
import com.gxy.ojbackend.judge.codesandbox.util.SandboxResponseHandle;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeRequest;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeResponse;
import org.springframework.beans.factory.annotation.Value;


public class RemoteCodeSandBox implements CodeSandBox {
    @Value("${codesandbox.url}")
    private String ip;
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        String url = "http://"+ip+"/exec";
        //拿到响应数据
        String body = HttpUtil.createPost(url)
                .body(JSONUtil.toJsonStr(executeCodeRequest))
                .timeout(10000)
                .execute()
                .body();
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(body, ExecuteCodeResponse.class);
        ExecuteCodeResponse response = SandboxResponseHandle.handle(executeCodeResponse);
        return response;

    }
}
