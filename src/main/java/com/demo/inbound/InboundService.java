package com.demo.inbound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
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

        // 计数，求和
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

    Record matchedMat() {
        return Db.template("inbound.matchedMat").findFirst();
    }

    List<List<BigDecimal>> matSourceAmtBoxPlot() {
        List<Record> items = Db.template("inbound.matSourceAmt").find();

        List<List<BigDecimal>> results = Lists.newArrayList();

        // 供应来源只有3 种：1/2/3
        // 提取每一种对应的采购金额
        ImmutableList<Integer> grades = ImmutableList.of(1, 2, 3);
        for (Integer i : grades) {
            List<BigDecimal> line = Lists.newArrayList();

            for (Record r : items) {
                Integer c = r.getInt("vendorCount");
                if (c > i) {
                    break;
                }

                if (c.compareTo(i) == 0) {
                    line.add(r.getBigDecimal("totalAmt"));
                }
            }

            results.add(line);
        }

        return results;
    }

    List<Record> matSourceRatio() {
        List<Record> items = Db.template("inbound.matSource").find();

        // 计数、汇总
        int count = 0;
        double amount = 0.0;
        for (Record i : items) {
            count += i.getInt("matCount");
            amount += i.getDouble("totalAmt");
        }

        // 求占比
        for (Record i : items) {
            i.set("countRatio", i.getInt("matCount") * 100 / (count * 1.0));
            i.set("amtRatio", i.getDouble("totalAmt") * 100 / (amount * 1.0));
        }

        return items;
    }

    List<Record> plantAmt() {
        // 按收货工厂统计
        return Db.template("inbound.plantAmt").find();
    }

    List<Record> vendorAmtByPlant(String plant) {
        return Db.template("inbound.vendorAmtByPlant", plant).find();
    }

    List<Record> orderAmtByPlant(String plant) {
        return Db.template("inbound.orderAmtByPlant", plant).find();
    }

    List<Record> orderAmtByPlantVendor(String plant, String vendor) {
        return Db.template("inbound.orderAmtByPlantVendor", plant, vendor).find();
    }

    List<Record> matAmtByOrder(String orderNum) {
        return Db.template("inbound.matAmtByOrderNum", orderNum).find();
    }

    List<Record> itemByOrderMat(String order, String mat) {
        return Db.template("inbound.itemByOrderMat", order, mat).find();
    }

    List<Record> itemByOrderMatDay(String order, String mat) {
        return Db.template("inbound.itemByOrderMatDay", order, mat).find();
    }
}
