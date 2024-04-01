package com.yupi.oj.judge.strategy;

import com.yupi.oj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    /**
     * 执行
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
