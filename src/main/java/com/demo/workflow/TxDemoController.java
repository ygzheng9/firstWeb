package com.demo.workflow;

import com.demo.model.WkFormDemo;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;

import java.util.List;

/**
 * @author ygzheng
 * 审批业务的 demo
 */
public class TxDemoController extends Controller {
    @Inject
    TxDemoService txSvc;


    public void newEntry() {
        WkFormDemo item = new WkFormDemo();

        set("item", item);
        set("title", "新建");
        render("txdemo_form.html");
    }

    public void saveEntry() {
        WkFormDemo demo = JsonKit.parse(getRawData(), WkFormDemo.class);

        if (txSvc.save(demo)) {
            renderJson(Ret.ok("msg", "保存成功"));
            return;
        }

        renderJson(Ret.fail("msg", "保存失败"));
    }

    public void submit() {
        WkFormDemo demo = JsonKit.parse(getRawData(), WkFormDemo.class);

        if (!txSvc.save(demo)) {
            renderJson(Ret.fail("msg", "保存失败"));
            return;
        }

    }

    public void list() {
        List<WkFormDemo> items = txSvc.allItems();

        set("items", items);
        render("txdemo_list.html");
    }

    public void show() {
        Integer id = getInt("id");
        WkFormDemo item = txSvc.findbyID(id);

        set("item", item);
        set("title", "修改");
        render("txdemo_form.html");
    }
}
