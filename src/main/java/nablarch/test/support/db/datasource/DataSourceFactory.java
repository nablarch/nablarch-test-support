package nablarch.test.support.db.datasource;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import nablarch.core.util.StringUtil;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import nablarch.core.repository.di.ComponentFactory;

public class DataSourceFactory implements ComponentFactory<DataSource> {

    private static DataSource dataSource = null;

    private String user;

    private String password;

    private String url;
    
    private String driverClassName;

    private String initialSize = "2";

    private String maxIdle;

    private DbInitializer dbInitializer;

    @Override
    public synchronized DataSource createObject() {

        if (dataSource != null) {
            return dataSource;
        }

        try {
            
            Properties properties = new Properties();
            properties.setProperty("driverClassName", driverClassName);
            properties.setProperty("username", user);
            properties.setProperty("password", password);
            properties.setProperty("url", url);
            properties.setProperty("initialSize", initialSize);
            properties.setProperty("maxActive", "30");
            if (StringUtil.hasValue(maxIdle)) {
                properties.setProperty("maxIdle", maxIdle);
            }

            properties.setProperty("timeBetweenEvictionRunsMillis", "5000");
            dataSource = BasicDataSourceFactory.createDataSource(properties);
            final Connection connection = dataSource.getConnection();
            try {
                if (dbInitializer != null) {
                    dbInitializer.initialize(connection);
                }
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setInitialSize(String initialSize) {
        this.initialSize = initialSize;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setDriverClassName(final String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setDbInitializer(final DbInitializer dbInitializer) {
        this.dbInitializer = dbInitializer;
    }
}

