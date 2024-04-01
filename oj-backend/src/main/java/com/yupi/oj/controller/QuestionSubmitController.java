package com.yupi.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.oj.common.BaseResponse;
import com.yupi.oj.common.ErrorCode;
import com.yupi.oj.common.ResultUtils;
import com.yupi.oj.exception.BusinessException;
import com.yupi.oj.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.yupi.oj.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.yupi.oj.model.entity.QuestionSubmit;
import com.yupi.oj.model.entity.User;
import com.yupi.oj.model.vo.QuestionSubmitVO;
import com.yupi.oj.service.QuestionSubmitService;
import com.yupi.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@Deprecated
//@RequestMapping("/question_submit")
public class QuestionSubmitController {
//    @Resource
//    private QuestionSubmitService questionSubmitService;
//
//    @Resource
//    private UserService userService;
//
//    /**
//     * 提交题目
//     *
//     * @param questionSubmitAddRequest
//     * @param request
//     * @return resultNum
//     */
//    @PostMapping("/")
//    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
//                                               HttpServletRequest request) {
//        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 登录才能提交
//        final User loginUser = userService.getLoginUser(request);
//        Long questionId = questionSubmitAddRequest.getQuestionId();
//        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
//        return ResultUtils.success(questionSubmitId);
//    }
//
//    /**
//     * 分页获取题目提交列表（除管理员外，普通用户只能非提交代码等公开信息）
//     *
//     * @param questionQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page")
//    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
//                                                                         HttpServletRequest request) {
//        long current = questionSubmitQueryRequest.getCurrent();
//        long size = questionSubmitQueryRequest.getPageSize();
//        //从数据库中查询原始的题目提交分页
//        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
//                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
//        User loginUser = userService.getLoginUser(request);
//        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
//    }
}
