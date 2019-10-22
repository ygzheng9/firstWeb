package com.demo.xstart;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * @author ygzheng
 */
public class PjaxInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
        } finally {
            Controller c = inv.getController();
            boolean isPjax = "true".equalsIgnoreCase(c.getHeader("X-PJAX"));
            c.setAttr("isPjax", isPjax);
        }
    }
}
