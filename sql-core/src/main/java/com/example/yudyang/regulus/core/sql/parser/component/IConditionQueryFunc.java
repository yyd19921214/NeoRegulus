package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.enumerate.SqlBoolOperator;
import org.elasticsearch.index.query.QueryBuilder;

@FunctionalInterface
public interface IConditionQueryFunc {
    QueryBuilder buildQuery(String fieldName, SqlBoolOperator operator,Object[] rightVal);

}
