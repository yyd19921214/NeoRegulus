package com.example.yudyang.regulus.core.sql.parser.component.like;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.component.AbstractQueryParser;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class RegexpLikeQueryParser extends AbstractQueryParser implements LikeQueryParser{

    @Override
    public AtomicQuery parse(ElasticsearchParser.LikeClauseContext expression) {
        SqlOperator sqlOperator = expression.not != null ? SqlOperator.NotLike : SqlOperator.Like;
        return parseCondition(expression, sqlOperator, new String[]{expression.pattern.getText()}, ((fieldName, operator, rightVal) -> {
            String targetVal = StringManager.removeStringSymbol(rightVal[0].toString());
            QueryBuilder queryBuilder = QueryBuilders.regexpQuery(fieldName, targetVal);
            if (sqlOperator == SqlOperator.Like) {
                return queryBuilder;
            } else {
                return QueryBuilders.boolQuery().mustNot(queryBuilder);
            }
        }));
    }
}
