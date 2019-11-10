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

    public void wordcloud() {
        render("wordcloud.html");
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

    public void plantEnv() {
        render("plantenv.html");
    }

    public void hello() {
        render("hello.html");
    }
}
