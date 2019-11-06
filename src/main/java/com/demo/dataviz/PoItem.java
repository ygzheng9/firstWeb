package com.demo.dataviz;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PoItem {
    private String itemID;
    private String poID;
    private String invCode;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal netAmt;
    private BigDecimal taxAmt;
    private BigDecimal totalAmt;
    private String projCode;
    private String projName;
    private String poCode;
    private String poDate;
    private String vendorCode;
    private String personCode;
}
