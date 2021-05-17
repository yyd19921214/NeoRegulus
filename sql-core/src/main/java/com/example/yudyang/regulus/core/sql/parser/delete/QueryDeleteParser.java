package com.example.yudyang.regulus.core.sql.parser.delete;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import com.example.yudyang.regulus.core.sql.parser.ElasticDslContext;
import com.example.yudyang.regulus.core.sql.parser.QueryParser;
import com.example.yudyang.regulus.core.sql.parser.component.NeoBooleanExpParser;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class QueryDeleteParser implements QueryParser {
    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().deleteOperation() != null) {
            if (elasticDslContext.getSqlContext().deleteOperation().identifyClause() != null) {
                parseDelete(elasticDslContext);
            } else {
                parseQueryDelete(elasticDslContext);
            }
        }
    }

    private void parseDelete(ElasticDslContext elasticDslContext) {
        elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.DELETE);
        ElasticsearchParser.DeleteOperationContext deleteOperationContext = elasticDslContext.getSqlContext().deleteOperation();
        DeleteRequest deleteRequest = new DeleteRequest(deleteOperationContext.tableRef().get(0).indexName.getText());
        deleteRequest.id(StringManager.removeStringSymbol(deleteOperationContext.identifyClause().id.getText()));
        if (deleteOperationContext.routingClause() != null) {
            deleteRequest.routing(StringManager.removeStringSymbol(deleteOperationContext.routingClause().STRING(0).getText()));
        }
        elasticDslContext.getElasticSqlParseResult().setDeleteRequest(deleteRequest);
    }


    private void parseQueryDelete(ElasticDslContext elasticDslContext) {
        elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.DELETE_BY_QUERY);
        ElasticsearchParser.DeleteOperationContext deleteOperationContext = elasticDslContext.getSqlContext().deleteOperation();
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        List<String> indexList = deleteOperationContext.tableRef().stream().map(i -> i.indexName.getText()).collect(toList());
        deleteByQueryRequest.indices(indexList.toArray(new String[0]));
        if (deleteOperationContext.whereClause() != null) {
            NeoBooleanExpParser booleanExpParser = new NeoBooleanExpParser();
            deleteByQueryRequest.setQuery(booleanExpParser.parseExpression(deleteOperationContext.whereClause().expression()));
        } else {
            deleteByQueryRequest.setQuery(QueryBuilders.matchAllQuery());
        }
        if (deleteOperationContext.routingClause() != null) {
            deleteByQueryRequest.setRouting(StringManager.removeStringSymbol(deleteOperationContext.routingClause().STRING(0).getText()));
        }
        if (deleteOperationContext.batchClause() != null) {
            deleteByQueryRequest.setBatchSize(Integer.parseInt(deleteOperationContext.batchClause().size.getText()));
        } else {
            deleteByQueryRequest.setBatchSize(10);
        }
        if (deleteOperationContext.limitClause() != null) {
            deleteByQueryRequest.setMaxDocs(Integer.parseInt(deleteOperationContext.limitClause().size.getText()));
        }
        elasticDslContext.getElasticSqlParseResult().setDeleteByQueryRequest(deleteByQueryRequest);
    }
}
