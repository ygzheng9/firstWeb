package com.demo.model;

import com.jfinal.plugin.activerecord.generator.MetaBuilder;

import javax.sql.DataSource;

public class _MetaBuilder extends MetaBuilder {
    public _MetaBuilder(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean isSkipTable(String tableName) {
        /// return !tableName.startsWith("T_CUSTOM_");

        String wanted = "Blog";
        return !tableName.equalsIgnoreCase(wanted);
    }


}
