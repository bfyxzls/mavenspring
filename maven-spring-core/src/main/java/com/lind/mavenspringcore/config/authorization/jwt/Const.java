package com.lind.mavenspringcore.config.authorization.jwt;

public interface Const {
    String TOKEN_SPLIT = "Bearer ";
    /**
     * JWT签名加密key
     */
    String JWT_SIGN_KEY = "xboot";
    /**
     * token参数头
     */
    String HEADER = "accessToken";
    String HEADERS = "access_token";
    /**
     * 交互token前缀key
     */
    String TOKEN_PRE = "XBOOT_TOKEN_PRE:";
    /**
     * 权限参数头
     */
    String AUTHORITIES = "authorities";
    /**
     * 用户token前缀key 单点登录使用
     */
    String USER_TOKEN = "XBOOT_USER_TOKEN:";
    /**
     * 用户选择JWT保存时间参数头
     */
    String SAVE_LOGIN = "saveLogin";
}
