package com.demo.xstart;

import com.demo.model.Hotword;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;

import java.util.List;

/**
 * @author ygzheng
 */
@Clear(PjaxInterceptor.class)
public class APIController extends Controller {
    @Inject
    private
    WordcloudService cloudSvc;

    private static String okStatus = "1000";

    public void wordcloud() {
        String force = get("force");

        boolean isForce = false;
        if (force != null && force.compareTo("true") == 0) {
            isForce = true;
        }

        List<Hotword> words = cloudSvc.getCloud(isForce);

        renderJson(words);
    }

    public void login() {
        Kv data = new Kv();
        data.set("msg", "欢迎回来");
        data.set("status", okStatus);

        renderJson(data);
    }
}
