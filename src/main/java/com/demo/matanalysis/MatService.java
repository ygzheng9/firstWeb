package com.demo.matanalysis;

import cn.hutool.core.util.NumberUtil;
import com.beust.jcommander.internal.Sets;
import com.demo.config.RecordKit;
import com.demo.model.BomItem;
import com.demo.model.BomProjectMapping;
import com.demo.model.MatInfo;
import com.demo.model.ProjectInfo;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author ygzheng
 */
public class MatService {
    private static Log log = Log.getLog(MatService.class);

    public void loadMapping() {
        // 从指定文件，加载 bom-项目-工厂-客户 的对应关系
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

    public List<Record> getClientProjectMapping() {
        // 获取 客户-项目-bom 对应关系
        List<Record> mappings = Db.template("mat.getProjectCustomerMapping").find();

        return mappings;
    }

    public List<Record> getBomList(String client, String plant) {
        // 根据 （客户，工厂） 获取 bom 列表
        List<Record> list = Db.template("mat.getBomList", client, plant).find();

        return list;
    }

    public List<BomItem> getBomItems(String bomid) {
        BomItem dao = new BomItem().dao();
        List<BomItem> items = dao.template("mat.getBomItems", bomid).find();

        Set<Integer> list = Sets.newHashSet();
        for (BomItem i : items) {
            String s = i.getLevel();
            Integer a = 0;
            try {
                a = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                log.info("level convert failed: " + i.getLevel());
            }

            if (a == 99) {
                a = 1;
            }

            list.add(a);
        }

        Integer max = Collections.max(list);
        Integer min = Collections.min(list);
        Double range = (max - min + 1) * 1.0;

        for (BomItem i : items) {
            Integer a = 0;
            try {
                a = Integer.parseInt(i.getLevel());
            } catch (NumberFormatException e) {
                log.info("level convert failed: " + i.getLevel());
            }
            if (a == 99) {
                a = 1;
            }

            Double d = (1.0 - (a - min) / range) * 100;

            i.setLevelRate(d);
        }

        return items;
    }


    public List<Record> reuseByBom() {
        // 料号复用：bom 级别
        List<Record> list = Db.template("mat.reuseByBom").find();

        return list;
    }

    public Kv getInfoByBom() {
        // 基于 bom 的复用率计算
        Integer bomCount = Db.template("mat.getBomCount").queryInt();
        Integer partCount = Db.template("mat.getMatCount").queryInt();
        Integer reuseCount = Db.template("mat.getMatReuseCount").queryInt();

        Integer reuseBomCount = Db.template("mat.getMatBomReuseCount").queryInt();
        Integer bomPartCount = Db.template("mat.getBomPartCnt").queryInt();
        double reuseRate = reuseBomCount * 1.0 / bomPartCount;
        String reuseStr = NumberUtil.decimalFormat("#.##%", reuseRate);

        Kv kv = new Kv();
        kv.set("bomCount", bomCount);
        kv.set("partCount", partCount);
        kv.set("reuseCount", reuseCount);
        kv.set("reuseBomCount", reuseBomCount);
        kv.set("reuseRate", reuseStr);

        return kv;
    }

    public Kv getInfoByProject() {
        // 基于 project 的复用率计算
        Integer projectCount = Db.template("mat.projectCount").queryInt();
        Integer partCount = Db.template("mat.getMatCount").queryInt();
        Integer reuseCount = Db.template("mat.projectReuseMat").queryInt();

        Integer a = Db.template("mat.projectMatPairReuse").queryInt();
        Integer b = Db.template("mat.projectMatPair").queryInt();
        double reuseRate = a * 1.0 / b;
        String reuseStr = NumberUtil.decimalFormat("#.##%", reuseRate);

        Kv kv = new Kv();
        kv.set("projectCount", projectCount);
        kv.set("partCount", partCount);
        kv.set("reuseCount", reuseCount);
        kv.set("reuseRateStr", reuseStr);
        kv.set("reuseRate", reuseRate * 100);

        return kv;
    }

    public List<Record> getBomReuse() {
        // 计算每个 bom 的零件复用率： bom 复用的零件数量 / bom 使用到的所有零件数量
        List<Record> items = Db.template("mat.getBomReuse").find();
        for (Record r : items) {
            double reuseRate = r.getInt("repeatCnt") * 1.0 / r.getInt("partCount");
            String reuseStr = NumberUtil.decimalFormat("#.##%", reuseRate);
            r.set("reuseStr", reuseStr);
            r.set("reuseRate", reuseRate);
        }

        return items;
    }

    public List<ProjectInfo> getProjectReuse() {
        // 计算每个 project 的零件复用率： 复用的零件数量 /  使用到的所有零件数量
        // 在 table 中已经预先生成，这里直接读取即可
        ProjectInfo dao = new ProjectInfo().dao();
        return dao.template("mat.projectInfo").find();
    }

    public List<MatInfo> getMatByReuseCount(int count) {
        MatInfo dao = new MatInfo().dao();
        List<MatInfo> items = dao.template("mat.getMatByReuseCount", count).find();
        return items;
    }

    public List<Record> getBomByMat(String mat) {
        return Db.template("mat.getBOMByMat", mat).find();
    }

    public MatInfo getMatInfo(String mat) {
        MatInfo matDao = new MatInfo().dao();
        return matDao.template("mat.getMatInfo", mat).findFirst();
    }

    public List<Record> getProjectMatList(String project) {
        return Db.template("mat.projectMatList", project).find();
    }

    public ProjectInfo getProjectInfoByName(String project) {
        ProjectInfo dao = new ProjectInfo().dao();
        return dao.template("mat.projectInfobyName", project)
            .findFirst();
    }

    public List<ProjectInfo> getprojectByMat(String mat) {
        ProjectInfo dao = new ProjectInfo().dao();
        return dao.template("mat.projectByMat", mat).find();
    }

    List<Record> projectMatReuseStats() {
        return Db.template("mat.projectMatReuseStats").find();
    }

    public List<MatInfo> projectMatByReuseCount(int count) {
        MatInfo dao = new MatInfo().dao();
        List<MatInfo> items = dao.template("mat.projectMatByReuseCount", count).find();
        return items;
    }

}
