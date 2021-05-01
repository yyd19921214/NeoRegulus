package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import org.elasticsearch.index.query.QueryBuilders;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class ExistQueryParser implements ExpressionQueryParser<IsClauseContext> {

    @Override
    public AtomicQuery parse(IsClauseContext expression) {
        String field = expression.field.getText();
        if (expression.not != null) {
            return new AtomicQuery(QueryBuilders.existsQuery(field));
        }
        return new AtomicQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(field)));
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return clazz == InClauseContext.class;
    }
}
