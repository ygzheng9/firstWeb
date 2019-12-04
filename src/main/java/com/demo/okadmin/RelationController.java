package com.demo.okadmin;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;

/**
 * @author ygzheng
 */
public class RelationController extends Controller {
    @Inject
    RelationService relationService;

    public void vendorPlant() {
        render("vendor_plant.html");
    }

    public void vendorPlantData() {
        Kv param = relationService.vendorPlant();

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("data", param);

        renderJson(data);
    }

    public void clientPlant() {
        render("client_plant.html");
    }

    public void clientPlantData() {
        Kv param = relationService.clientPlant();

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("data", param);

        renderJson(data);
    }

    public void clientVendor() {
        render("client_vendor.html");
    }

    public void clientVendorData() {
        Kv param = relationService.clientVendor();

        Kv data = new Kv();
        data.set("rtnCode", 0);
        data.set("data", param);

        renderJson(data);
    }
}
