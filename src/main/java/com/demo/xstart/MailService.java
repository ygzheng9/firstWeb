package com.demo.xstart;

import cn.hutool.core.lang.Validator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author ygzheng
 */
public class MailService {
    private static Log log = Log.getLog(MailService.class);

    public void sendMail() {
        String server = PropKit.get("email.server");
        String password = PropKit.get("email.password");
        String from = PropKit.get("email.from");
        String display = PropKit.get("email.from.display");

        String title = "邮件标题";

        List<EmailAttachment> attachments = new ArrayList<>();
        List<String> fileNames = Lists.newArrayList("/Users/ygzheng/Downloads/tmp/数据库表结构信息导出.html",
            "/Users/ygzheng/Downloads/tmp/待收集数据.xlsx");
        for (String a : fileNames) {
            EmailAttachment att = new EmailAttachment();
            att.setPath(a);
            att.setDisposition(EmailAttachment.ATTACHMENT);
            attachments.add(att);
        }

        List<String> toMails = new ArrayList<>();
        toMails.add("yonggang.zheng@qq.com");
        toMails.add("zygzheng@cn.ibm.com");

        Engine engine = Engine.use();
        engine.setDevMode(true);
        engine.setToClassPathSourceFactory();

        Template tmpl = engine.getTemplate("cmd/simple.html");
        Kv kv = Kv.by("name", "template");
        kv.set("type", "哈哈哈哈");

        try {
            HtmlEmail email = new HtmlEmail();
            email.setCharset("utf-8");

            email.setHostName(server);
            email.setAuthentication(from, password);

            if (display.length() > 0) {
                email.setFrom(from, display);
            } else {
                email.setFrom(from);
            }

            email.setSubject(title);

            // 内嵌图片，拼接到邮件正文的 html 中
            File f = new File("/Users/ygzheng/Downloads/tmp/ABCD.png");
            String cid1 = email.embed(f);
            kv.set("cid1", cid1);

            // 邮件正文, 包含内嵌的图片
            email.setHtmlMsg(tmpl.renderToString(kv));

            // 收件人列表
            for (String to : toMails) {
                email.addTo(to);
            }

            // 添加附件
            for (EmailAttachment att : attachments) {
                email.attach(att);
            }

            String rtn = email.send();

            System.out.println(rtn);
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    public void validateMail() {
        List<String> inputs = ImmutableList.of("zygzheng@cn.ibm.com", "yonggang.zheng@qq.com",
            "asdfkasdf@asdf.com", "asdf", "haha@163.net");

        List<Boolean> expected = ImmutableList.of(true, true, true, true, false);

        int idx = 0;
        for (String i : inputs) {
            boolean b = Validator.isEmail(i);
            if (expected.get(idx) != b) {
                log.warn("failed: " + i);
            }

            idx++;
        }

    }

    public static void main(String[] args) {
        PropKit.useFirstFound("production.properties", "default.properties");

        MailService svc = new MailService();

        svc.sendMail();
    }
}
