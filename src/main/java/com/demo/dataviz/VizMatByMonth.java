package com.demo.dataviz;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ygzheng
 */
@Data
public class VizMatByMonth {
    private String bizMonth;
    private String invCode;
    private BigDecimal qty;
    private BigDecimal amt;
}
