package com.example.yudyang.regulus.jdbc.statement;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import com.example.yudyang.regulus.core.sql.utils.CoreConstants;
import com.example.yudyang.regulus.jdbc.connection.ElasticConnection;
import com.example.yudyang.regulus.jdbc.constant.JdbcConstants;
import com.example.yudyang.regulus.jdbc.elastic.JdbcResultExtractor;
import com.example.yudyang.regulus.jdbc.elastic.JdbcSearchResponse;
import com.example.yudyang.regulus.jdbc.resultset.ElasticResultSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.reindex.BulkByScrollResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ElasticStatement extends AbstractStatement {

    protected static Logger logger = Logger.getLogger(ElasticStatement.class);

    protected ElasticConnection connection;
    private ElasticSql2DslParser elasticSql2DslParser;
    private ResultSet resultSet;
    // scrollsearch中需要保存aliasmap
    private Map<String, String> aliasMap;

    public ElasticStatement(ElasticConnection connection) {
        this.connection = connection;
        this.elasticSql2DslParser = new ElasticSql2DslParser();
    }

    @Override
    protected ResultSet executeQuery(String sql, Object[] args) throws SQLException {
        sql = prepareSQL(sql, args);
        return executeQuery(sql);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        ElasticSqlParseResult parseResult = elasticSql2DslParser.parse(sql);
        if (SqlOperation.SELECT != parseResult.getSqlOperation()) {
            throw new IllegalArgumentException("only select operation supported in query method");
        }
        try {
            checkDatabase(parseResult.getIndices());
            SearchResponse response = connection.getRestHighLevelClient().search(parseResult.getSearchRequest(), RequestOptions.DEFAULT);
            JdbcResultExtractor jdbcResultExtractor = new JdbcResultExtractor();
            this.aliasMap = parseResult.getAliasMap();
            JdbcSearchResponse jdbcSearchResponse = jdbcResultExtractor.parseSearchResponse(response, parseResult.getAliasMap());
            this.resultSet = new ElasticResultSet(this, jdbcSearchResponse);
        } catch (IOException e) {
            logger.error("@executeQuery, ex info is " + e.getMessage());
        }
        return this.resultSet;
    }

    public ResultSet executeScrollQuery(String sql, String scrollId) throws SQLException, IOException {
        SearchResponse searchResponse;
        JdbcResultExtractor jdbcResultExtractor = new JdbcResultExtractor();

        if (StringUtils.isEmpty(scrollId)) {
            ElasticSqlParseResult parseResult = elasticSql2DslParser.parse(sql);
            assert parseResult.getSqlOperation() == SqlOperation.SELECT;
            checkDatabase(parseResult.getIndices());
            parseResult.getSearchRequest().scroll(JdbcConstants.SCROLL);
            parseResult.getSearchRequest().source().size(JdbcConstants.DEFAULT_SCROLL_SIZE);
            parseResult.getSearchRequest().source().trackTotalHits(true);
            aliasMap = parseResult.getAliasMap();
            searchResponse = connection.getRestHighLevelClient().search(parseResult.getSearchRequest(), RequestOptions.DEFAULT);
        } else {
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(JdbcConstants.SCROLL);
            searchResponse = connection.getRestHighLevelClient().scroll(searchScrollRequest, RequestOptions.DEFAULT);
        }
        JdbcSearchResponse jdbcSearchResponse = jdbcResultExtractor.parseScrollSearchResponse(searchResponse, this.aliasMap);
        if (StringUtils.isEmpty(jdbcSearchResponse.getSql())) {
            jdbcSearchResponse.setSql(sql);
        }
        resultSet = new ElasticResultSet(this, jdbcSearchResponse);
        return resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        ElasticSqlParseResult elasticSqlParseResult = elasticSql2DslParser.parse(sql);
        checkDatabase(elasticSqlParseResult.getIndices());
        try {
            switch (elasticSqlParseResult.getSqlOperation()) {
                case INSERT: {
                    connection.getRestHighLevelClient().index(elasticSqlParseResult.getIndexRequest(), RequestOptions.DEFAULT);
                    return 1;
                }
                case UPDATE: {
                    connection.getRestHighLevelClient().update(elasticSqlParseResult.getUpdateRequest(), RequestOptions.DEFAULT);
                    return 1;
                }
                case UPDATE_BY_QUERY: {
                    BulkByScrollResponse response = connection.getRestHighLevelClient().updateByQuery(elasticSqlParseResult.getUpdateByQueryRequest(), RequestOptions.DEFAULT);
                    return (int) response.getUpdated();
                }
                case DELETE:
                    connection.getRestHighLevelClient().delete(elasticSqlParseResult.getDeleteRequest(), RequestOptions.DEFAULT);
                    return 1;
                case DELETE_BY_QUERY:
                    BulkByScrollResponse response = connection.getRestHighLevelClient().deleteByQuery(elasticSqlParseResult.getDeleteByQueryRequest(), RequestOptions.DEFAULT);
                    return (int) response.getDeleted();
                default: {
                    throw new SQLException("only support [insert,update,delete] operation");
                }
            }
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdate(sql);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        executeQuery(sql);
        return true;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        executeQuery(sql, columnNames);
        return true;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
        super.close();
    }

    @Override
    public boolean isClosed() {
        return connection.isClosed();
    }

    private String prepareSQL(String sql, Object[] args) {
        int count = StringUtils.countMatches(sql, CoreConstants.COND);
        assert count == args.length;
        for (Object obj : args) {
            sql = sql.replaceFirst("\\?", obj.toString());
        }
        return sql;
    }

    private void checkDatabase(List<String> indices) throws SQLException {
        if (!connection.getDatabases().containsAll(indices)) {
            throw new SQLException("[invalid] database queried must be contained in " + connection.getDatabases());
        }
    }
}
