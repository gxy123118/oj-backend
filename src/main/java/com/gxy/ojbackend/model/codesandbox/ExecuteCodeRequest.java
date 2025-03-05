package com.gxy.ojbackend.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱运行请求
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeRequest {
    //代码
    private String code;
    //测试用例组
    private List<String> input;
    //语言
    private String language;
}
