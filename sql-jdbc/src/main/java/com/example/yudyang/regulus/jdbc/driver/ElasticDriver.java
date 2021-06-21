package com.example.yudyang.regulus.jdbc.driver;

import com.example.yudyang.regulus.core.sql.parser.BasicParser;
import com.example.yudyang.regulus.jdbc.constant.JdbcConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public class ElasticDriver implements Driver {

    protected static Logger logger = Logger.getLogger(ElasticDriver.class);

    static {
        try {
            DriverManager.registerDriver(new ElasticDriver());

        }catch (SQLException sqlException){
            logger.info("register error, ex "+ ExceptionUtils.getMessage(sqlException));
        }
    }

    private ElasticDriver(){

    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return null;
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
