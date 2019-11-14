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

    public void index() {
        render("index.html");
    }

    public void getClientHeat() {
        List<Record> items = bomSvc.getClientProjectMapping();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void getBomList() {
        String client = get("client");
        String plant = get("plant");

        List<Record> items = bomSvc.getBomList(client, plant);

        set("items", items);
        keepPara();

        render("bomlist.html");
    }

    public void getBomItems() {
        String bomid = get("bomid");

        List<BomItem> items = bomSvc.getBomItems(bomid);

        set("items", items);
        keepPara();

        render("bomitems.html");
    }

    public void reuseByBom() {
        Kv kv = bomSvc.getInfoByBom();
        set("info", kv);

        render("reusebybom.html");
    }

    public void reuseByBomData() {
        List<Record> items = bomSvc.reuseByBom();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void getBomReuse() {
        render("bomreuse.html");
    }

    public void getBomReuseData() {
        List<Record> items = bomSvc.getBomReuse();

        Kv data = new Kv();
        data.set("status", StatusOK);
        data.set("rtnCode", 0);
        data.set("items", items);

        renderJson(data);
    }

    public void getMatByReuseCount() {
        int count = getInt("count");
        List<MatInfo> items = bomSvc.getMatByReuseCount(count);

        set("items", items);
        keepPara();

        render("matlist.html");
    }

    public void getBomByMat() {
        String mat = get("mat");

        List<Record> items = bomSvc.getBomByMat(mat);
        MatInfo info = bomSvc.getMatInfo(mat);

        set("items", items);
        set("mat", info);

        render("bombymat.html");
    }

    public void reuseByProject() {
        Kv kv = bomSvc.getInfoByProject();
        set("info", kv);

        render("reusebyproject.html");
    }

    public void getProjectReuse() {
        String avg = get("avg");

        int a = (int) Double.parseDouble(avg);

        List<ProjectInfo> items = bomSvc.getProjectReuse();

        set("items", items);
        set("avg", a);
        set("title", "项目零件复用度");
        render("projectreuse.html");
    }

    public void getProjectMatList() {
        // 根据 project，取得所有的料号
        String p = get("p");

        List<Record> items = bomSvc.getProjectMatList(p);
        ProjectInfo project = bomSvc.getProjectInfoByName(p);

        set("items", items);
        set("p", project);

        render("projectmatlist.html");
    }

    public void getProjectByMat() {
        String m = get("m");

        List<ProjectInfo> items = bomSvc.getprojectByMat(m);

        set("items", items);
        set("avg", 0);
        set("title", "使用到 " + m + " 的项目");
        render("projectreuse.html");
    }
}
