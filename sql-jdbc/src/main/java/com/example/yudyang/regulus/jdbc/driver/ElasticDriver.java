package com.example.yudyang.regulus.jdbc.driver;

import com.example.yudyang.regulus.jdbc.client.ElasticClientManager;
import com.example.yudyang.regulus.jdbc.client.ElasticClientProvider;
import com.example.yudyang.regulus.jdbc.connection.ElasticConnection;
import com.example.yudyang.regulus.jdbc.constant.JdbcConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;

import java.sql.*;
import java.util.Properties;

public class ElasticDriver implements Driver {

    protected static Logger logger = Logger.getLogger(ElasticDriver.class);

    private RestHighLevelClient restHighLevelClient;
    private ElasticClientProvider elasticClientProvider;

    static {
        try {
            DriverManager.registerDriver(new ElasticDriver());
            System.out.println("register good");
        }catch (SQLException sqlException){
            logger.info("register error, ex "+ ExceptionUtils.getMessage(sqlException));
            System.out.println(sqlException);
        }
    }

    private ElasticDriver(){

    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        synchronized (ElasticDriver.class){
            if (restHighLevelClient!=null){
                return new ElasticConnection(url,info,restHighLevelClient);
            }
            if (elasticClientProvider==null){
                elasticClientProvider = new ElasticClientManager();
            }
            String username=info.getOrDefault("user","").toString();
            String password=info.getOrDefault("password","").toString();
            restHighLevelClient = elasticClientProvider.fromUrl(url,username,password);
            if (restHighLevelClient==null){
                throw new SQLException(String.format("Failed to create elastic client for url[%s]", url));
            }
        }
        return new ElasticConnection(url,info,restHighLevelClient);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return (url != null && StringUtils.containsIgnoreCase(url, JdbcConstants.ELASTIC_DRIVER_PREFIX));
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }

}
