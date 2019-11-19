package com.demo.inbound;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @author ygzheng
 */
public class InboundService {
    Record summary() {
        return Db.template("inbound.summary").findFirst();
    }

    List<Record> matSource() {
        List<Record> items = Db.template("inbound.matSource").find();

        int count = 0;
        double amount = 0.0;
        for (Record i : items) {
            i.set("avg", i.getDouble("totalAmt") / i.getDouble("matCount"));

            count += i.getInt("matCount");
            amount += i.getDouble("totalAmt");
        }

        Record r = new Record();
        r.set("vendorCount", "");
        r.set("matCount", count);
        r.set("totalAmt", amount);
        r.set("avg", amount / (count * 1.0));
        items.add(r);

        return items;
    }

}
