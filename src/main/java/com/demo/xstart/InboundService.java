package com.demo.xstart;

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
 */
public class InboundService {
    private static Log log = Log.getLog(InboundService.class);


    public void loadData() {
        String fileName = PropKit.get("baseFolder") + "/zdata/ib_test2.txt";

        List<List<String>> headList = Lists.newArrayList();
        Map<String, List<List<String>>> items = Maps.newHashMap();

        List<PoHead> poHeads = Lists.newArrayList();
        List<PoItem> poItems = Lists.newArrayList();

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

                p.nextToken();
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

            // for (PoHead h : poHeads) {
            //     boolean rtn = h.save();
            //     if (!rtn) {
            //         log.error("save po head failed: " + h.toString());
            //     }
            // }
            //
            // for (PoItem i : poItems) {
            //     boolean rtn = i.save();
            //     if (!rtn) {
            //         log.error("save po items failed: " + i.toString());
            //     }
            // }
        } catch (
            IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BaseConfig.setupEnv();

        InboundService svc = new InboundService();
        svc.loadData();
    }
}
