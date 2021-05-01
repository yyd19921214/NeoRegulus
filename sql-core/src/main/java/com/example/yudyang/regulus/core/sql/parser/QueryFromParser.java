package com.example.yudyang.regulus.core.sql.parser;


import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
import java.util.ArrayList;
import java.util.List;

public class QueryFromParser implements QueryParser{
    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        List<String> indices = new ArrayList<>();
        if (elasticDslContext.getSqlContext().selectOperation()!=null){
            elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.SELECT);
            SelectOperationContext selectOperationContext = elasticDslContext.getSqlContext().selectOperation();
            for (TableRefContext tableRefContext:selectOperationContext.tableRef()){
                indices.add(tableRefContext.indexName.getText());
            }
            if (selectOperationContext.trackTotalClause()!=null){
                elasticDslContext.getElasticSqlParseResult().setTrackTotalHits(true);
            }
            elasticDslContext.getElasticSqlParseResult().getIndices().addAll(indices);

        }else if(elasticDslContext.getSqlContext().updateOperation()!=null){
            elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.UPDATE);
        }else if(elasticDslContext.getSqlContext().deleteOperation()!=null){
            elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.DELETE);
        }

    }
}
