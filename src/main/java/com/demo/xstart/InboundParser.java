package com.demo.xstart;

import cn.hutool.core.util.StrUtil;
import com.demo.model.PoHead;
import com.demo.model.PoItem;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jfinal.log.Log;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ygzheng
 */

@Data
public class InboundParser {
    private static Log log = Log.getLog(InboundParser.class);

    public enum State {
        // 未进入订单
        NA,
        // 进入订单头
        HEAD,
        // 进入订单行项目
        BODY,
    }

    private int pos;
    private int maxPos;
    private State state;
    private List<String> lines = Lists.newArrayList();

    private InboundParser() {
    }

    public static InboundParser build(List<String> items) {
        InboundParser p = new InboundParser();

        p.setState(State.NA);
        p.setLines(items);

        p.setMaxPos(items.size() - 1);
        // 初始是 0，在使用前，需要 nextToken()
        p.setPos(-1);

        return p;
    }

    public String current() {
        if (pos > maxPos) {
            return "EOFEOF";
        }

        return lines.get(pos);
    }

    public boolean hasNext() {
        // 最后一个是没有下一个的，所以最大是倒数第二个
        return pos < maxPos;
    }

    public boolean nextToken() {
        if (hasNext()) {
            pos += 1;
            skipBlank();

            // skipPageBreak();

            return true;
        } else {
            log.warn("Already reached end.");

            return false;
        }
    }

    public boolean shouldIgnore(String line) {
        // 进度订单后，如果出现了分页
        ImmutableList<String> l = ImmutableList.of("采购收货报表", "收货单");
        for (String a : l) {
            if (line.contains(a) && this.getState() == State.HEAD) {
                return true;
            }
        }

        return false;
    }

    public void skipBlank() {
        // 这里直接操作 pos，没有使用 nextToken，否则是嵌套的递归调用

        // 跳过所有的空行，最后停留在第一个非空行
        String l = current();

        if (l.trim().length() == 0) {
            moveDown(1);
            return;
        }

        // if (state == State.HEAD) {
        if (l.contains("采购收货报表")) {
            moveDown(2);
        } else if (l.contains("收货单")) {
            moveDown(3);
            }
        // }


        // while (l.trim().length() == 0 || shouldIgnore(l)) {
        //     if (hasNext()) {
        //         if (l.trim().length() == 0) {
        //             moveDown(1);
        //         } else if (l.contains("采购收货报表")) {
        //             moveDown(2);
        //         } else if (l.contains("收货单")) {
        //             moveDown(3);
        //         }
        //
        //         l = current();
        //
        //     } else {
        //         break;
        //     }
        // }
    }

    public void moveDown(int n) {
        for (int c = 0; c < n; ) {
            pos += 1;
            if (pos >= maxPos) {
                log.info("moveDown reach last line.");
                break;
            }

            String s = current();
            if (s.trim().length() > 0) {
                c += 1;
            }
        }
    }

    public List<String> skipExchangeRate() {
        List<String> result = Lists.newArrayList("", "", "", "");

        String l = current();
        if (l.trim().startsWith("兑换率")) {
            result = splitExchangeRate(l);

            // 跳过汇率行
            nextToken();
        }

        return result;
    }

    public boolean isOrderEnd() {
        String c = current();
        // 最后一行
        if (pos == maxPos) {
            state = State.NA;
            return true;
        }

        // 原始文件中，订单行结束，有多种格式
        ImmutableList<String> stops = ImmutableList.of("---------", "基本PO合计", "采购订单");

        // 当前行（之前是汇率行，会向前进一行）或下一行（之前不是汇率行，没有向前进），
        String l = lines.get(pos + 1);
        for (String s : stops) {
            if (c.trim().startsWith(s) || l.trim().startsWith(s)) {
                state = State.NA;

                return true;
            }
        }

        return false;
    }

    public boolean isOrderStart() {
        String l = current();
        if (l.trim().startsWith("采购订单")) {
            state = State.HEAD;
            return true;
        }

        return false;
    }
    //
    // public void skipPageBreak() {
    //     String c = current();
    //     String p1 = "采购收货报表";
    //     if (c.contains(p1)) {
    //         // nextToken();
    //         // nextToken();
    //
    //         pos += 1;
    //         skipBlank();
    //
    //         pos += 1;
    //         skipBlank();
    //
    //         c = current();
    //         String p2 = "收货单";
    //         if (c.trim().startsWith(p2)) {
    //             for (int i = 1; i <= 3; i++) {
    //                 // nextToken();
    //
    //                 pos += 1;
    //                 skipBlank();
    //             }
    //         }
    //     }
    // }


    public List<String> parseHead() {
        List<String> result = Lists.newArrayList();

        while (!isOrderStart()) {
            if (hasNext()) {
                nextToken();
            } else {
                log.info("no head found.");
                return result;
            }
        }

        // pos 指向：采购订单 交货       供应商
        // 移动到下一行：  -------------
        // 再移动到下一行： IP9339DJ            IP9339D  埃意(廊坊)电子工程有限
        nextToken();
        nextToken();
        String l = current();

        result = splitHead(l);

        setState(State.HEAD);

        return result;
    }

    public List<List<String>> parseItems() {
        List<List<String>> orderItems = Lists.newArrayList();

        // 在表头也会有分页，所以直接通过字符查找；
        String c = current();
        while (!c.startsWith("装箱单号")) {
            nextToken();
            c = current();
        }
        moveDown(2);

        setState(State.BODY);

        int maxItem = 100000;
        int idx = 0;

        while (true) {
            // 一个行项目，是拆分两行显示，需要再重新拼接到一行；
            // 行项目第一行
            String l = current();
            List<String> l1 = splitLineOne(l);

            nextToken();
            l = current();
            // 行项目第二行，
            List<String> l2 = splitLineTwo(l);

            // 跳过汇率行
            nextToken();
            List<String> l3 = skipExchangeRate();

            // 两行合并成一行
            List<String> joins = Lists.newArrayList();
            joins.addAll(l1);
            joins.addAll(l2);
            joins.addAll(l3);
            orderItems.add(joins);

            // 订单结尾的汇总
            if (isOrderEnd()) {
                setState(State.NA);
                break;
            }

            idx += 1;
            if (idx >= maxItem) {
                log.warn("item count exceeds max");
                break;
            }
        }

        return orderItems;
    }

    public List<String> splitHead(String s) {
        // 根据输入文件的格式定义, 订单行项目第一行
        ImmutableList<Integer> positions = ImmutableList.of(0, 19, 28, 59, 130);

        return doSplit(s, positions);
    }

    public List<String> splitLineOne(String s) {
        // 根据输入文件的格式定义, 订单行项目第一行
        // 115,
        ImmutableList<Integer> positions = ImmutableList.of(0, 20, 24, 43, 53, 76, 78, 91, 93, 109, 125, 138);

        return doSplit(s, positions);
    }

    public List<String> splitLineTwo(String s) {
        // 根据输入文件的格式定义, 订单行项目第二行
        ImmutableList<Integer> positions = ImmutableList.of(0, 20, 43, 53, 76, 91, 125);

        return doSplit(s, positions);
    }


    @Deprecated
    public List<String> splitExchangeRateRaw(String s) {
        // 根据输入文件的格式定义, 订单行项目第二行
        ImmutableList<Integer> positions = ImmutableList.of(4, 7, 11, 13, 17, 24);

        return doSplit(s, positions);
    }

    public List<String> splitExchangeRate(String s) {
        List<String> result = Lists.newArrayList();

        // 固定格式：兑换率:EUR 1.0 = CNY 8.0111
        List<String> l1 = Splitter.on(":").trimResults().splitToList(s);
        List<String> l2 = Splitter.on("=").trimResults().splitToList(l1.get(1));

        Splitter splt = Splitter.on(" ").trimResults();
        for (String s1 : l2) {
            List<String> a = splt.splitToList(s1);
            result.addAll(a);
        }

        return result;
    }

    private List<String> doSplit(String input, List<Integer> positions) {
        Preconditions.checkNotNull(input, "input string can not be null");
        Preconditions.checkNotNull(positions, "position can not be null");

        List<String> result = Lists.newArrayList();

        // 生成(from, to)序列对，用以取 sub,注意：前后下标是需要重叠的
        List<Integer> from = Lists.newArrayList(positions);
        List<Integer> to = Lists.newArrayList(positions);
        from.remove(positions.size() - 1);
        to.remove(0);

        int strLength = input.length();
        for (int i = 0; i < from.size(); i++) {
            int f = from.get(i);
            int t = to.get(i);

            if (f > strLength) {
                // 起始位置超出总长度
                result.add("");

                String template = "({}, {}) is empty: {}";
                log.debug(StrUtil.format(template, f, t, input));
            } else if (t > strLength) {
                // 结束位置超出总长度
                String s = StrUtil.sub(input, f, strLength).trim();
                result.add(s);

                String template = "({}, {}) not enough long: {}";
                log.debug(StrUtil.format(template, f, t, input));
            } else {
                // 在总长度之内
                String s = StrUtil.sub(input, f, t).trim();
                result.add(s);
            }
        }

        return result;
    }

    public PoHead convertToPoHead(List<String> line) {
        List<String> columnNames = ImmutableList.of("orderNum", "vendorCode", "vendorName", "project");
        RecordKit record = RecordKit.build(columnNames);

        record.wrap(line);

        PoHead h = new PoHead();
        h.setOrderNum(record.getString("orderNum"));
        h.setVendorCode(record.getString("vendorCode"));
        h.setVendorName(record.getString("vendorName"));
        h.setProject(record.getString("project"));

        return h;
    }

    public List<PoItem> convertToPoItem(String orderNum, List<List<String>> lines) {
        List<PoItem> items = Lists.newArrayList();

        List<String> columnNames = ImmutableList.of(
            "ibOrderNum",
            "ibOrderLine",
            "material",
            "ibDate",
            "receivedQuantity",
            "indicator10",
            "unitCost10",
            "indicator20",
            "totalAmt10",
            "compareAmt",
            "diffAmt",

            "pickupOrderNum",
            "pickupOrderLine",
            "pickupDate",
            "pickupQuantity",
            "unitCost20",
            "totalAmt20",

            "fromCurrency",
            "fromAmt",
            "toCurrency",
            "toAmt");
        RecordKit record = RecordKit.build(columnNames);

        int k = 0;
        for (List<String> l : lines) {
            record.wrap(l);
            PoItem i = new PoItem();

            String s = record.getString("pickupQuantity");
            try {
                if (s.length() > 0) {
                    s = s.replace(",", "");
                    BigDecimal d = new BigDecimal(s);
                }
            } catch (NumberFormatException e) {
                log.warn("parse BigDecimal failed: " + orderNum + " : " + k + " " + l);
                System.out.println(lines.get(k));
                System.out.println(lines.get(k + 1));
                System.out.println(lines.get(k + 2));

                e.printStackTrace();

                break;
            }

            k++;


            // 入库单号
            i.setOrderNum(orderNum);

            // 原始记录中第一行
            i.setIbOrderNum(record.getString("ibOrderNum"));
            i.setIbOrderLine(record.getString("ibOrderLine"));
            i.setMaterial(record.getString("material"));
            i.setIbDate(record.getString("ibDate"));

            i.setReceivedQuantity(record.getDecimal("receivedQuantity"));
            i.setIndicator10(record.getString("indicator10"));
            i.setUnitCost10(record.getDecimal("unitCost10"));
            i.setIndicator20(record.getString("indicator20"));
            i.setTotalAmt10(record.getDecimal("totalAmt10"));
            i.setCompareAmt(record.getDecimal("compareAmt"));
            i.setDiffAmt(record.getDecimal("diffAmt"));

            // 原始记录中第二行
            i.setPickupOrderNum(record.getString("pickupOrderNum"));
            i.setPickupOrderLine(record.getString("pickupOrderLine"));
            i.setPickupDate(record.getString("pickupDate"));
            i.setPickupQuantity(record.getDecimal("pickupQuantity"));
            i.setUnitCost20(record.getDecimal("unitCost20"));
            i.setTotalAmt20(record.getDecimal("totalAmt20"));

            // 原始记录只用第三行：有可能有，也可能没有，汇率
            i.setFromCurrency(record.getString("fromCurrency"));
            i.setFromAmt(record.getDecimal("fromAmt"));
            i.setToCurrency(record.getString("toCurrency"));
            i.setToAmt(record.getDecimal("toAmt"));

            items.add(i);

        }

        return items;

    }
}
