package com.example.yudyang.regulus.core.sql.model;

import com.example.yudyang.regulus.core.sql.enumerate.SqlBoolOperator;
import com.example.yudyang.regulus.core.sql.enumerate.SqlConditionType;
import com.google.common.collect.Lists;

import java.util.List;

public class SqlCondition {

    private SqlConditionType sqlConditionType;
    private SqlBoolOperator sqlBoolOperator;
    private List<AtomicQuery> queryList;

    public SqlCondition(SqlBoolOperator sqlBoolOperator, List<AtomicQuery> queryList) {
        this.sqlBoolOperator = sqlBoolOperator;
        this.queryList = queryList;
        this.sqlConditionType = SqlConditionType.Combine;
    }

    public SqlCondition(AtomicQuery query, SqlConditionType conditionType){
        this.queryList = Lists.newArrayList(query);
        this.sqlConditionType = conditionType;
    }

    public SqlCondition(AtomicQuery atomicQuery){
        this(atomicQuery,SqlConditionType.Atomic);
    }

    public SqlConditionType getSqlConditionType() {
        return sqlConditionType;
    }

    public SqlBoolOperator getSqlBoolOperator() {
        return sqlBoolOperator;
    }

    public List<AtomicQuery> getQueryList() {
        return queryList;
    }
}
