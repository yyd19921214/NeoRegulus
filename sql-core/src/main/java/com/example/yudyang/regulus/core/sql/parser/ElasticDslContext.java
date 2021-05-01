package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;

public class ElasticDslContext {

    private ElasticsearchParser.SqlContext sqlContext;

    private ElasticSqlParseResult elasticSqlParseResult;

    public ElasticDslContext(ElasticsearchParser.SqlContext sqlContext) {
        this.sqlContext = sqlContext;
        this.elasticSqlParseResult = new ElasticSqlParseResult() {
        };
    }

    public ElasticsearchParser.SqlContext getSqlContext() {
        return sqlContext;
    }

    public void setSqlContext(ElasticsearchParser.SqlContext sqlContext) {
        this.sqlContext = sqlContext;
    }

    public ElasticSqlParseResult getElasticSqlParseResult() {
        return elasticSqlParseResult;
    }

    public void setElasticSqlParseResult(ElasticSqlParseResult elasticSqlParseResult) {
        this.elasticSqlParseResult = elasticSqlParseResult;
    }
}
