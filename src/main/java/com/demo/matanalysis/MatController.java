package com.demo.matanalysis;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @author ygzheng
 */
public class MatController extends Controller {
    private static Log log = Log.getLog(MatController.class);

    private static int StatusOK = 200;

    @Inject
    private MatService bomSvc;

    public void index() {
        render("index.html");
    }

    public void getClientHeat() {
        List<Record> items = bomSvc.getClientProjectMapping();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void getBomList() {
        String client = get("client");
        String plant = get("plant");

        List<Record> items = bomSvc.getBomList(client, plant);

        set("items", items);
        keepPara();

        render("bomlist.html");
    }

    public void getBomItems() {
        String bomid = get("bomid");

        List<BomItem> items = bomSvc.getBomItems(bomid);

        set("items", items);
        keepPara();

        render("bomitems.html");
    }

    public void reuseByBom() {
        render("reuse1.html");
    }

    public void reuseByBomData() {
        List<Record> items = bomSvc.reuseByBom();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }
}
