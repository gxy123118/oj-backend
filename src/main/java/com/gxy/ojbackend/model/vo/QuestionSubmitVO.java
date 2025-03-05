package com.gxy.ojbackend.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class QuestionSubmitVO implements Serializable {
    private static final long serialVersionUID = -2118668440058264854L;
    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 判断结果（json 对象）
     */
    private String judgeInfo;

    /**
     * 判题状态(0代判题,1判题中,2成功,3失败)
     */
    private Integer status;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 创建时间
     */
    private Date createTime;

}
