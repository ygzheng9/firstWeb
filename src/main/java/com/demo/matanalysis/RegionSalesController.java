package com.demo.matanalysis;

import com.demo.model.RegionSalesStats;
import com.google.common.collect.ImmutableList;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ygzheng
 */
public class RegionSalesController extends Controller {
    private static Log log = Log.getLog(RegionSalesController.class);
    private static int StatusOK = 200;

    @Inject
    private RegionSalesService svc;

    public void regionData() {
        String year = get("year");
        List<RegionSalesStats> items = svc.getRegionSales(year);

        // 汇总销量
        BigDecimal total = BigDecimal.ZERO;
        for (RegionSalesStats i : items) {
            total = total.add(i.getQuantity2());
        }

        ImmutableList<String> fields = ImmutableList.of("id", "year", "region", "quantity1", "quantity2", "quantity3");
        List<List<Object>> source = svc.toDataset(items, fields);

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("source", source);
        data.set("dims", fields);
        data.set("total", total);

        renderJson(data);
    }

    public void regions() {
        render("regionsales.html");
    }
}
