package com.gxy.ojbackend.judge.codesandbox.util;


import com.gxy.ojbackend.model.codesandbox.ExecuteCodeResponse;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 沙箱返回值处理
 */

public class SandboxResponseHandle {
    public static ExecuteCodeResponse handle(ExecuteCodeResponse executeCodeResponse) {
        List<String> output = executeCodeResponse.getOutput();
        //去掉换行符和空格
        for (int i = 0; i < output.size(); i++) {
            output.set(i, output.get(i).replaceAll("[\\s\\r]", ""));
        }
        ExecuteCodeResponse executeCodeResponseNew = new ExecuteCodeResponse();
        BeanUtils.copyProperties(executeCodeResponse, executeCodeResponseNew);
        executeCodeResponseNew.setOutput(output);
        return executeCodeResponse;
    }
}
