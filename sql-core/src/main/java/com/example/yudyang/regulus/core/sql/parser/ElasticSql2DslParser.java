package com.example.yudyang.regulus.core.sql.parser;
import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.parser.desc.QueryDescParser;
import com.google.common.collect.ImmutableList;
import com.example.yudyang.regulus.core.antlr4.Walker;
import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;

import java.util.List;
public class ElasticSql2DslParser extends AbstractParser {
    public ElasticSqlParseResult parse(String sql) {
        Walker walker = new Walker(sql);
        ElasticsearchParser.SqlContext sqlContext = walker.buildAntlrTree();
        ElasticDslContext elasticDslContext = new ElasticDslContext(sqlContext);
        for (QueryParser parser:buildSqlParseChain()){
            parser.parse(elasticDslContext);
        }
        return elasticDslContext.getElasticSqlParseResult();
    }

    private static List<QueryParser> buildSqlParseChain(){
        return ImmutableList.of(
                new QuerySelectFieldParser(),
                new QueryFromParser(),
                new NeoWhereConditionParser(),
                new NeoAggregateQueryParser(),
                new QueryGroupByParser(),
                new InOrderParser(),

                new QueryDescParser()
        );
    }
}
