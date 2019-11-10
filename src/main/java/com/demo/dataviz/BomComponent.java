package com.demo.dataviz;

import lombok.Data;

/**
 * @author ygzheng
 * BOM 的层级关系：子件号，父件号
 */
@Data
public class BomComponent {
    private String childInv;
    private String childName;
    private String parentInv;
    private String parentName;
}
