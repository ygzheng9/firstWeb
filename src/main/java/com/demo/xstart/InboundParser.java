package com.demo.xstart;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jfinal.log.Log;
import lombok.Data;

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
        // 进入订单
        Body
    }

    private int pos;
    private int maxPos;
    private State state;
    private List<String> lines = Lists.newArrayList();
    private Splitter splitter;

    private InboundParser() {
    }

    public static InboundParser build(List<String> items) {
        InboundParser p = new InboundParser();

        p.setState(State.NA);
        p.setLines(items);
        p.setSplitter(Splitter.on(" "));

        p.setMaxPos(items.size() - 1);
        // 初始是 0，在使用前，需要 nextToken()
        p.setPos(-1);

        return p;
    }

    public String current() {
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

            return true;
        } else {
            log.warn("Already reached end.");

            return false;
        }
    }

    public void skipBlank() {
        // 跳过所有的空行，最后停留在第一个非空行
        String l = current();
        while (l.trim().length() == 0) {
            if (hasNext()) {
                // 这里直接操作 pos，没有使用 nextToken，否则是嵌套的递归调用
                pos += 1;
                l = current();
            } else {
                break;
            }
        }
    }

    public List<String> skipExchangeRate() {
        List<String> result = Lists.newArrayList("", "", "", "");

        String l = current();
        if (l.trim().startsWith("兑换率")) {
            log.info("got：" + l);

            result = splitExchangeRate(l);

            // 跳过汇率行
            nextToken();
        }

        return result;
    }


    public boolean isOrderEnd() {
        String l = current();
        if (l.trim().startsWith("-----")) {
            state = State.NA;

            return true;
        }
        return false;
    }

    public boolean isOrderStart() {
        String l = current();
        if (l.trim().startsWith("采购订单")) {
            state = State.Body;
            return true;
        }

        return false;
    }


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
        // 移动到下一行  -------------
        // 再移动到下一行 IP9339DJ            IP9339D  埃意(廊坊)电子工程有限
        nextToken();
        nextToken();
        String l = current();

        result = splitter.splitToList(l);
        return result;
    }

    public List<List<String>> parseItems() {
        List<List<String>> orderItems = Lists.newArrayList();

        nextToken();
        nextToken();
        nextToken();
        nextToken();

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

    public List<String> splitLineOne(String s) {
        // 根据输入文件的格式定义, 订单行项目第一行
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

    public List<String> doSplit(String input, List<Integer> positions) {
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

            if (t > strLength) {
                String template = "({}, {}) not enough long: {}";
                log.warn(StrUtil.format(template, f, t, input));
                break;
            }

            String s = StrUtil.sub(input, f, t).trim();
            result.add(s);
        }

        return result;
    }
}
