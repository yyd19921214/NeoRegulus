package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import org.elasticsearch.index.query.QueryBuilder;

@FunctionalInterface
public interface IConditionQueryFunc {
    QueryBuilder buildQuery(String fieldName, SqlOperator operator, Object[] rightVal);

}
