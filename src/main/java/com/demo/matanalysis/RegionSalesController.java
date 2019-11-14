package com.demo.matanalysis;

import com.demo.model.RegionSales;
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

        Kv data = svc.toDataset(items, fields, false);
        renderJson(data);
    }

    public void regions() {
        render("regionsales.html");
    }

    public void cityData() {
        String year = get("year");
        String region = get("region");

        List<RegionSales> items = svc.getCitySales(year, region);
        ImmutableList<String> fields = ImmutableList.of("id", "year", "region", "city", "quantity");

        Kv data = svc.toDataset(items, fields, true);
        renderJson(data);
    }
}
