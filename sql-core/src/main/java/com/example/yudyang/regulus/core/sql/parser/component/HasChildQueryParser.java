package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.BooleanExpParser;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
public class HasChildQueryParser implements ExpressionQueryParser<HasChildClauseContext> {

    @Override
    public AtomicQuery parse(HasChildClauseContext expression) {
        String type = expression.type.getText();
        NeoBooleanExpParser booleanExpParser = new NeoBooleanExpParser();
        QueryBuilder queryBuilder = booleanExpParser.parseExpression(expression.query);
        QueryBuilder joinBuilder = JoinQueryBuilders.hasChildQuery(type,queryBuilder, ScoreMode.Avg);
        AtomicQuery atomicQuery = new AtomicQuery(joinBuilder);
        atomicQuery.getHighlighter().addAll(booleanExpParser.getHighlighters());
        return atomicQuery;
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return HasChildClauseContext.class==clazz;
    }
}
