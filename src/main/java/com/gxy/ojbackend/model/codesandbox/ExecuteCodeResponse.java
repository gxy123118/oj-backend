package com.gxy.ojbackend.model.codesandbox;



import com.gxy.ojbackend.model.dto.questionsubmit.JudgeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecuteCodeResponse {
    // 执行结果组
    private List<String> output;
    // 执行状态
    private Integer status;
    // 信息
    private String message;
    // 执行信息
    private JudgeInfo judgeInfo;
}
