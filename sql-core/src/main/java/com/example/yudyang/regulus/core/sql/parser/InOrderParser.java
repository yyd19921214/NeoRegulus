package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import org.elasticsearch.search.sort.SortOrder;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
public class InOrderParser implements QueryParser{
    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation() != null && elasticDslContext.getSqlContext().selectOperation().orderClause() != null) {

            OrderClauseContext orderClauseContext = elasticDslContext.getSqlContext().selectOperation().orderClause();
            for (OrderContext context: orderClauseContext.order()){
                if (context.nameClause() instanceof FieldNameContext){
                    FieldNameContext fieldNameContext = (FieldNameContext) context.nameClause();
                    String field = fieldNameContext.field.getText();
                    SortOrder sortOrder = (context.ASC() != null ? SortOrder.ASC : SortOrder.DESC);
                    elasticDslContext.getElasticSqlParseResult().getSortOrderMap().put(field,sortOrder);
                }
            }
        }

    }
}
