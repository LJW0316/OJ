package com.oj.sandbox.security;

import java.security.Permission;

/**
 * 禁用所有权限安全管理器
 */
public class DenySecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        throw new SecurityException("权限异常");
//        super.checkPermission(perm);
    }
}
