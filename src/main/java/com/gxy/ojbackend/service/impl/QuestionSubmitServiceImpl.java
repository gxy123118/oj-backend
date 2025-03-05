package com.gxy.ojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gxy.ojbackend.common.ErrorCode;
import com.gxy.ojbackend.constant.CommonConstant;
import com.gxy.ojbackend.constant.UserConstant;
import com.gxy.ojbackend.exception.BusinessException;
import com.gxy.ojbackend.judge.JudgeService;
import com.gxy.ojbackend.mapper.QuestionSubmitMapper;
import com.gxy.ojbackend.messageproductor.MessageToJudge;
import com.gxy.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.gxy.ojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.gxy.ojbackend.model.entity.Question;
import com.gxy.ojbackend.model.entity.QuestionSubmit;
import com.gxy.ojbackend.model.entity.User;
import com.gxy.ojbackend.model.enums.QuestionSubmitLanguageEnum;
import com.gxy.ojbackend.model.enums.QuestionSubmitStatusEnum;
import com.gxy.ojbackend.model.vo.QuestionSubmitVO;
import com.gxy.ojbackend.service.QuestionService;
import com.gxy.ojbackend.service.QuestionSubmitService;
import com.gxy.ojbackend.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Baaadman
 * @description 针对表【question_submit(题目提交表)】的数据库操作Service实现
 * @createDate 2025-01-26 00:05:54
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;


    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private MessageToJudge messageToJudge;

    /**
     * 提交题目
     *
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        long userId = loginUser.getId();
        // 是否已点赞
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(userId);
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByText = QuestionSubmitLanguageEnum.getEnumByText(language);
        if (enumByText == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的编程语言");
        }
        questionSubmit.setLanguage(enumByText.getValue());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());

        boolean save = save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "提交失败");
        }
//        //todo 判题
        messageToJudge.sendMessageToJudge(questionSubmit.getId());

        Question oldQuestion = questionService.getById(questionSubmit.getQuestionId());
        Integer oldSubmitNum = oldQuestion.getSubmitNum();
        Integer newSubmitNum = oldSubmitNum + 1;
        questionService.update()
                .eq("id", questionSubmit.getQuestionId())
                .set("submitNum", newSubmitNum)
                .update();
        //todo 发送延时消息判断判题是否成功,成功后更新题目的通过数

        messageToJudge.sendMessageToCheckStatus(questionSubmit.getId());
        return questionSubmit.getId();
    }

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {


        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        if (ObjectUtils.isNotEmpty(questionId)) {
            queryWrapper.eq("questionId", questionId);
        }
        if (ObjectUtils.isNotEmpty(userId)) {
            queryWrapper.eq("userId", userId);
        }
        if (StringUtils.isNotBlank(language)) {
            queryWrapper.eq("language", language);
        }
        if (ObjectUtils.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    /**
     * 将QuestionSubmit转化为QuestionSubmitVO(脱敏)
     *
     * @param qs
     * @param loginUser
     * @return
     */
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit qs, User loginUser) {
        QuestionSubmitVO qsVO = new QuestionSubmitVO();

        Long questionId = qs.getQuestionId();
        Long userId = qs.getUserId();
        String language = qs.getLanguage();
        String judgeInfo = qs.getJudgeInfo();
        Integer status = qs.getStatus();
        String code = qs.getCode();
        Date createTime = qs.getCreateTime();
        qsVO.setQuestionId(questionId);
        qsVO.setUserId(userId);
        qsVO.setLanguage(language);
        qsVO.setJudgeInfo(judgeInfo);
        qsVO.setStatus(status);
        if (!Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE) && !Objects.equals(loginUser.getId(), userId)) {
            qsVO.setCode(null);
        } else {
            qsVO.setCode(code);
        }
        qsVO.setCreateTime(createTime);

        return qsVO;
    }

    /**
     * 分页获取列表包装VO数据
     *
     * @param page
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionVOPage(Page<QuestionSubmit> page, User loginUser) {
        List<QuestionSubmitVO> pageList = page.getRecords().stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser)).collect(Collectors.toList());
        Page<QuestionSubmitVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        return voPage.setRecords(pageList);
    }

}









