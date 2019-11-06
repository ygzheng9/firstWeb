package com.demo.dataviz;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;

import java.util.List;

/**
 * @author ygzheng
 */
public class VizController extends Controller {
    @Inject
    private VizService dataSvc;

    private static int StatusOK = 200;

    public void poHeads() {
        List<PoHead> items = dataSvc.loadPoHeads();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void poItems() {
        String json = HttpKit.readData(getRequest());

        Kv para = JsonKit.parse(json, Kv.class);
        String start = para.getStr("start");
        String end = para.getStr("end");


        List<PoItem> items = dataSvc.loadPoItems();
        List<PoItem> remains = dataSvc.findPOItemsByDate(items, start, end);

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", remains);

        renderJson(data);
    }

    public void matByMonth() {
        List<MatByMonth> items = dataSvc.loadMatByMonth();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void bomComponent() {
        List<BomComponent> items = dataSvc.loadBomComponents();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void loadMatInfo() {
        List<MatInfo> items = dataSvc.loadMatInfos();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public <T> void renderT(T items) {
        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }
}
