package com.demo.okadmin;

import com.demo.model.User;
import com.jfinal.aop.Aop;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

/**
 * @author ygzheng
 * html 中做权限控制，效果是：显示/不显示，参数是 resourceKey
 */
public class PermissionDirective extends Directive {
    private static UserService userSvc = Aop.get(UserService.class);

    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        // 在 LoginInterceptor 中设置，这里提取
        User account = (User) scope.getRootData().get(LoginService.loginAccountCacheName);
        if (account != null) {
            // 和 AuthInterceptor 中的逻辑保持相同
            if (userSvc.hasPermission(account, getParam(scope))) {
                stat.exec(env, scope, writer);
            }
        }
    }

    /**
     * 获取 #permission 指令参数，参数是 资源，配置在 z_resources.key
     */
    private String getParam(Scope scope) {
        Object value = exprList.eval(scope);
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new IllegalArgumentException("权限参数只能为 String 类型");
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
