package com.demo.xstart;

import cn.hutool.core.util.ReUtil;
import com.demo.config.BaseConfig;
import com.demo.model.BomProjectMapping;
import com.demo.matanalysis.InboundParser;
import com.demo.matanalysis.InboundService;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class InboundServiceTest {
    private final InboundService svc = new InboundService();

    @BeforeAll
    public static void setup() {
        System.out.println("setup...");
        BaseConfig.setupEnv();
    }

    @Test
    void trySplitterTest() {
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

    @Test
    void spliteTest() {

        List<String> result = Lists.newArrayList();

        InboundParser p = InboundParser.build(result);

        String s1 = "C2007051             209 7105100-870        10/29/18                    -1.0 R        42.19            -42.19          -42.19         0.00";
        String s2 = "                          1                 10/29/18                     0.0          42.19                              0.00";

        result = p.splitLineOne(s1);

        System.out.println(result.size() + ": " + result);
    }

    @Test
    void parseTest() {
        Pattern p = Pattern.compile("兑换率:[A-Za-z]+ * = * *");
        String s = "兑换率:EUR 1.0 = CNY 8.0111";
        List<String> l = ReUtil.getAllGroups(p, s);
        System.out.println(l);
    }

    @Test
    void parseExchange() {
        String s = "兑换率:EUR 1.0 = CNY 8.0111";
        List<String> result = Lists.newArrayList();
        InboundParser p = InboundParser.build(result);
        List<String> l = p.splitExchangeRateRaw(s);

        System.out.println(l);
    }

    @Test
    void parseExchange2() {
        String s = "兑换率:EUR 1.0 = CNY 8.0111";

        List<String> result = Lists.newArrayList();

        InboundParser p = InboundParser.build(result);
        List<String> l = p.splitExchangeRate(s);

        System.out.println(l);

        BomProjectMapping b = new BomProjectMapping().dao();
        System.out.println(b.findAll());
    }
}
