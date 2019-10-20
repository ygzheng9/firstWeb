package com.demo.service;

import com.beust.jcommander.internal.Sets;
import com.demo.kit.RecordKit;
import com.demo.model.Hotword;
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
import java.util.Set;

/**
 * @author ygzheng
 */
public class WordcloudService {
    private static Log log = Log.getLog(WordcloudService.class);
    private static List<Hotword> words = null;

    public List<Hotword> getCloud(boolean isForce) {
        Set<String> ignores = this.loadIgnores();
        List<Hotword> words = this.loadCloud(isForce);

        List<Hotword> results = Lists.newArrayList();

        for (Hotword w : words) {
            if (!ignores.contains(w.getName())) {
                results.add(w);
            }
        }

        return results;
    }

    private List<Hotword> loadCloud(boolean isForce) {
        if (!isForce && words != null) {
            log.info("use cache. isForce: " + isForce);
            return words;
        }

        if (words == null) {
            words = Lists.newArrayList();
        }

        String fname = PropKit.get("baseFolder") + "/zdata/wordcloud.csv";
        List<String> columnNames = ImmutableList.of("name", "count");
        RecordKit record = RecordKit.build(columnNames);

        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);

            Splitter splter = Splitter.on("|");
            boolean isHeader = true;
            for (String line : lines) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                List<String> fields = splter.splitToList(line);
                record.wrap(fields);

                Hotword word = new Hotword();
                word.setName(record.getString("name"));
                word.setCount(record.getInteger("count"));

                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;
    }


    private Set<String> loadIgnores() {
        Set<String> items = Sets.newHashSet();

        String fname = PropKit.get("baseFolder") + "/zdata/ignores.txt";
        try {
            File infile = new File(fname);
            List<String> lines = Files.readLines(infile, Charsets.UTF_8);


            for (String line : lines) {
                items.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("load ignores: " + items.size());

        return items;
    }

}
