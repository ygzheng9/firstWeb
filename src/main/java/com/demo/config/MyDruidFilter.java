package com.demo.config;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.jfinal.kit.StrKit;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author ygzheng
 * 打印带参数的 sql
 */
public class MyDruidFilter extends FilterAdapter {
    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);
        Map<Integer, JdbcParameter> lParameters = statement.getParameters();
        String lSql = statement.getBatchSql();
        if (StrKit.notBlank(lSql)) {
            for (Map.Entry<Integer, JdbcParameter> lEntry : lParameters.entrySet()) {
                JdbcParameter lValue = lEntry.getValue();
                if (lValue == null) {
                    continue;
                }
                Object lO = lValue.getValue();
                if (lO == null) {
                    continue;
                }
                String lS = lO.toString();
                lSql = lSql.replaceFirst("\\?", lS);
            }
            System.out.println("MyDruidFilter: \n>>>>>>>>>>>> \n" + lSql + "\n<<<<<<<<<<<<<<<\n");
        }
    }
}
