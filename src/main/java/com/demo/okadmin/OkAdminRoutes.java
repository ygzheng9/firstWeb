package com.demo.okadmin;

import com.demo.inbound.InboundController;
import com.demo.matanalysis.MatController;
import com.demo.matanalysis.RegionSalesController;
import com.demo.workday.WorkdayController;
import com.demo.workflow.EngineController;
import com.demo.workflow.TxDemoController;
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

        // 关系图分析
        add("/pages/relation", RelationController.class, "/pages/relation");

        // 审批流
        add("/pages/workflow/engine", EngineController.class, "/pages/workflow");
        add("/pages/workflow/txDemo", TxDemoController.class, "/pages/workflow");

        // 工作日志
        add("/pages/workday", WorkdayController.class, "/pages/workday");


        // stimulus
        add("/st", StimulusController.class, "/pages/stimulus");

    }
}
