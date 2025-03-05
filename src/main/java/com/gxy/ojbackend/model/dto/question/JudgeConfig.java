package com.gxy.ojbackend.model.dto.question;

import lombok.Data;

/**
 * 题目配置
 */
@Data
public class JudgeConfig {
    /**
     * 内存限制
     */
    private Long memoryLimit;
    /**
     * 堆栈限制
     */
    private Long stackLimit;
    /**
     * 时间限制
     */
    private Long timeLimit;

}
