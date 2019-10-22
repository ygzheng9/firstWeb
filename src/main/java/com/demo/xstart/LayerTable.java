package com.demo.xstart;

import com.jfinal.plugin.activerecord.Page;
import lombok.Data;

import java.util.List;

/**
 * @author ygzheng
 */

@Data
public class LayerTable<T> {
    private int code;
    private String msg;
    private int count;
    private List<T> data;

    private LayerTable() {
    }

    static public <T> LayerTable<T> build(Page<T> pages) {
        LayerTable<T> result = new LayerTable<T>();
        result.setCode(0);
        result.setMsg("OK");
        result.setCount(pages.getTotalRow());
        result.setData(pages.getList());

        return result;
    }
}
