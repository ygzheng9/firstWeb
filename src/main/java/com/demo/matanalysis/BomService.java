package com.demo.matanalysis;

import com.demo.config.BaseConfig;
import com.demo.config.RecordKit;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author ygzheng
 */
public class BomService {
    private static Log log = Log.getLog(BomService.class);

    public void loadMapping() {
        List<BomProjectMapping> results = Lists.newArrayList();

        String fname = PropKit.get("baseFolder") + "/zdata/bom_projct_mapping.csv";
        List<String> columnNames = ImmutableList.of("bomId", "partCount", "project", "client", "plant", "city");
        RecordKit record = RecordKit.build(columnNames);

        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on(",");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);
                record.wrap(fields);

                BomProjectMapping entry = new BomProjectMapping();
                entry.setBomID(record.getString("bomId"));
                entry.setPartCount(record.getInteger("partCount"));
                entry.setProject(record.getString("project"));
                entry.setClient(record.getString("client"));
                entry.setPlant(record.getString("plant"));
                entry.setCity(record.getString("city"));

                results.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (BomProjectMapping i : results) {
            boolean r = i.save();
            if (!r) {
                log.warn("save failed: " + i.toString());
            }
        }
    }


    public static void main(String[] args) {
        BaseConfig.loadConfig();

        BomService svc = new BomService();
        svc.loadMapping();
    }

}
