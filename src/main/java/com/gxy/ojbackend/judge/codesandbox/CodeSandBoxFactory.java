package com.gxy.ojbackend.judge.codesandbox;


import com.gxy.ojbackend.judge.codesandbox.impl.ExampleCodeSandBox;
import com.gxy.ojbackend.judge.codesandbox.impl.RemoteCodeSandBox;
import com.gxy.ojbackend.judge.codesandbox.impl.ThirdPartyCodeSandBox;

/**
 * 沙箱工厂
 */
public class CodeSandBoxFactory {
    public static CodeSandBox getCodeSandBox(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandBox();
            case "remote":
                return new RemoteCodeSandBox();
            case "thirdParty":
                return new ThirdPartyCodeSandBox();
            default:
                return new ExampleCodeSandBox();
        }
    }
}
