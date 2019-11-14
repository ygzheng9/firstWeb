package com.demo.matanalysis;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.excel.EasyExcel;
import com.demo.dataviz.RegionSalesRaw;
import com.demo.dataviz.RegionSalesRawListener;
import com.demo.model.RegionSalesStats;
import com.google.common.collect.Lists;
import com.jfinal.kit.PropKit;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ygzheng
 */
public class RegionSalesService {
    private void loadSales() {
        // 从 excel 中读取区域销售记录，逐行解析，最后再保存到数据库中；
        String fileName = PropKit.get("baseFolder") + "/zdata/pd_sales.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, RegionSalesRaw.class, new RegionSalesRawListener()).sheet().doRead();
    }

    protected List<RegionSalesStats> getRegionSales(String year) {
        RegionSalesStats dao = new RegionSalesStats().dao();
        return dao.template("mat.getRegionSales", year).find();
    }

    protected <T> List<List<Object>> toDataset(List<T> items, List<String> fields) {
        // echart.dataset 的数据格式是 db 中的 table，每列对应一个属性；
        // 把 java 的对象的属性，转变成数组，数组内元素类型可以不同；

        List<List<Object>> source = Lists.newArrayList();
        for (T s : items) {
            List<Object> line = Lists.newArrayList();

            for (String f : fields) {
                String methodName = "get" + f;
                Object v = "";
                try {
                    Method m = ReflectUtil.getMethodIgnoreCase(s.getClass(), methodName);
                    if (m != null) {
                        v = ReflectUtil.invoke(s, m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                line.add(v);
            }

            source.add(line);
        }

        return source;
    }

}
