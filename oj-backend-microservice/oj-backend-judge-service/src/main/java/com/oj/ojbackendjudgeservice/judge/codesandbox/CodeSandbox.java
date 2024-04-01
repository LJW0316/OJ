package com.oj.ojbackendjudgeservice.judge.codesandbox;

import com.oj.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.oj.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
