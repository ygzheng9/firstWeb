package com.demo.matanalysis;

import com.demo.config.BaseConfig;
import com.jfinal.kit.JsonKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MatServiceTest {
    private static Log log = Log.getLog(MatServiceTest.class);
    private final MatService svc = new MatService();

    @BeforeAll
    public static void setup() {
        BaseConfig.setupEnv();
    }

    @Test
    public void testJsonData() {
        System.out.println("it is test");
        assertEquals(6, 6);
    }


    @Test
    public void getCustomerProjectMappingTest() {
        List<Record> list = svc.getClientProjectMapping();
        int idx = 0;
        for (Record i : list) {
            log.info(JsonKit.toJson(i));

            if (idx > 10) {
                break;
            }
            idx++;
        }
    }

    @Test
    public void getBomList() {
        List<Record> list = svc.getBomList("蔚来", "合肥JIT");
        int idx = 0;
        for (Record i : list) {
            log.info(JsonKit.toJson(i));

            if (idx > 10) {
                break;
            }
            idx++;
        }
    }

    @Test
    public void getBomItemsTest() {
        List<BomItem> list = svc.getBomItems("C490-Front");

        assertNotNull(list);

        int idx = 0;
        for (BomItem i : list) {
            log.info(JsonKit.toJson(i));

            if (idx > 10) {
                break;
            }
            idx++;
        }
    }

    @Test
    public void reuseByBom() {
        List<Record> list = svc.reuseByBom();

        assertNotNull(list);

        int idx = 0;
        for (Record i : list) {
            log.info(JsonKit.toJson(i));

            if (idx > 10) {
                break;
            }
            idx++;
        }
    }
}
