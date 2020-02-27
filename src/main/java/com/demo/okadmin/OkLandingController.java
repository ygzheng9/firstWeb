package com.demo.okadmin;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

/**
 * @author ygzheng
 */

@Clear({LoginInterceptor.class, AuthInterceptor.class})
public class OkLandingController extends Controller {
    @Inject
    LoginService loginSvc;

    public void index() {
        forwardAction("/pages/relation/vendorPlant");
    }

    public void login() {
        render("login.html");
    }

    public void doLogin() {
        Ret ret = loginSvc.login(getPara("email"), getPara("password"), true);
        if (ret.isOk()) {
            String sessionId = ret.getStr(LoginService.sessionIdName);
            int maxAgeInSeconds = ret.getInt("maxAgeInSeconds");
            setCookie(LoginService.sessionIdName, sessionId, maxAgeInSeconds, true);
            set(LoginService.loginAccountCacheName, ret.get(LoginService.loginAccountCacheName));

            // 如果 returnUrl 存在则跳过去，否则跳去首页
            ret.set("returnUrl", getPara("returnUrl", "/"));
        }
        renderJson(ret);
    }

    public void logout() {
        // 退出后，删除 cookie
        removeCookie(LoginService.sessionIdName);

        forwardAction("/login");
    }

    public void overview() {
        render("overview.html");
    }
}
