package com.demo.okadmin;

import com.demo.model.User;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

/**
 * @author ygzheng
 */
public class AuthInterceptor implements Interceptor {
    @Inject
    UserService userSvc;

    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();

        User loginAccount = controller.getAttr(LoginService.loginAccountCacheName);
        if (loginAccount != null) {
            String actionKey = inv.getActionKey();

            // 后台配置：受控资源，所需的角色
            if (userSvc.hasPermission(loginAccount, actionKey)) {
                inv.invoke();
                return;
            }
        }

        // renderError(404) 避免暴露后台管理 url，增加安全性
        if (loginAccount == null || inv.getActionKey().equals("/admin")) {
            inv.getController().renderError(404);
        } else {
            // renderJson 提示没有操作权限，提升用户体验
            inv.getController().renderJson(Ret.fail("msg", "没有操作权限"));
        }
    }
}
