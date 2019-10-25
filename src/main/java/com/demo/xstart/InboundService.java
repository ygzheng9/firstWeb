package com.demo.xstart;

import com.demo.config.BaseConfig;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ygzheng
 */
public class InboundService {
    private static Log log = Log.getLog(InboundService.class);

    public enum state {
        // 不在处理范围
        NA,
        // 订单行项目
        Body
    }

    @Deprecated
    public void loadDataRaw() {
        String fileName = PropKit.get("baseFolder") + "/zdata/ib_all.txt";

        List<List<String>> headList = Lists.newArrayList();
        Map<String, List<List<String>>> items = Maps.newHashMap();

        try {
            File infile = new File(fileName);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter sep = Splitter.on(" ");

            state s = state.NA;
            int total = lines.size();
            int idx = 0;
            while (idx < total) {
                if (s.equals(state.NA)) {
                    String line = lines.get(idx);
                    if (line.trim().startsWith("采购订单")) {
                        // 订单头
                        idx = idx + 2;
                        line = lines.get(idx);
                        List<String> fields = sep.splitToList(line);
                        headList.add(fields);

                        String orderHead = fields.get(0);
                        List<List<String>> orderItems = Lists.newArrayList();

                        // 订单行项目
                        s = state.Body;
                        // 跳过报表中的表头
                        idx += 4;
                        line = lines.get(idx);

                        while (s.equals(state.Body)) {
                            // 一个行项目，是拆分成两行显示，需要再重新拼接到一行；
                            // 行项目第一行
                            List<String> l1 = sep.splitToList(line);

                            // 行项目第二行，需要跳过空行
                            idx += 1;
                            line = lines.get(idx);
                            while (line.trim().length() == 0) {
                                idx += 1;
                                line = lines.get(idx);
                            }

                            List<String> l2 = sep.splitToList(line);
                            // 第二行的第一列如果为空，那么补上
                            if (l2.size() == 5) {
                                l2.add(0, "");
                            }

                            // 两行合并成一行
                            List<String> joins = Lists.newArrayList();
                            joins.addAll(l1);
                            joins.addAll(l2);
                            orderItems.add(Lists.newArrayList(joins));

                            // 前进一行
                            idx += 1;
                            line = lines.get(idx);

                            // 跳过汇率行
                            if (line.trim().startsWith("兑换率")) {
                                idx += 1;
                                line = lines.get(idx);
                            }

                            // 跳过空行
                            while (line.trim().length() == 0) {
                                idx += 1;
                                line = lines.get(idx);
                            }

                            // 订单结尾的汇总
                            if (line.trim().startsWith("----")) {
                                s = state.NA;

                                break;
                            }
                        }

                        items.put(orderHead, orderItems);

                        log.info("order: " + orderHead + " with items: " + orderItems.size());
                    }
                }
                idx += 1;
            }


            log.info("total order: " + items.size() + " with lines: " + lines.size());
        } catch (
            IOException e) {
            e.printStackTrace();
        }
    }


    public void loadData() {
        String fileName = PropKit.get("baseFolder") + "/zdata/ib_test2.txt";

        List<List<String>> headList = Lists.newArrayList();
        Map<String, List<List<String>>> items = Maps.newHashMap();

        try {
            File infile = new File(fileName);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);
            InboundParser p = InboundParser.build(lines);

            while (p.hasNext()) {
                p.nextToken();

                List<String> fields = p.parseHead();
                if (!p.hasNext()) {
                    break;
                }

                List<List<String>> orderItems = p.parseItems();

                headList.add(fields);
                String orderHead = fields.get(0);
                items.put(orderHead, orderItems);

                log.info("order: " + orderHead + " with items: " + orderItems.size());
                for (List<String> l : orderItems) {
                    System.out.println(l);
                }
            }

            log.info("total order: " + headList.size() + " with lines: " + lines.size());

        } catch (
            IOException e) {
            e.printStackTrace();
        }
    }

    public void trySplitter() {
        //根据分隔符进行分割
        String sequence = "a,,-b, c,-d";
        Iterable<String> iterator1 = Splitter.on(",").split(sequence);
        System.out.println("根据分隔符进行分割:");
        for (String str : iterator1) {
            System.out.println(str);
        }

        //去掉分割后空的字符串
        Iterable<String> iterator2 = Splitter.on(",").omitEmptyStrings().split(sequence);
        System.out.println("去掉分割后空的字符串:");
        for (String str : iterator2) {
            System.out.println(str);
        }

        //去掉分后后字符串中的空格
        Iterable<String> iterator3 = Splitter.on(",").omitEmptyStrings().trimResults().split(sequence);
        System.out.println("去掉分后后字符串中的空格:");
        for (String str : iterator3) {
            System.out.println(str);
        }
        Iterable<String> iterator5 = Splitter.on(",").omitEmptyStrings().trimResults(CharMatcher.is('-')).split(sequence);
        System.out.println("去掉分后后字符串中'-':");
        for (String str : iterator5) {
            System.out.println(str);
        }

        //以固定长度进行分割
        //Iterable iterator4=Splitter.fixedLength(2).split(sequence);
        Iterable<String> iterator4 = Splitter.fixedLength(2).trimResults().split(sequence);
        System.out.println("以固定长度进行分割:");
        for (String str : iterator4) {
            System.out.println(str);
        }

        //Splitter将处理结果处理成map类型
        Map<String, String> map = Splitter.on(";").omitEmptyStrings().withKeyValueSeparator(",").split("a,c;quzer,yuanrq; , ;hello,csdn");
        System.out.println("Splitter将处理结果处理成map类型:");
        for (Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    public List<String> split() {
        List<String> result = Lists.newArrayList();

        InboundParser p = InboundParser.build(result);

        String s1 = "C2007051             209 7105100-870        10/29/18                    -1.0 R        42.19            -42.19          -42.19         0.00";
        String s2 = "                          1                 10/29/18                     0.0          42.19                              0.00";

        result = p.splitLineOne(s1);
        return result;
    }

    public List<String> splitExchangeRate(String s) {
        List<String> result = Lists.newArrayList();

        InboundParser p = InboundParser.build(result);
        return p.splitExchangeRateRaw(s);
    }

    public List<String> splitExchangeRate2(String s) {
        List<String> result = Lists.newArrayList();

        InboundParser p = InboundParser.build(result);
        return p.splitExchangeRate(s);
    }

    public static void main(String[] args) {
        BaseConfig.loadConfig();

        InboundService svc = new InboundService();
        /// svc.loadData();

        svc.trySplitter();
    }
}
