package com.demo.workday;

import com.jfinal.core.Controller;

/**
 * @author ygzheng
 */
public class WorkdayController extends Controller {
    public void list() {
        render("list.html");
    }
}
