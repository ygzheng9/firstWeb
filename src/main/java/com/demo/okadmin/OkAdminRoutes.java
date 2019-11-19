package com.demo.okadmin;

import com.demo.inbound.InboundController;
import com.demo.matanalysis.MatController;
import com.demo.matanalysis.RegionSalesController;
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

        // bom 共用性分析
        add("/pages/mat", MatController.class, "/pages/mat");

        // 区域销售分析
        add("/pages/sales", RegionSalesController.class, "/pages/mat");

        // 入库分析
        add("/pages/inbound", InboundController.class, "/pages/inbound");


    }
}
