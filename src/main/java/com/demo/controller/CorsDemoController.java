package com.demo.controller;


import com.demo.config.CorsInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

/**
 * @author ygzheng
 */
@Before(CorsInterceptor.class)
public class CorsDemoController extends Controller {
    private static final String OPTIONS = "OPTIONS";

    /// @Inject
    // IteService srv;

    public void test() {
        String mUrlParam = getPara("Parameter");
        Ret ret = null;

        if (OPTIONS.equals(getRequest().getMethod())) {
            ret = Ret.ok("result", "options");
            renderJson(ret);

            return;
        }

        String jsonString = HttpKit.readData(getRequest());
        if (jsonString != null &&  jsonString != "") {
            try {
                Kv kv = JsonKit.parse(jsonString, Kv.class);
                System.out.println(kv);

                ret = Ret.ok("result", mUrlParam);
                renderJson(ret);
            } catch (Exception e) {
                ret = Ret.fail("result", "失败");
                renderJson(ret);

                return;
            }
        } else {
            ret = Ret.fail("result", mUrlParam);
            renderJson(ret);

            return;
        }
    }
}
