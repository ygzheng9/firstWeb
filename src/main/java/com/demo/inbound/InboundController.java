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

    public void orderAmtByPlant() {
        String plant = get("p");
        String vendor = get("v", "");

        if (vendor.length() == 0) {
            List<Record> items = ibSvc.orderAmtByPlant(plant);
            set("items", items);
        } else {
            List<Record> items = ibSvc.orderAmtByPlantVendor(plant, vendor);
            set("items", items);
        }

        render("plant_order_amt.html");
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


    public void plantVendor() {
        render("plant_vendor.html");
    }

    public void plantVendorData() {
        List<Record> items = ibSvc.plantAmt();

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("items", items);
        renderJson(data);
    }

    public void vendorAmtByPlantData() {
        String plant = get("p");
        List<Record> items = ibSvc.vendorAmtByPlant(plant);

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("items", items);
        renderJson(data);
    }


    public void matMultiSourceSummary() {
        List<Record> matSource = ibSvc.matSource();
        set("matSource", matSource);

        render("mat_multi_source_summary.html");
    }

    public void matMultiSource() {
        List<Record> items = ibSvc.matMultiSource();
        set("items", items);

        render("mat_multi_source.html");
    }

    public void matMultiSourceVendors() {
        String mat = get("m", "");
        List<Record> items = ibSvc.matMultiSourceVendors(mat);
        set("items", items);
        set("dataStr", JsonKit.toJson(items));

        List<Record> ibItems = ibSvc.matMultiSourceIBItems(mat);
        set("ibItems", ibItems);

        List<Record> ibPlants = ibSvc.matMultiSourcePlants(mat);
        set("ibPlants", ibPlants);

        render("mat_multi_source_vendors.html");
    }

    public void matMultiSourceIBItemsByPlant() {
        String mat = get("m", "");
        String plant = get("p", "");
        List<Record> items = ibSvc.matMultiSourceIBItemsByPlant(mat, plant);
        set("items", items);
        set("itemsStr", JsonKit.toJson(items));

        keepPara();

        render("mat_multi_source_ibitems_by_plant.html");

    }
}
