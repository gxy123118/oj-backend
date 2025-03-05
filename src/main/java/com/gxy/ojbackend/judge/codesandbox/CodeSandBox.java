package com.gxy.ojbackend.judge.codesandbox;


import com.gxy.ojbackend.model.codesandbox.ExecuteCodeRequest;
import com.gxy.ojbackend.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandBox {


    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
