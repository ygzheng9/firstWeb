package com.demo.matanalysis;

import com.demo.model.BomItem;
import com.demo.model.MatInfo;
import com.demo.model.ProjectInfo;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @author ygzheng
 */
public class MatController extends Controller {
    private static Log log = Log.getLog(MatController.class);

    private static int StatusOK = 200;

    @Inject
    private MatService bomSvc;

    public void projectPlant() {
        render("project_plant.html");
    }

    public void getClientHeat() {
        List<Record> items = bomSvc.getClientProjectMapping();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void bomList() {
        String client = get("client");
        String plant = get("plant");

        List<Record> items = bomSvc.getBomList(client, plant);

        set("items", items);
        keepPara();

        render("bom_list.html");
    }

    public void bomItems() {
        String bomid = get("bomid");

        List<BomItem> items = bomSvc.getBomItems(bomid);

        set("items", items);
        keepPara();

        render("bom_items.html");
    }

    public void bomMatReuse() {
        Kv kv = bomSvc.getInfoByBom();
        set("info", kv);

        render("bom_mat_reuse.html");
    }

    public void reuseByBomData() {
        List<Record> items = bomSvc.reuseByBom();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void bomReuse() {
        render("bom_reuse.html");
    }

    public void getBomReuseData() {
        List<Record> items = bomSvc.getBomReuse();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void bomMatByReuse() {
        int count = getInt("count");
        List<MatInfo> items = bomSvc.getMatByReuseCount(count);

        set("items", items);
        keepPara();

        render("bom_mat_by_reuse.html");
    }

    public void bomByMat() {
        String mat = get("mat");

        List<Record> items = bomSvc.getBomByMat(mat);
        MatInfo info = bomSvc.getMatInfo(mat);

        set("items", items);
        set("mat", info);

        render("bom_by_mat.html");
    }

    public void projectMatReuse() {
        Kv kv = bomSvc.getInfoByProject();
        set("info", kv);

        render("project_mat_reuse.html");
    }

    public void projectReuse() {
        String avg = get("avg");

        int a = (int) Double.parseDouble(avg);

        List<ProjectInfo> items = bomSvc.getProjectReuse();

        set("items", items);
        set("avg", a);
        set("title", "项目零件复用度");
        render("project_reuse.html");
    }

    public void projectMatList() {
        // 根据 project，取得所有的料号
        String p = get("p");

        List<Record> items = bomSvc.getProjectMatList(p);
        ProjectInfo project = bomSvc.getProjectInfoByName(p);

        set("items", items);
        set("p", project);

        render("project_mat_list.html");
    }

    public void projectByMat() {
        String m = get("m");

        List<ProjectInfo> items = bomSvc.getprojectByMat(m);

        set("items", items);
        set("avg", 0);
        set("title", "使用零件 " + m + " 的项目");
        render("project_reuse.html");
    }

    public void projectMatReuseStats() {
        List<Record> items = bomSvc.projectMatReuseStats();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void projectMatByReuseCount() {
        int count = getInt("count");
        List<MatInfo> items = bomSvc.projectMatByReuseCount(count);

        set("items", items);
        keepPara();

        render("project_mat_by_reuse.html");
    }
}
