package com.demo.dataviz;

import lombok.Data;

import java.math.BigDecimal;


/**
 * @author ygzheng
 */
@Data
public class VizMatInfo {
    private String invCode;
    private String invName;
    private String invStd;
    private Integer isPurchase;
    private Integer isSelfMade;
    private Integer isProxy;
    private BigDecimal moQ;
    private BigDecimal leadTime;
    private String fileName;
    private String version;
}
