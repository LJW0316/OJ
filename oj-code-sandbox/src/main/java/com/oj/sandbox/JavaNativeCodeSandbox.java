package com.oj.sandbox;

import com.oj.sandbox.model.ExecuteCodeRequest;
import com.oj.sandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

@Service
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
