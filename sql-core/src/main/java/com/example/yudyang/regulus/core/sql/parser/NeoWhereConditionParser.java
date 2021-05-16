package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.parser.component.NeoBooleanExpParser;
import org.elasticsearch.index.query.QueryBuilder;

public class NeoWhereConditionParser implements QueryParser{

    private NeoBooleanExpParser neoBooleanExpParser;

    public NeoWhereConditionParser() {
        this.neoBooleanExpParser = new NeoBooleanExpParser();
    }

    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation() != null) {
            ElasticsearchParser.WhereClauseContext whereClauseContext = elasticDslContext.getSqlContext().selectOperation().whereClause();
            parseWhereCondition(elasticDslContext, whereClauseContext);
        } else if (elasticDslContext.getSqlContext().deleteOperation() != null) {
            ElasticsearchParser.WhereClauseContext whereClauseContext = elasticDslContext.getSqlContext().deleteOperation().whereClause();
            parseWhereCondition(elasticDslContext, whereClauseContext);
        } else if (elasticDslContext.getSqlContext().updateOperation() != null) {
            ElasticsearchParser.WhereClauseContext whereClauseContext = elasticDslContext.getSqlContext().updateOperation().whereClause();
            parseWhereCondition(elasticDslContext, whereClauseContext);
        }

    }

    private void parseWhereCondition(ElasticDslContext dslContext, ElasticsearchParser.WhereClauseContext whereClauseContext){
        if (whereClauseContext != null) {
            ElasticsearchParser.ExpressionContext expressionContext = whereClauseContext.expression();
            QueryBuilder queryBuilder = neoBooleanExpParser.parseExpression(expressionContext);
            dslContext.getElasticSqlParseResult().setWhereCondition(queryBuilder);
            dslContext.getElasticSqlParseResult().getHighlighter().addAll(neoBooleanExpParser.getHighlighters());
        }
    }


}
