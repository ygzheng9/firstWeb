package com.demo.okadmin;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ygzheng
 * 这些数据都是不变的：不需要查询参数，也不会对数据进行 Insert/Update/delete，所以可以做缓存 --> 等幂
 */
public class RelationService {
    private Kv vendorPlantData = null;
    private Kv clientPlantData = null;
    private Kv clientVendorData = null;

    Kv vendorPlant() {
        if (vendorPlantData == null) {
            List<Record> vendorAmt = Db.template("relation.vendorAmt").find();
            List<Record> plantAmt = Db.template("relation.plantAmt").find();
            List<Record> vendorPlant = Db.template("relation.vendorPlant").find();

            List<Record> nodes = new ArrayList<>();
            for (Record r : vendorAmt) {
                Record n = new Record();
                n.set("name", r.getStr("vendorName"));
                n.set("amt", r.getDouble("totalAmt") / 10000.0);
                n.set("category", r.getStr("category"));
                nodes.add(n);
            }

            for (Record r : plantAmt) {
                Record n = new Record();
                n.set("name", r.getStr("toPlant"));
                n.set("amt", r.getDouble("totalAmt") / 10000.0);
                n.set("category", r.getStr("category"));
                nodes.add(n);
            }

            List<Record> links = new ArrayList<>();
            for (Record r : vendorPlant) {
                Record l = new Record();
                l.set("source", r.getStr("vendorName"));
                l.set("target", r.getStr("toPlant"));
                links.add(l);
            }

            vendorPlantData = new Kv();
            vendorPlantData.set("nodes", nodes);
            vendorPlantData.set("links", links);
        }

        return vendorPlantData;
    }

    Kv clientPlant() {
        if (clientPlantData == null) {
            List<Record> nodes = Db.template("relation.clientPlantNodes").find();
            List<Record> links = Db.template("relation.clientPlantLinks").find();

            clientPlantData = new Kv();
            clientPlantData.set("nodes", nodes);
            clientPlantData.set("links", links);
        }

        return clientPlantData;
    }

    Kv clientVendor() {
        if (clientVendorData == null) {
            List<Record> nodes = Db.template("relation.clientVendorNodes").find();
            List<Record> links = Db.template("relation.clientVendorLinks").find();

            clientVendorData = new Kv();
            clientVendorData.set("nodes", nodes);
            clientVendorData.set("links", links);
        }

        return clientVendorData;
    }
}
