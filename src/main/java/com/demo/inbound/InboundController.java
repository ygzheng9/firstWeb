package com.demo.inbound;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @author ygzheng
 */
public class InboundController extends Controller {
    @Inject
    private InboundService ibSvc;

    public void summary() {
        Record summary = ibSvc.summary();

        List<Record> matSource = ibSvc.matSource();

        set("summary", summary);
        set("matSource", matSource);

        render("summary.html");
    }
}
