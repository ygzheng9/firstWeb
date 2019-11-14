package com.demo.matanalysis;

import com.demo.config.BaseConfig;
import com.demo.model.PoHead;
import com.demo.model.PoItem;
import com.google.common.base.Charsets;
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
 * 将 QAD 系统导出的入库记录，导入到 db；
 * 入库记录：
 * 1. 固定长度分割；
 * 2. 有分页，分页的位置有多个：头，两行之间，一行内部；
 * 3. 连续的两行表示一条入库记录，并且还可能有额外的汇率行(紧接着的第三行)；
 * 4. 订单行项目结束有多种情况：固定符号；没有任何符号，直接开始新订单；
 */
public class InboundService {
    private static Log log = Log.getLog(InboundService.class);

    public void loadData(String batchID, boolean save) {
        String fileName = PropKit.get("baseFolder") + "/zdata/" + batchID + ".txt";

        List<List<String>> headList = Lists.newArrayList();
        Map<String, List<List<String>>> items = Maps.newHashMap();

        List<PoHead> poHeads = Lists.newArrayList();
        List<PoItem> poItems = Lists.newArrayList();

        try {
            File infile = new File(fileName);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);
            InboundParser p = InboundParser.build(lines);
            p.setBatchID(batchID);

            while (p.hasNext()) {
                p.moveDown(1);

                List<String> fields = p.parseHead();
                if (!p.hasNext()) {
                    break;
                }

                List<List<String>> orderItems = p.parseItems();

                headList.add(fields);
                String orderHead = fields.get(0);
                items.put(orderHead, orderItems);

                // log.info("order: " + orderHead + " with items: " + orderItems.size());
                // for (List<String> l : orderItems) {
                //     System.out.println(l);
                // }
                // log.debug("got order: " + orderHead);
            }

            log.info("1===total order: " + headList.size() + " with lines: " + lines.size());

            for (List<String> line : headList) {
                PoHead h = p.convertToPoHead(line);
                poHeads.add(h);
            }

            for (Map.Entry<String, List<List<String>>> entry : items.entrySet()) {
                List<PoItem> i = p.convertToPoItem(entry.getKey(), entry.getValue());

                poItems.addAll(i);
            }

            log.info("2===total order: " + poHeads.size() + " with lines: " + poItems.size());

            if (save) {
                for (PoHead h : poHeads) {
                    boolean rtn = h.save();
                    if (!rtn) {
                        log.error("save po head failed: " + h.toString());
                    }
                }

                for (PoItem i : poItems) {
                    boolean rtn = i.save();
                    if (!rtn) {
                        log.error("save po items failed: " + i.toString());
                    }
                }
            }
        } catch (
            IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BaseConfig.setupEnv();

        long startTime = System.currentTimeMillis();

        InboundService svc = new InboundService();
        svc.loadData("190105", true);

        long endTime = System.currentTimeMillis();

        log.info("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
