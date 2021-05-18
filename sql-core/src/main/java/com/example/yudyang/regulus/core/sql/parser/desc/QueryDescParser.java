package com.example.yudyang.regulus.core.sql.parser.desc;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import com.example.yudyang.regulus.core.sql.parser.ElasticDslContext;
import com.example.yudyang.regulus.core.sql.parser.QueryParser;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;

public class QueryDescParser implements QueryParser {

    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().descOperation()!=null){
            elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.DESC);
            String index = elasticDslContext.getSqlContext().descOperation().tableRef().indexName.getText();
            GetFieldMappingsRequest fieldMappingsRequest = new GetFieldMappingsRequest();
            GetMappingsRequest mappingsRequest = new GetMappingsRequest();
            if (elasticDslContext.getSqlContext().descOperation().identity()!=null){
                String field = elasticDslContext.getSqlContext().descOperation().identity().getText();
                fieldMappingsRequest.indices(index).fields(field);
                elasticDslContext.getElasticSqlParseResult().setGetFieldMappingsRequest(fieldMappingsRequest);
            }
            else{
                mappingsRequest.indices(index);
                elasticDslContext.getElasticSqlParseResult().setGetMappingsRequest(mappingsRequest);
            }
        }
    }
}
