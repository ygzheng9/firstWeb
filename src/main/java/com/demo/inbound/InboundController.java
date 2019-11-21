package com.demo.inbound;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
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
        Record matchedMat = ibSvc.matchedMat();

        List<Record> plantAmt = ibSvc.plantAmt();

        set("summary", summary);
        set("matSource", matSource);
        set("matchedMat", matchedMat);
        set("matchedMatStr", JsonKit.toJson(matchedMat));

        set("plantAmt", plantAmt);


        render("summary.html");
    }

    public void matSourceAmtBoxPlot() {
        List<List<BigDecimal>> items = ibSvc.matSourceAmtBoxPlot();

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void matSourceRatio() {
        List<Record> items = ibSvc.matSourceRatio();

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void vendorAmtByPlant() {
        String plant = get("p");
        List<Record> items = ibSvc.vendorAmtByPlant(plant);
        set("items", items);

        render("plant_vendor_amt.html");
    }

    public void matAmtByOrder() {
        String order = get("o");
        List<Record> items = ibSvc.matAmtByOrder(order);
        set("items", items);

        render("mat_by_order.html");
    }

    public void itemByOrderMat() {
        String order = get("o");
        String mat = get("m");
        List<Record> items = ibSvc.itemByOrderMat(order, mat);
        set("items", items);
        set("order", order);
        set("mat", mat);

        render("item_by_order_mat.html");
    }

    public void itemByOrderMatData() {
        String order = get("o");
        String mat = get("m");
        List<Record> items = ibSvc.itemByOrderMatDay(order, mat);

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

}
