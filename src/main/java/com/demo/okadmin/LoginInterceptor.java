package com.demo.okadmin;

import com.demo.model.User;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

/**
 * @author ygzheng
 * 从 cookie 中取得 sessionid，然后找到对应的 user，保存到 request 中
 * 注意：将此拦截器设置为全局拦截器，所有 action 都需要
 */
public class LoginInterceptor implements Interceptor {
    @Inject
    LoginService loginSrv;

    @Override
    public void intercept(Invocation inv) {
        User loginAccount = null;
        Controller c = inv.getController();
        String sessionId = c.getCookie(LoginService.sessionIdName);
        if (sessionId != null) {
            loginAccount = loginSrv.getUserBySessionId(sessionId);
            if (loginAccount != null) {
                // 已经登录过, 把当前用户存入 request 中，供后续 action 使用，
                // 在 directive 中也可以使用，参见 PermissionDirective
                c.set(LoginService.loginAccountCacheName, loginAccount);

                // 把 actionKey 写入 response，供前端做菜单高亮设置
                c.set("actionKey", inv.getActionKey());

                inv.invoke();

                return;
            }
        }

        if (loginAccount == null) {
            // cookie 登录未成功，证明该 cookie 已经没有用处，删之
            c.removeCookie(LoginService.sessionIdName);

            String queryString = inv.getController().getRequest().getQueryString();
            if (StrKit.isBlank(queryString)) {
                inv.getController().redirect("/login?returnUrl=" + inv.getActionKey());

            } else {
                inv.getController().redirect("/login?returnUrl=" + inv.getActionKey() + "?" + queryString);
            }
        }
    }
}
