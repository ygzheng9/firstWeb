package com.demo.controller;

import com.demo.model.Hotword;
import com.demo.service.WordcloudService;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;

import java.util.List;

/**
 * @author ygzheng
 */
public class APIController extends Controller {
    @Inject
    private
    WordcloudService cloudSvc;

    public void wordcloud() {
        String force = get("force");

        boolean isForce = false;
        if (force != null && force.compareTo("true") == 0) {
            isForce = true;
        }

        List<Hotword> words = cloudSvc.getCloud(isForce);

        renderJson(words);
    }
}
