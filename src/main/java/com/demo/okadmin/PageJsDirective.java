package com.demo.okadmin;

import cn.hutool.core.util.StrUtil;
import com.jfinal.core.JFinal;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

/**
 * @author ygzheng
 * 未完成
 * TODO:
 * 1. 未能获取 controllerName，目前能拿到 controllerKey； --> controllerKey 是 URL 中 除去 action 的部分；
 * 2. 未能获取 templateName，目前能拿到 actionKey，是 url mapping 中的 key，而不是 render 中的文件名；
 * 3. render 中传入了 templateName，但是还有 baseViewPath，拼在一起，才是完整的路径(服务器文件系统中的路径)；
 * 4. 未能获取 程序的当前目录，或文件系统的路径 -> 本来想先判断是否存在文件 path_to_js_folder/controllerName/templateName.js
 */
public class PageJsDirective extends Directive {
    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        // String templateName = (String) scope.getRootData().get("templateName");
        // if (StrKit.isBlank(templateName)) {
        //     write(writer, "");
        //     return;
        // }
        // String fileBase = StrUtil.removeSuffix(templateName, ".html");

        String controllerKey = (String) scope.getRootData().get("controllerKey");

        System.out.println(JFinal.me().getContextPath());
        System.out.println(controllerKey);

        String template = "<script src=\"/assets/js{}/{}.js\"></script>";
        String str = StrUtil.format(template, controllerKey, "fileBase");

        write(writer, str);
    }
}
