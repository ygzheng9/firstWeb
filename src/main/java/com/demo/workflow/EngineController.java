package com.demo.workflow;

import com.demo.model.WkInstance;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

import java.util.List;

/**
 * @author ygzheng
 * 审批流的控制核心
 */
public class EngineController extends Controller {
    @Inject
    EngineService engSvc;

    public void list() {
        List<WkInstance> items = engSvc.findInstances();

        set("items", items);

        render("instance_list.html");
    }

    public void doApprove() {
        String id = get("id");

        engSvc.doApprove(id, "审批意见");

        renderJson(Ret.ok("msg", "审批通过"));
    }

    public void doReject() {
        String id = get("id");

        engSvc.doReject(id, "驳回意见");

        renderJson(Ret.ok("msg", "驳回"));
    }

}
