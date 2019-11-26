package com.demo.okadmin;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

/**
 * @author ygzheng
 */
public class OkLandingController extends Controller {
    @Inject
    LoginService loginSvc;

    public void index() {
        // render("index.html");
        // redirect("/pages/mat/projectPlant");
        forwardAction("/pages/mat/projectPlant");
    }

    @Clear(LoginInterceptor.class)
    public void login() {
        render("login.html");
    }

    @Clear(LoginInterceptor.class)
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


    @Clear(LoginInterceptor.class)
    public void logout() {
        // cookie 登录未成功，证明该 cookie 已经没有用处，删之
        removeCookie(LoginService.sessionIdName);

        redirect("/login");
    }
}
