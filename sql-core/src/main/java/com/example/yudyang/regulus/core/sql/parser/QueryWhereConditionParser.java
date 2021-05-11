package com.example.yudyang.regulus.core.sql.parser;

import org.elasticsearch.index.query.QueryBuilder;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
public class QueryWhereConditionParser extends BooleanExpParser implements QueryParser{
    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation()!=null){
            WhereClauseContext whereClauseContext = elasticDslContext.getSqlContext().selectOperation().whereClause();
            parseWhereCondition(elasticDslContext,whereClauseContext);
            // todo function score

        }else if(elasticDslContext.getSqlContext().deleteOperation()!=null){
            WhereClauseContext whereClauseContext = elasticDslContext.getSqlContext().deleteOperation().whereClause();
            parseWhereCondition(elasticDslContext,whereClauseContext);
        }else if(elasticDslContext.getSqlContext().updateOperation()!=null){
            WhereClauseContext whereClauseContext = elasticDslContext.getSqlContext().updateOperation().whereClause();
            parseWhereCondition(elasticDslContext,whereClauseContext);
        }
    }

    private void parseWhereCondition(ElasticDslContext dslContext, WhereClauseContext whereClauseContext){
        if (whereClauseContext != null) {
            ExpressionContext expressionContext = whereClauseContext.expression();
            QueryBuilder queryBuilder = parseBoolQueryExpr(expressionContext);
            dslContext.getElasticSqlParseResult().setWhereCondition(queryBuilder);
            dslContext.getElasticSqlParseResult().getHighlighter().addAll(this.highlighter);
        }
    }
}
