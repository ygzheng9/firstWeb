package com.demo.xstart;

import com.demo.config.CorsInterceptor;
import com.demo.config.PjaxInterceptor;
import com.demo.dataviz.DataController;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.config.Routes;

/**
 * @author ygzheng
 */
@Clear(PjaxInterceptor.class)
@Before(CorsInterceptor.class)
public class APIRoutes extends Routes {

    @Override
    public void config() {
        setBaseViewPath("/view/api");

        add("/api", APIController.class, "/");
        add("/api/user", APIUserController.class, "/");

        add("/api/dataviz", DataController.class, "/");

    }
}
