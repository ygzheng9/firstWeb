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
 */
public class VizService {
    private static Log log = Log.getLog(VizService.class);
    private static String NULL = "NULL";

    public List<PoHead> loadPoHeads() {
        List<PoHead> results = Lists.newArrayList();

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

                PoHead entry = new PoHead();
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

    public List<PoItem> loadPoItems() {
        List<PoItem> results = Lists.newArrayList();

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

                PoItem entry = new PoItem();
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

    public List<PoItem> findPOItemsByDate(List<PoItem> items, String start, String end) {
        List<PoItem> results = Lists.newArrayList();

        if (items == null || start == null || end == null) {
            return results;
        }

        for (PoItem i : items) {
            if (i.getPoDate().compareToIgnoreCase(start) >= 0 &&
                i.getPoDate().compareToIgnoreCase(end) <= 0) {
                results.add(i);
            }
        }

        return results;
    }

    public List<MatByMonth> loadMatByMonth() {
        List<MatByMonth> results = Lists.newArrayList();

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

                MatByMonth entry = new MatByMonth();
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


    public List<MatInfo> loadMatInfos() {
        List<MatInfo> results = Lists.newArrayList();

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

                MatInfo entry = new MatInfo();

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

    public static void main(String[] args) {
        BaseConfig.setupEnv();

        VizService svc = new VizService();
        List<PoHead> heads = svc.loadPoHeads();

        int i = 0;
        for (PoHead h : heads) {
            log.info(JsonKit.toJson(h));

            if (i >= 10) {
                break;
            }
            i++;
        }

        List<PoItem> items = svc.loadPoItems();
        i = 0;
        for (PoItem entry : items) {
            log.info(JsonKit.toJson(entry));

            if (i >= 10) {
                break;
            }
            i++;
        }


        List<MatByMonth> mats = svc.loadMatByMonth();
        i = 0;
        for (MatByMonth entry : mats) {
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

        List<MatInfo> infos = svc.loadMatInfos();
        i = 0;
        for (MatInfo entry : infos) {
            log.info(JsonKit.toJson(entry));

            if (i >= 10) {
                break;
            }
            i++;
        }
    }

}
