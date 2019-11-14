package com.demo.dataviz;

import com.demo.config.BaseConfig;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author ygzheng
 * 1. 从本地文件读取 *.dt，然后以 json 格式返回给前端
 * 2. 从本地文件读取 sales.xlsx 保存入数据库，然后从数据库中返回 json 给前台
 */
public class VizService {
    private static Log log = Log.getLog(VizService.class);
    private static String NULL = "NULL";

    public List<VizPoHead> loadPoHeads() {
        List<VizPoHead> results = Lists.newArrayList();

        String fname = PropKit.get("baseFolder") + "/zdata/raw_input/1.dt";


        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on("\t");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);

                String poDate = fields.get(2);
                if (poDate.length() > 10) {
                    poDate = poDate.substring(0, 10);
                }

                VizPoHead entry = new VizPoHead();
                entry.setPoID(fields.get(0));
                entry.setPoCode(fields.get(1));
                entry.setPoDate(poDate);
                entry.setVendorCode(fields.get(3));
                entry.setPersonCode(fields.get(4));

                results.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<VizPoItem> loadPoItems() {
        List<VizPoItem> results = Lists.newArrayList();

        String fname = PropKit.get("baseFolder") + "/zdata/raw_input/3.dt";

        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on("\t");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);

                String poDate = fields.get(11);
                if (poDate.length() > 10) {
                    poDate = poDate.substring(0, 10);
                }

                VizPoItem entry = new VizPoItem();
                entry.setItemID(fields.get(0));
                entry.setPoID(fields.get(1));
                entry.setInvCode(fields.get(2));

                entry.setQuantity(toDecimal(fields.get(3)));
                entry.setUnitPrice(toDecimal(fields.get(4)));
                entry.setNetAmt(toDecimal(fields.get(5)));
                entry.setTaxAmt(toDecimal(fields.get(6)));
                entry.setTotalAmt(toDecimal(fields.get(7)));

                entry.setProjCode(fields.get(8));
                entry.setProjName(fields.get(9));

                entry.setPoCode(fields.get(10));
                entry.setPoDate(poDate);
                entry.setVendorCode(fields.get(12));
                entry.setPersonCode(fields.get(13));

                results.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<VizPoItem> findPOItemsByDate(List<VizPoItem> items, String start, String end) {
        List<VizPoItem> results = Lists.newArrayList();

        if (items == null || start == null || end == null) {
            return results;
        }

        for (VizPoItem i : items) {
            if (i.getPoDate().compareToIgnoreCase(start) >= 0 &&
                i.getPoDate().compareToIgnoreCase(end) <= 0) {
                results.add(i);
            }
        }

        return results;
    }

    public List<VizMatByMonth> loadMatByMonth() {
        List<VizMatByMonth> results = Lists.newArrayList();

        String fname = PropKit.get("baseFolder") + "/zdata/raw_input/4.dt";

        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on("\t");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);

                VizMatByMonth entry = new VizMatByMonth();
                entry.setBizMonth(fields.get(0));
                entry.setInvCode(fields.get(1));

                entry.setQty(toDecimal(fields.get(2)));
                entry.setAmt(toDecimal(fields.get(3)));

                results.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<BomComponent> loadBomComponents() {
        List<BomComponent> results = Lists.newArrayList();

        String fname = PropKit.get("baseFolder") + "/zdata/raw_input/5.dt";

        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on("\t");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);

                BomComponent entry = new BomComponent();
                entry.setChildInv(fields.get(0));
                entry.setChildName(fields.get(1));
                entry.setParentInv(fields.get(2));
                entry.setParentName(fields.get(3));

                results.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }


    public List<VizMatInfo> loadMatInfos() {
        List<VizMatInfo> results = Lists.newArrayList();

        String fname = PropKit.get("baseFolder") + "/zdata/raw_input/6.dt";

        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on("\t");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);

                VizMatInfo entry = new VizMatInfo();

                entry.setInvCode(normalizeStr(fields.get(0)));
                entry.setInvName(normalizeStr(fields.get(1)));
                entry.setInvStd(normalizeStr(fields.get(2)));
                entry.setIsPurchase(toInteger(fields.get(3)));
                entry.setIsSelfMade(toInteger(fields.get(4)));

                entry.setMoQ(toDecimal(fields.get(5)));
                entry.setLeadTime(toDecimal(fields.get(6)));

                entry.setFileName(normalizeStr(fields.get(7)));
                entry.setVersion(normalizeStr(fields.get(8)));

                results.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private BigDecimal toDecimal(String s) {
        BigDecimal d = BigDecimal.valueOf(0);

        if (s == null) {
            return d;
        }

        if (s.compareToIgnoreCase(NULL) == 0) {
            return d;
        }


        try {
            if (s.length() > 0) {
                s = s.replace(",", "");
                d = new BigDecimal(s);
            }
        } catch (NumberFormatException e) {
            log.warn("parse BigDecimal failed: " + s);
            e.printStackTrace();
        }

        return d;
    }

    private Integer toInteger(String s) {
        Integer c = 0;
        if (s == null) {
            return c;
        }

        try {
            c = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return c;
    }

    private String normalizeStr(String s) {
        if (s.compareToIgnoreCase(NULL) == 0) {
            return "";
        }
        return s;
    }

    private static void testBatch1() {
        VizService svc = new VizService();
        List<VizPoHead> heads = svc.loadPoHeads();

        int i = 0;
        for (VizPoHead h : heads) {
            log.info(JsonKit.toJson(h));

            if (i >= 10) {
                break;
            }
            i++;
        }

        List<VizPoItem> items = svc.loadPoItems();
        i = 0;
        for (VizPoItem entry : items) {
            log.info(JsonKit.toJson(entry));

            if (i >= 10) {
                break;
            }
            i++;
        }


        List<VizMatByMonth> mats = svc.loadMatByMonth();
        i = 0;
        for (VizMatByMonth entry : mats) {
            log.info(JsonKit.toJson(entry));

            if (i >= 10) {
                break;
            }
            i++;
        }

        List<BomComponent> comps = svc.loadBomComponents();
        i = 0;
        for (BomComponent entry : comps) {
            log.info(JsonKit.toJson(entry));

            if (i >= 10) {
                break;
            }
            i++;
        }

        List<VizMatInfo> infos = svc.loadMatInfos();
        i = 0;
        for (VizMatInfo entry : infos) {
            log.info(JsonKit.toJson(entry));

            if (i >= 10) {
                break;
            }
            i++;
        }
    }


    public static void main(String[] args) {
        BaseConfig.setupEnv();
        // testBatch1();

        VizService svc = new VizService();
    }

}
