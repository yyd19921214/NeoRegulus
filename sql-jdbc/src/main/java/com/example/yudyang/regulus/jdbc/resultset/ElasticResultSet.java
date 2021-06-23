package com.example.yudyang.regulus.jdbc.resultset;

import com.example.yudyang.regulus.core.sql.utils.FlatMapUtils;
import com.example.yudyang.regulus.jdbc.elastic.JdbcScrollSearchResponse;
import com.example.yudyang.regulus.jdbc.elastic.JdbcSearchResponse;
import com.example.yudyang.regulus.jdbc.statement.ElasticStatement;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class ElasticResultSet extends AbstractResultSet {

    private JdbcSearchResponse response;

    private int rowCursor = -1;

    private static final ElasticResultSetMetaData RESULT_SET_META_DATA = new ElasticResultSetMetaData();

    public ElasticResultSet(Statement statement, JdbcSearchResponse response) {
        super(statement);
        this.response = response;
    }

    @Override
    public boolean next() throws SQLException {
        rowCursor++;
        int total = getFetchSize();
        if (response instanceof JdbcScrollSearchResponse && rowCursor == total) {
            ElasticStatement statement = (ElasticStatement) this.getStatement();
            try {
                ElasticResultSet resultSet = (ElasticResultSet) statement.executeScrollQuery(response.getSql(), ((JdbcScrollSearchResponse) response).getScrollId());
                response = resultSet.response;
                rowCursor = 0;
            } catch (IOException e) {
                throw new SQLException(e.getMessage());
            }
        }

        return rowCursor + 1 <= total;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        String fieldName = columnLabel;
        if (this.response.getAliasMap().containsKey(columnLabel)) {
            fieldName = this.response.getAliasMap().get(fieldName);
        }
        Object result = FlatMapUtils.flatGet(fieldName, this.response.getResult().get(rowCursor));
        Objects.requireNonNull(result);
        return result.toString();
    }


    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return Boolean.parseBoolean(getString(columnLabel));
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return FlatMapUtils.flatGet(columnLabel, this.response.getResult().get(rowCursor));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return new Date(getLong(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return new Time(getLong(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return new Timestamp(getLong(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return Long.parseLong(getString(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return Integer.parseInt(getString(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return Double.parseDouble(getString(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return Short.parseShort(getString(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return Float.parseFloat(getString(columnLabel));
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return RESULT_SET_META_DATA;
    }

    @Override
    public void beforeFirst() throws SQLException {
        rowCursor = 0;
    }

    @Override
    public void afterLast() throws SQLException {
        rowCursor = response.getSize();
    }

    @Override
    public int getRow() throws SQLException {
        return rowCursor;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return response.getSize();
    }
}
