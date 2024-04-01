package com.oj.ojbackendjudgeservice.judge.strategy;

import com.oj.ojbackendmodel.model.codesandbox.JudgeInfo;

public interface JudgeStrategy {

    /**
     * 执行
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
