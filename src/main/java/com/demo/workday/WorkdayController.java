package com.demo.workday;

import com.demo.model.UtEntry;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @author ygzheng
 */
public class WorkdayController extends Controller {
    private static Log log = Log.getLog(WorkdayController.class);

    @Inject
    WorkdayService workdaySvc;

    public void empty() {
        render("empty.html");
    }

    public void validation() {
        render("validation.html");
    }

    public void list() {
        render("list.html");
    }

    public void search() {
        render("search_main.html");
    }

    public void doSearch() {
        List<UtEntry> items = workdaySvc.search(getRawData());
        set("items", items);
        render("search_items.html");
    }

    public void newEntry() {
        // 设置初始值
        UtEntry entry = new UtEntry();
        entry.setId("");
        entry.setOnSite("现场");
        entry.setDetails("");

        set("entry", entry);

        render("entry.html");
    }

    public void openEntry() {
        String id = get("id");

        renderEntry(id);
    }

    public void renderEntry(String id) {
        UtEntry entry = workdaySvc.findById(id);
        set("entry", entry);
        render("entry.html");
    }

    public void saveEntry() {
        UtEntry entry = JsonKit.parse(getRawData(), UtEntry.class);
        log.info(entry.toJson());

        String id = workdaySvc.saveEntry(entry);
        if (id.length() == 0) {
            renderJson(Ret.fail("msg", "保存失败"));
            return;
        }
        renderJson(Ret.ok("id", id));
    }

    public void projectList() {
        String pCode = get("s");

        List<Record> items = workdaySvc.getProject();
        set("items", items);
        set("code", pCode);

        // render("project_list.html");

        Kv param = new Kv();
        param.set("items", items);
        param.set("code", pCode);

        String html = renderToString("project_list.html", param);
        Kv kv = new Kv();
        kv.set("count", items.size());
        kv.set("html", html);
        renderJson(kv);
    }
}
