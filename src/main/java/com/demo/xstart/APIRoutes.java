package com.demo.xstart;

import com.jfinal.config.Routes;

public class APIRoutes extends Routes {

    @Override
    public void config() {
        setBaseViewPath("/view/api");

        add("/api", APIController.class, "/");
        add("/api/user", APIUserController.class, "/");
    }
}
