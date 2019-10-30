package com.demo.xstart;

import com.google.common.collect.Maps;
import com.jfinal.log.Log;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ygzheng
 */
public class RecordKit {
    private static Log log = Log.getLog(RecordKit.class);

    private List<String> fields;
    private Map<String, Integer> colNames;

    private RecordKit() {
    }

    public static RecordKit build(List<String> cols) {
        RecordKit instance = new RecordKit();

        instance.colNames = Maps.newHashMap();

        for (int i = 0; i < cols.size(); i++) {
            instance.colNames.put(cols.get(i), i);
        }

        return instance;
    }

    public void wrap(List<String> fs) {
        fields = fs;
    }

    public String getString(String col) {
        checkNotNull(fields, "fileds must NOT be null.");
        checkNotNull(colNames, "columns must NOT be null.");

        Integer idx = getIndex(col);

        if (idx >= fields.size()) {
            log.warn("not enough field: " + col);
            return "";
        }

        return fields.get(idx);
    }

    public Integer getInteger(String col) {
        return toInteger(getString(col));
    }

    public BigDecimal getDecimal(String col) {
        String s = getString(col);

        BigDecimal d = new BigDecimal(0.0);

        try {
            if (s.length() > 0) {
                s = s.replace(",", "");
                d = new BigDecimal(s);
            }
        } catch (NumberFormatException e) {
            log.warn("parse BigDecimal failed: " + col + " = " + s);
            e.printStackTrace();
        }

        return d;
    }

    private Integer getIndex(String col) {
        checkNotNull(colNames, "columns must NOT be null.");

        Integer idx = colNames.get(col);
        if (idx == null) {
            log.error("not in column names: " + col);
        }

        checkNotNull(idx, "index can not be null");

        return idx;
    }

    private Integer toInteger(String s) {
        checkNotNull(s, "input string can not be null");

        Integer c = 0;
        try {
            c = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return c;
    }
}
