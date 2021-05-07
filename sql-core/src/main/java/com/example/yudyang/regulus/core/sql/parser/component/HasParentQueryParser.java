package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.BooleanExpParser;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
public class HasParentQueryParser implements ExpressionQueryParser<HasParentClauseContext> {
    @Override
    public AtomicQuery parse(ElasticsearchParser.HasParentClauseContext expression) {
        String type = expression.type.getText();
        BooleanExpParser booleanExpParser = new BooleanExpParser();
        QueryBuilder queryBuilder = booleanExpParser.parseBoolQueryExpr(expression.query);
        QueryBuilder joinBuilder = JoinQueryBuilders.hasParentQuery(type,queryBuilder, false);
        AtomicQuery atomicQuery = new AtomicQuery(joinBuilder);
        atomicQuery.getHighlighter().addAll(booleanExpParser.highlighter);
        return atomicQuery;
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return HasParentQueryParser.class==clazz;
    }
}
