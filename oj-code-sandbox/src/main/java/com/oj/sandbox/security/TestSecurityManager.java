package com.oj.sandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class TestSecurityManager {
    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());

        FileUtil.readLines(new File("D:\\Programming\\Java\\Project\\OJ\\oj-sandbox\\src\\main\\resources\\application.yml"), StandardCharsets.UTF_8);
    }
}
