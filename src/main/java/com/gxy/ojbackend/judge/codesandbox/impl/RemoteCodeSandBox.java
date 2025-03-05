package com.gxy.ojbackend.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.gxy.ojbackend.judge.codesandbox.CodeSandBox;
import com.gxy.ojbackend.judge.codesandbox.util.SandboxResponseHandle;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeRequest;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class RemoteCodeSandBox implements CodeSandBox {
    @Value("${codesandbox.url}")
    private String ip;
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        String url = "http://123.207.158.188:8089/exec";
        //拿到响应数据
        String body = HttpUtil.createPost(url)
                .body(JSONUtil.toJsonStr(executeCodeRequest))
                .timeout(30000)
                .execute()
                .body();
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(body, ExecuteCodeResponse.class);
        ExecuteCodeResponse response = SandboxResponseHandle.handle(executeCodeResponse);
        log.info("远程代码沙箱执行结果：{}",response);
        return response;

    }
}
