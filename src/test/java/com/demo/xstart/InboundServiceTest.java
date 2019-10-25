package com.demo.xstart;

import cn.hutool.core.util.ReUtil;
import com.demo.config.BaseConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class InboundServiceTest {
    private final InboundService svc = new InboundService();

    @BeforeAll
    public static void setup() {
        System.out.println("setup...");
        BaseConfig.loadConfig();
    }

    @Test
    void loadDataTest() {
        svc.loadData();
    }

    @Test
    void trySplitterTest() {
        svc.trySplitter();
    }

    @Test
    void spliteTest() {
        List<String> l = svc.split();
        System.out.println(l.size() + ": " + l);
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
        List<String> l = svc.splitExchangeRate(s);

        System.out.println(l);
    }

    @Test
    void parseExchange2() {
        String s = "兑换率:EUR 1.0 = CNY 8.0111";
        List<String> l = svc.splitExchangeRate2(s);

        System.out.println(l);
    }
}
