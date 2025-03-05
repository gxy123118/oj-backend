package com.gxy.ojbackend.model.dto.questionsubmit;


import lombok.Data;

import java.io.Serializable;
@Data
public class QuestionSubmitAddRequest implements Serializable {
    private static final long serialVersionUID = -6753300690572148674L;
    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;
}
