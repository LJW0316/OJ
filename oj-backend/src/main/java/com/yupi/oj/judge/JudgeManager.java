package com.yupi.oj.judge;

import com.yupi.oj.judge.strategy.DefaultJudgeStrategy;
import com.yupi.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.yupi.oj.judge.strategy.JudgeContext;
import com.yupi.oj.judge.strategy.JudgeStrategy;
import com.yupi.oj.judge.codesandbox.model.JudgeInfo;
import com.yupi.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy;
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        } else {
            judgeStrategy = new DefaultJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
