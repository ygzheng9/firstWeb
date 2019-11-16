package com.demo.xstart;


// import com.demo.model.Blog;

import com.demo.config.StreamRender;
import com.demo.matanalysis.MatService;
import com.demo.model.Blog;
import com.demo.service.SimpleKt;
import com.google.common.io.ByteStreams;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * BlogController class
 *
 * @author demo
 * @date 2019/10/01
 */

public class BlogController extends Controller {
    @Inject
    private
    BlogService blogSvc;

    @Inject
    private UserService userSvc;

    @Inject
    private MatService bomSvc;

    public void index(){
        System.out.println("index render.....");

        // blogSvc.showlog();
        //
        blogSvc.dumpInof();
        //
        // blogSvc.localFile();
        //
        // userSvc.loadFile();

        // bomSvc.loadMapping();

        render("list.html");

    }

    @ActionKey("/pjax_one")
    public void page1() {
        render("page1.html");
    }

    @ActionKey("/pjax_two")
    public void page2() {
        render("page2.html");
    }


    @ActionKey("/turbolinks_one")
    public void one() {
        render("one.html");
    }

    @ActionKey("/turbolinks_two")
    public void two() {
        try {
            TimeUnit.SECONDS.sleep(1);

            render("two.html");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void qrCode() {
        // 二维码携带的数据
        String data = "It's barCode from jFinal";

        // 渲染二维码图片，长度与宽度为 200 像素
        renderQrCode(data, 200, 200);
    }

    public void jsonData() {
        Map<String, Object> m2 = SimpleKt.getData();
        renderJson(m2);
    }

    public void getList(){
        Integer pageNum = getParaToInt("page");
        Integer pageSize = getParaToInt("limit");
        Page<Blog> blogs = blogSvc.blogDao.paginate(pageNum, pageSize, "select * ", "from blog");
        renderJson(blogs);
    }

    static String loadLongString() {
        Engine engine = Engine.use();

        engine.setDevMode(true);
        engine.setToClassPathSourceFactory();

        Kv kv = Kv.by("name", "template");
        kv.set("type", "哈哈哈哈");

        Template tmpl = engine.getTemplate("cmd/simple.html");

        // 直接输出到 String 变量
        String str = tmpl.renderToString(kv);

        return str;
    }

    public void form(){
        render("add.html");
    }

    /**
     * 提交方法
     */
    public  void submit(){
        Blog blog = getModel(Blog.class,"blog");
        blog.save();
        renderJson(Ret.ok());
    }
    /**
     * 编辑方法
     */
    public void edit(){
        Integer id = getInt(0);
        if(id!=null&&id>0){
            set("blog", blogSvc.blogDao.findById(id));
        }
        render("edit.html");
    }
    /**
     * 删除方法
     */
    public void del(){
        blogSvc.blogDao.deleteById(getPara(0));
        redirect("/");
    }
    /**
     * 更新方法
     */
    public void update(){
        Blog blog = getModel(Blog.class,"blog");
        blog.update();

        redirect("/");
    }

    public void asyncLoad() {
        render("asyncLoad.html");
    }

    // 下载默认目录下的文件
    public void downloadFile() {
        renderFile("2.pptx");
    }

    // render stream 直接写在 controller 中，可以抽取出来，自定义一个 StreamRender
    public void downloadStream() {
        String filename = "test.xlsx";
        HttpServletResponse response = getResponse();
        // HttpServletRequest request = getRequest();

        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-disposition", "attachment; filename=" + filename);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        //获取要下载的文件的绝对路径
        // String realPath = request.getSession().getServletContext().getRealPath("/images/module/curve_w.png");
        String realPath = "/Users/ygzheng/Downloads/tmp/待收集数据.xlsx";
        InputStream in = null;
        OutputStream out = null;
        try {
            //获取要下载的文件输入流
            in = new FileInputStream(realPath);
            out = response.getOutputStream();

            ByteStreams.copy(in, out);

            out.flush();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // render stream，自定义 render
    public void boomStream() {
        String filename = "test.xlsx";
        String realPath = "/Users/ygzheng/Downloads/tmp/待收集数据.xlsx";

        InputStream in = null;
        try {
            //获取要下载的文件输入流
            in = new FileInputStream(realPath);
            render(new StreamRender(in, filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
