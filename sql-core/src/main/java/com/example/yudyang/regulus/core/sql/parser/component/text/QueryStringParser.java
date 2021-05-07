package com.example.yudyang.regulus.core.sql.parser.component.text;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class QueryStringParser implements ExpressionQueryParser<ElasticsearchParser.QueryStringClauseContext> {

    @Override
    public AtomicQuery parse(QueryStringClauseContext expression) {
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(StringManager.removeStringSymbol(expression.STRING().getText()));
        return new AtomicQuery(queryBuilder);
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return QueryStringClauseContext.class == clazz;

    }
}
