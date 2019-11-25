package com.demo.model;

import com.demo.model.base.BaseSession;

/**
 * Generated by JFinal.
 */
public class Session extends BaseSession<Session> {

    /**
     * 登录会话是否已过期
     */
    public boolean isExpired() {
        return getExpireAt() < System.currentTimeMillis();
    }

    /**
     * 登录会话是否未过期
     */
    public boolean notExpired() {
        return ! isExpired();
    }
	
}
