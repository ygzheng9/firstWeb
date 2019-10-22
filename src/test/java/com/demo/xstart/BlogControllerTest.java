package com.demo.xstart;


import com.demo.model.Person;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlogControllerTest {
    @Test
    public void testJsonData() {
        System.out.println("it is test");
        assertEquals(6, 6);
    }


    @Test
    public void testJsonData2() {
        System.out.println("it is another test");
        int i = 6;
        assertEquals(6, i);
    }

    @Test
    void loadLongString() {
        BlogController blog = new BlogController();
        String s = BlogController.loadLongString();
        System.out.println(s);

        String s2 = com.demo.service.SimpleKt.loadTemplate();
        assertEquals(s, s2);

        int i = com.demo.service.SimpleKt.sum(3, 5);
        assertEquals(8, i);

    }

    @Test
    void initList() {
        List l = new ArrayList<String>() {
            private static final long serialVersionUID = 531850072164831668L;

            {
                add("first");
                add("有一个");
                add("another");
            }
        };

        assertEquals(l.size(), 3);
        System.out.println(l);
    }

    @Test
    void dbMeta() {
        String url = "jdbc:mysql://localhost:3333/jfinal_club";
        String user = "root";
        String password = "mysql";

        Connection conn =null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url,user,password);

            DatabaseMetaData dbMeta = conn.getMetaData();

            int    majorVersion   = dbMeta.getDatabaseMajorVersion();
            int    minorVersion   = dbMeta.getDatabaseMinorVersion();

            String productName    = dbMeta.getDatabaseProductName();
            String productVersion = dbMeta.getDatabaseProductVersion();

            System.out.println("数据库属性信息："+majorVersion+"."+minorVersion+"; "+productName+"-"+productVersion);

            int driverMajorVersion = dbMeta.getDriverMajorVersion();
            int driverMinorVersion = dbMeta.getDriverMinorVersion();

            System.out.println("驱动信息："+driverMajorVersion+"."+driverMinorVersion);

            // table list
            String   catalog          = null;
            String   schemaPattern    = null;
            String   tableNamePattern = "a%";
            String[] types            = null;

            ResultSet rsTables = dbMeta.getTables(
                    conn.getCatalog(), schemaPattern, tableNamePattern, types );

            while(rsTables.next()) {
                // String tableName = result.getString(3);
                String tableName = rsTables.getString("TABLE_NAME");
                System.out.println(tableName);
            }


            // columns in table
            String   tableName  = "blog";
            String   columnNamePattern = null;

            ResultSet rsColumns = dbMeta.getColumns(
                    conn.getCatalog(), null, tableName, columnNamePattern);

            System.out.println("table info: " + tableName);
            while(rsColumns.next()){
                String columnName = rsColumns.getString(4);
                int    columnType = rsColumns.getInt(5);
                System.out.println(columnName+" "+columnType+" ");
            }


            // table primary keys
            ResultSet rsKeys = dbMeta.getPrimaryKeys(
                catalog, schemaPattern, tableName);

            while(rsKeys.next()){
                String columnName = rsKeys.getString(4);
                System.out.println("primary keys: " +  columnName);
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pathVar() {
        System.out.println(PathKit.getRootClassPath());
        System.out.println(PathKit.getWebRootPath());
    }

    @Test
    public void lombok() {
        Person p = new Person();
        p.setCode("ZZ030S");
        p.setName("Zhizhang");
        String s = JsonKit.toJson(p);
        System.out.println(s);

        System.out.println(p.toString());
    }
}
