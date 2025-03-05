package com.gxy.ojbackend.model.dto.questionsubmit;




import com.gxy.ojbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -3692002600257236347L;
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
     * 判题状态(0代判题,1判题中,2成功,3失败)
     */
    private Integer status;

}
