package com.demo.okadmin;

import com.demo.matanalysis.MatController;
import com.jfinal.config.Routes;

/**
 * @author ygzheng
 */
public class OkAdminRoutes extends Routes {
    @Override
    public void config() {
        setBaseViewPath("/view/okadmin");

        add("/", OkLandingController.class);
        add("/pages", OKPagesController.class, "/pages");

        add("/pages/member", MemberController.class, "/pages/member");

        add("/pages/mat", MatController.class, "/pages/mat");

    }
}
