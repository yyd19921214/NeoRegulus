package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.BooleanExpParser;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class NestedQueryParser implements ExpressionQueryParser<NestedContext> {
    @Override
    public AtomicQuery parse(NestedContext expression) {
        String nestedPath = expression.nestedClause().nestedPath.getText();
        BooleanExpParser booleanExpParser = new BooleanExpParser();
        QueryBuilder queryBuilder = booleanExpParser.parseBoolQueryExpr(expression.nestedClause().query);
        return new AtomicQuery(QueryBuilders.nestedQuery(nestedPath, queryBuilder, ScoreMode.Avg));
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return NestedContext.class == clazz;
    }
}
