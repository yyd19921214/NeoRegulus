package com.example.yudyang.regulus.jdbc.connection;

import com.example.yudyang.regulus.jdbc.constant.JdbcConstants;
import com.example.yudyang.regulus.jdbc.driver.ElasticDriver;
import com.example.yudyang.regulus.jdbc.metadata.ElasticMetaData;
import com.example.yudyang.regulus.jdbc.statement.ElasticStatement;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ElasticConnection extends AbstractConnection{

    protected static Logger logger = Logger.getLogger(ElasticConnection.class);

    private RestHighLevelClient restHighLevelClient;

    private List<String> databases;

    ElasticConnection(String url, Properties properties,RestHighLevelClient client){
        super(url,properties);
        this.restHighLevelClient = client;
        String[] items = url.split("/");
        String database= items[items.length-1];
        if (database.contains(JdbcConstants.COND)){
            database = database.split("[?]")[0];
        }
        this.databases = Arrays.asList(database.split("[,]"));
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new ElasticStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
//        return super.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }


    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new ElasticMetaData(this.url,this.properties.getOrDefault("user","").toString());
    }

    public List<String> getDatabases() {
        return databases;
    }

    @Override
    public void close() throws SQLException {
        try {
            restHighLevelClient.close();
            closed = true;
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }


}
