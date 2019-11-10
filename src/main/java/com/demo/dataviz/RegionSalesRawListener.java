package com.demo.dataviz;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.demo.model.RegionSales;
import com.demo.model.RegionSalesStats;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.jfinal.log.Log;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static java.math.RoundingMode.HALF_DOWN;

/**
 * @author ygzheng
 * 1. 读取 excel 的回调，每一行的回调，以及所有都读取完毕后的回调
 */
public class RegionSalesRawListener extends AnalysisEventListener<RegionSalesRaw> {
    private static Log log = Log.getLog(RegionSalesRawListener.class);

    private List<RegionSales> sales = Lists.newArrayList();

    private String batch = "20191111";

    @Override
    public void invoke(RegionSalesRaw regionSalesRaw, AnalysisContext analysisContext) {
        RegionSales s1 = new RegionSales();
        s1.setRegion(regionSalesRaw.getRegion());
        s1.setCity(regionSalesRaw.getCity());
        s1.setYear("2016");
        try {
            BigDecimal d1 = new BigDecimal(regionSalesRaw.getQty16());
            s1.setQuantity(d1);
        } catch (NumberFormatException e) {
            s1.setQuantity(BigDecimal.ZERO);
        }

        sales.add(s1);

        RegionSales s2 = new RegionSales();
        s2.setRegion(regionSalesRaw.getRegion());
        s2.setCity(regionSalesRaw.getCity());
        s2.setYear("2017");
        try {
            BigDecimal d2 = new BigDecimal(regionSalesRaw.getQty17());
            s2.setQuantity(d2);
        } catch (NumberFormatException e) {
            s2.setQuantity(BigDecimal.ZERO);
        }

        sales.add(s2);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("total sales: " + sales.size());

        // save to db;
        for (RegionSales s : sales) {
            /// log.info(JsonKit.toJson(s));
            s.setBatch(batch);
            s.save();
        }

        calcStats(sales);
    }

    private void calcStats(List<RegionSales> inputs) {
        // 按照 (year, region) 计算 汇总，最大和后面和的比例
        Multimap<String, BigDecimal> amap = ArrayListMultimap.create();
        for (RegionSales s : inputs) {
            String key = s.getYear() + "|" + s.getRegion();
            amap.put(key, s.getQuantity());
        }

        List<RegionSalesStats> results = Lists.newArrayList();
        Splitter splter = Splitter.on("|");
        for (String k : amap.keySet()) {
            List<String> fields = splter.splitToList(k);

            List<BigDecimal> v = Lists.newArrayList(amap.get(k));

            RegionSalesStats s = new RegionSalesStats();
            s.setYear(fields.get(0));
            s.setRegion(fields.get(1));

            s.setQuantity1(sum(v));
            s.setQuantity2(ratio(v, 2));

            results.add(s);
        }

        log.info("total stats: " + results.size());
        for (RegionSalesStats s : results) {
            /// log.info(JsonKit.toJson(s));
            s.setBatch(batch);
            s.save();
        }
    }

    private BigDecimal sum(List<BigDecimal> list) {
        // 累加和
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal b : list) {
            total = total.add(b);
        }
        return total;
    }

    private BigDecimal ratio(List<BigDecimal> list, int stop) {
        // 最大的 和 后面的第n大和 的比值
        int baseline = 2;
        int s = list.size();
        if (s < baseline) {
            return BigDecimal.ONE;
        }

        list.sort(Collections.reverseOrder());

        // a 是最大的，b 是后面累加的和
        BigDecimal a = list.get(0);
        BigDecimal b = list.get(1);
        for (int i = baseline; i < stop; i++) {
            if (i >= s) {
                break;
            }

            b = b.add(list.get(i));
        }

        if (b.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }

        return a.divide(b, HALF_DOWN);
    }
}
