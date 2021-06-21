package com.example.yudyang.regulus.jdbc.statement;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import com.example.yudyang.regulus.jdbc.connection.ElasticConnection;
import com.example.yudyang.regulus.jdbc.driver.ElasticDriver;
import com.example.yudyang.regulus.jdbc.elastic.JdbcResultExtractor;
import com.example.yudyang.regulus.jdbc.elastic.JdbcSearchResponse;
import com.example.yudyang.regulus.jdbc.resultset.ElasticResultSet;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ElasticStatement extends AbstractStatement{

    protected static Logger logger = Logger.getLogger(ElasticStatement.class);

    protected ElasticConnection connection;
    private ElasticSql2DslParser elasticSql2DslParser;
    private ResultSet resultSet;

    public ElasticStatement(ElasticConnection connection) {
        this.connection = connection;
        this.elasticSql2DslParser = new ElasticSql2DslParser();
    }

    @Override
    protected ResultSet executeQuery(String sql, Object[] args) throws SQLException {
        ElasticSqlParseResult parseResult = elasticSql2DslParser.parse(sql);
//        SearchResponse response = connection.getRestHighLevelClient().search(parseResult.getSearchRequest(),RequestOptions.DEFAULT);

        return null;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        ElasticSqlParseResult parseResult = elasticSql2DslParser.parse(sql);
        if (SqlOperation.SELECT!=parseResult.getSqlOperation()){
            throw new IllegalArgumentException("only select operation supported in query method");
        }
        try {
            SearchResponse response = connection.getRestHighLevelClient().search(parseResult.getSearchRequest(), RequestOptions.DEFAULT);
            JdbcResultExtractor jdbcResultExtractor = new JdbcResultExtractor();
            JdbcSearchResponse jdbcSearchResponse = jdbcResultExtractor.parseSearchResponse(response, parseResult.getAliasMap());
            this.resultSet = new ElasticResultSet(this,jdbcSearchResponse);
        } catch (IOException e) {
            logger.error("@executeQuery, ex info is "+e.getMessage());
        }
        return this.resultSet;
    }

    public ResultSet executeScrollQuery(String sql, String scrollId) throws SQLException, IOException {
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return super.executeUpdate(sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return super.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return super.execute(sql, columnNames);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {
        super.close();
    }

    @Override
    public boolean isClosed() {
        return super.isClosed();
    }
}
