package com.gxy.ojbackend.controller;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.gxy.ojbackend.annotation.AuthCheck;
import com.gxy.ojbackend.common.BaseResponse;
import com.gxy.ojbackend.common.DeleteRequest;
import com.gxy.ojbackend.common.ErrorCode;
import com.gxy.ojbackend.common.ResultUtils;
import com.gxy.ojbackend.constant.UserConstant;
import com.gxy.ojbackend.exception.BusinessException;
import com.gxy.ojbackend.exception.ThrowUtils;
import com.gxy.ojbackend.model.dto.question.QuestionAddRequest;
import com.gxy.ojbackend.model.dto.question.QuestionEditRequest;
import com.gxy.ojbackend.model.dto.question.QuestionQueryRequest;
import com.gxy.ojbackend.model.dto.question.QuestionUpdateRequest;
import com.gxy.ojbackend.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.gxy.ojbackend.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.gxy.ojbackend.model.entity.Question;
import com.gxy.ojbackend.model.entity.QuestionSubmit;
import com.gxy.ojbackend.model.entity.User;
import com.gxy.ojbackend.model.vo.QuestionSubmitVO;
import com.gxy.ojbackend.model.vo.QuestionVO;
import com.gxy.ojbackend.service.QuestionService;
import com.gxy.ojbackend.service.QuestionSubmitService;
import com.gxy.ojbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/api/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        if (questionAddRequest.getJudgeCase() != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(questionAddRequest.getJudgeCase()));
        }
        if (questionAddRequest.getJudgeConfig() != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(questionAddRequest.getJudgeConfig()));
        }
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldquestion = questionService.getById(id);
        ThrowUtils.throwIf(oldquestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldquestion.getUserId().equals(user.getId()) && !userService.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        if (questionUpdateRequest.getJudgeCase() != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeCase()));
        }
        if (questionUpdateRequest.getJudgeConfig() != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeConfig()));
        }
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldquestion = questionService.getById(id);
        ThrowUtils.throwIf(oldquestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        if (questionEditRequest.getJudgeCase() != null) {
            question.setJudgeCase(JSONUtil.toJsonStr(questionEditRequest.getJudgeCase()));
        }
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldquestion = questionService.getById(id);
        ThrowUtils.throwIf(oldquestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldquestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (!Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据 id 获取vo类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest).orderBy(true, false, "updateTime"));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        String redisKey = "questionList" +questionQueryRequest.getCurrent();
        boolean b = questionQueryRequest.getTags().isEmpty() && Objects.equals(questionQueryRequest.getTitle(), "") && questionQueryRequest.getCurrent() == 1;
        //redis 缓存
        if (b&&stringRedisTemplate.opsForValue().get(redisKey) != null) {
            log.info("redis缓存命中");
            return ResultUtils.success(
                    JSONUtil.toBean(stringRedisTemplate.opsForValue().get(redisKey), Page.class));
        } else if (b) {
            //redis时间格式化
            JSONConfig jsonConfig = new JSONConfig();
            jsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
            long current = questionQueryRequest.getCurrent();
            long size = questionQueryRequest.getPageSize();
            QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
            Page<Question> questionPage = questionService.page(new Page<>(current, size), queryWrapper);
            log.info("questionPage:{}", "题目列表");
            Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
            stringRedisTemplate.opsForValue().set(
                    redisKey, JSONUtil.toJsonStr(questionVOPage, jsonConfig), 3, TimeUnit.MINUTES);
            return ResultUtils.success(questionVOPage);
        }else {
            long current = questionQueryRequest.getCurrent();
            long size = questionQueryRequest.getPageSize();
            // 限制爬虫
            ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
            //获取查询条件
            QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
            Page<Question> questionPage = questionService.page(new Page<>(current, size), queryWrapper);
            log.info("questionPage:{}", "题目列表");
            Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
//            //如果查询结果是空,不放入redis
//            if (questionVOPage.getRecords().isEmpty()){
//
//                return ResultUtils.success(questionVOPage);
//            }

            return ResultUtils.success(questionVOPage);
        }

    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 提交题目提交表单
     */
    @PostMapping("/question_submit")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userService.getLoginUser(request);
        Long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询提交内容
     */
    @PostMapping("/question_submit/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitVOByPage(
            @RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        Integer pageSize = questionSubmitQueryRequest.getPageSize();
        Integer current = questionSubmitQueryRequest.getCurrent();
        Page<QuestionSubmit> qsp = new Page<>(current, pageSize);
        QueryWrapper<QuestionSubmit> qw = questionSubmitService.getQueryWrapper(questionSubmitQueryRequest)
                .orderBy(true, false, "createTime");
        //page方法会自动计算total
        Page<QuestionSubmit> page = questionSubmitService.page(qsp, qw);
        User loginUser = userService.getLoginUser(request);

        return ResultUtils.success(questionSubmitService.getQuestionVOPage(page, loginUser));
    }


}
