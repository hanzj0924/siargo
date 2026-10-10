package cn.jbolt.common.ureport.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import cn.jbolt.common.config.ProjectConfig;
import cn.jbolt.core.db.datasource.JBoltDataSourceUtil;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.druid.DruidPlugin;

import cn.jbolt.core.base.config.JBoltConfig;

import javax.sql.DataSource;

/** ureport使用的内置平台数据源 */
public class JBoltMainDatasource implements BuildinDatasource {

    @Override
    public String name() {
        return DbKit.MAIN_CONFIG_NAME;
    }

    @Override
    public Connection getConnection() {
        try {
            DataSource dataSource = JBoltDataSourceUtil.me.getDatasource(DbKit.MAIN_CONFIG_NAME);
            return dataSource == null ? null : dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
