package com.gxy.ojbackend.model.dto.questionsubmit;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 判题信息
 */
@Data
@AllArgsConstructor
public class JudgeInfo {
    /**
     * 程序运行时长，单位为 ms
     */
    private Long time;
    /**
     * 程序占用内存，单位为 kB
     */
    private Long memory;
    /**
     * 判题信息展示
     */
    private String message;
}
