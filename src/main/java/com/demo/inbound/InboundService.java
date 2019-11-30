package com.demo.inbound;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author ygzheng
 */
public class InboundService {
    // 全局数据，只加载一次
    private List<Record> amtByVendorData = null;

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

    List<Record> matMultiSource() {
        return Db.template("inbound.matMultiSource").find();
    }

    List<Record> matMultiSourceVendors(String mat) {
        return Db.template("inbound.matMultiSourceVendors", mat).find();
    }

    List<Record> matMultiSourceIBItems(String mat) {
        return Db.template("inbound.matMultiSourceIBItems", mat).find();
    }

    List<Record> matMultiSourcePlants(String mat) {
        return Db.template("inbound.matMultiSourcePlants", mat).find();
    }

    List<Record> matMultiSourceIBItemsByPlant(String mat, String plant) {
        return Db.template("inbound.matMultiSourceIBItemsByPlant", mat, plant).find();
    }

    List<Record> bomByMat(String matCode) {
        return Db.template("inbound.bomByMat", matCode).find();
    }

    List<Record> amtByVendor() {
        if (amtByVendorData == null) {
            amtByVendorData = amtByVendorGrade(Db.template("inbound.amtByVendor").find());
        }

        return amtByVendorData;
    }

    private List<Record> amtByVendorGrade(List<Record> items) {
        // 计算汇总金额
        Double total = 0.0;
        for (Record r : items) {
            total += r.getDouble("totalAmt");

            r.set("cumAmt", total);
        }

        // 计算累计比例
        for (Record r : items) {
            Double pect = r.getDouble("cumAmt") * 100.0 / total;
            r.set("pect", pect);
        }

        // 累计比例分组
        ImmutableList<Double> grades = ImmutableList.of(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 1000.0);
        int j = 0;
        for (int i = 0; i < grades.size(); i++) {
            Double g = grades.get(i);
            /// int label = g.intValue();
            int label = (i + 1) * 10;

            while (j < items.size()) {
                Record r = items.get(j);

                if (r.getDouble("pect") < g) {
                    r.set("grade", label);

                    j++;
                } else {
                    break;
                }
            }
        }

        return items;
    }

    List<Record> countByGrade(List<Record> items) {
        Multiset<Integer> countMap = HashMultiset.create();
        for (Record r : items) {
            countMap.add(r.getInt("grade"));
        }

        List<Record> results = new ArrayList<>();
        for (Multiset.Entry entry : countMap.entrySet()) {
            Record r = new Record();
            r.set("grade", entry.getElement());
            r.set("count", entry.getCount());

            results.add(r);
        }

        Collections.sort(results, Comparator.comparingInt(a -> a.getInt("grade")));

        Integer total = 0;
        for (Record r : results) {
            total += r.getInt("count");
        }

        for (Record r : results) {
            r.set("pect", r.getInt("count") * 100.0 / total);
        }

        return results;
    }

    List<Record> drillByGrade(int grade) {
        List<Record> results = new ArrayList<>();

        for (Record r : amtByVendorData) {
            if (r.getInt("grade") == grade) {
                results.add(r);
            }
        }

        return results;
    }

    List<Record> amtByVendorPlant(String vendor) {
        List<Record> items = Db.template("inbound.amtByVendorPlant", vendor).find();
        return items;
    }
}
