package com.demo.okadmin;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

/**
 * @author ygzheng
 */
public class MemberController extends Controller {

    @ActionKey("/pages/member/user_pwd")
    public void userPwd() {
        render("user-pwd.html");
    }

    @ActionKey("/pages/member/user_info")
    public void userinfo() {
        render("user-info.html");
    }

    public void user() {
        render("user.html");
    }

    @ActionKey("/pages/member/user_add")
    public void userAdd() {
        render("user-add.html");
    }


    @ActionKey("/pages/member/user_edit")
    public void userEdit() {
        render("user-edit.html");
    }

}
