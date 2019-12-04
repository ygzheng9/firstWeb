package com.demo.okadmin;

import com.jfinal.core.Controller;

/**
 * @author ygzheng
 */
public class OKPagesController extends Controller {
    public void console() {
        render("console.html");
    }

    public void console1() {
        render("console1.html");
    }

    public void login() {
        render("login/login.html");
    }

    public void register() {
        render("login/register.html");
    }

    public void forget() {
        render("login/forget.html");
    }

    public void wordCloud() {
        render("word_cloud.html");
    }

    public void plantEnv() {
        render("plant_env.html");
    }

    public void relation() {
        render("relation.html");
    }


    public void helloReact() {
        render("hello_react.html");
    }

    public void helloRouter() {
        render("hello_router.html");
    }

}
