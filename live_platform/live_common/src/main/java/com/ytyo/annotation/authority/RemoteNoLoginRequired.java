package com.ytyo.annotation.authority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 添加该注解后，远程调用无需登录校验，但是网关还会拦截登录
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteNoLoginRequired {
}
