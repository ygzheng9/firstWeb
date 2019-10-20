package com.demo.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.demo.controller.APIController;
import com.demo.controller.BlogController;
import com.demo.model._MappingKit;
import com.jfinal.config.*;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

import java.sql.Connection;

public class BaseConfig extends JFinalConfig {
    private static Prop defaultConfig;
    private WallFilter wallFilter;

    /**
     * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
     */
    public static void main(String[] args) {
        UndertowServer.start(BaseConfig.class);
    }


    static void loadConfig() {
        if (defaultConfig == null) {
            defaultConfig = PropKit.useFirstFound("production.properties", "default.properties");
        }
    }

    /**
     * 配置JFinal常量值
     * @param me
     */
    @Override
    public void configConstant(Constants me) {
        /// 设置 devMode 之下的 action report 是否在 invocation 之后，默认值为 true
        // me.setReportAfterInvocation(false);
        defaultConfig = PropKit.use("default.properties");

        me.setDevMode(defaultConfig.getBoolean("devMode", false));

        me.setJsonFactory(MixedJsonFactory.me());

        // 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
        me.setInjectDependency(true);

        // 是否对超类中的属性进行注入
        me.setInjectSuperClass(true);
    }

    /**
     * 配置访问路由
     * @param me
     */
    @Override
    public void configRoute(Routes me) {
        me.setBaseViewPath("/view");

        me.add("/", BlogController.class, "/blog");
        me.add("/api", APIController.class, "/api");
    }

    /**
     * 配置引擎
     * @param me
     */
    @Override
    public void configEngine(Engine me) {
        //设置共享页面，在页面上通过#define layopen()定义模板，#@layopen()引用
        me.addSharedFunction("/view/common/_layopen.html");
        me.addSharedFunction("/view/common/_segment.html");

    }

    public static DruidPlugin getDruidPlugin() {
        loadConfig();
        //配置数据库连接池
        DruidPlugin dp = new DruidPlugin(defaultConfig.get("jdbcUrl"), defaultConfig.get("user"), defaultConfig.get("password"));
        return dp;
    }


    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin druidPlugin = getDruidPlugin();
        // 加强数据库安全
        wallFilter = new WallFilter();
        wallFilter.setDbType("mysql");
        druidPlugin.addFilter(wallFilter);
        // 添加 StatFilter 才会有统计数据
        druidPlugin.addFilter(new StatFilter());
        me.add(druidPlugin);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
        _MappingKit.mapping(arp);
        // 强制指定复合主键的次序，避免不同的开发环境生成在 _MappingKit 中的复合主键次序不相同
        arp.setPrimaryKey("document", "mainMenu,subMenu");
        me.add(arp);
        arp.setShowSql(defaultConfig.getBoolean("devMode", false));

        arp.getEngine().setToClassPathSourceFactory();
        // arp.addSqlTemplate("/sql/_all_sqls.sql");
    }

    @Override
    public void configInterceptor(Interceptors me) {
    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
