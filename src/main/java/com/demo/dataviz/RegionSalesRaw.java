package com.demo.dataviz;

import lombok.Data;

/**
 * @author ygzheng
 * 1. 和本地 excel 文件列对应，最直接都用 String，然后在 listener.invoke 中做格式转换
 */
@Data
public class RegionSalesRaw {
    private String region;
    private String city;
    private String qty16;
    private String qty17;
}
