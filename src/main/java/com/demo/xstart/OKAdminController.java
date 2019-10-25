package com.demo.xstart;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

/**
 * @author ygzheng
 */
public class OKAdminController extends Controller {
    public void index() {
        render("index.html");
    }

    @ActionKey("pages/console")
    public void console() {
        render("pages/console.html");
    }

    @ActionKey("pages/console1")
    public void console1() {
        render("pages/console1.html");
    }

    @ActionKey("pages/wordcloud")
    public void wordcloud() {
        render("pages/wordcloud.html");
    }

    @ActionKey("pages/login")
    public void login() {
        render("pages/login/login.html");
    }

    @ActionKey("pages/register")
    public void register() {
        render("pages/login/register.html");
    }

    @ActionKey("pages/forget")
    public void forget() {
        render("pages/login/forget.html");
    }

    @ActionKey("pages/member/user_pwd")
    public void userpwd() {
        render("pages/member/user-pwd.html");
    }

    @ActionKey("pages/member/user_info")
    public void userinfo() {
        render("pages/member/user-info.html");
    }


    @ActionKey("pages/member/user")
    public void user() {
        render("pages/member/user.html");
    }


    @ActionKey("pages/member/user_add")
    public void userAdd() {
        render("pages/member/user-add.html");
    }


    @ActionKey("pages/member/user_edit")
    public void userEdit() {
        render("pages/member/user-edit.html");
    }


}
