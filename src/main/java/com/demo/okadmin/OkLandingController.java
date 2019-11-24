package com.demo.okadmin;

import com.jfinal.core.Controller;

/**
 * @author ygzheng
 */
public class OkLandingController extends Controller {
    public void index() {
        // render("index.html");

        redirect("/pages/mat/projectPlant");
    }
}
