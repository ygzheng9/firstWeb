package com.demo.dataviz;

import lombok.Data;

/**
 * @author ygzheng
 */
@Data
public class BomComponent {
    private String childInv;
    private String childName;
    private String parentInv;
    private String parentName;
}
