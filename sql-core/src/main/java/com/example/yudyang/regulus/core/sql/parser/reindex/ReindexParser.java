package com.example.yudyang.regulus.core.sql.parser.reindex;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import com.example.yudyang.regulus.core.sql.parser.ElasticDslContext;
import com.example.yudyang.regulus.core.sql.parser.QueryParser;
import com.example.yudyang.regulus.core.sql.parser.component.NeoBooleanExpParser;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.index.reindex.RemoteInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReindexParser implements QueryParser {

    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().reindexOperation() != null) {
            elasticDslContext.getElasticSqlParseResult().setSqlOperation(SqlOperation.REINDEX);
            ElasticsearchParser.ReindexOperationContext reindexOperationContext = elasticDslContext.getSqlContext().reindexOperation();
            ReindexRequest reindexRequest = new ReindexRequest();
            String dstTable = reindexOperationContext.tableRef(0).indexName.getText();
            List<String> srcTableList = new ArrayList<>();
            for (int idx = 1; idx < reindexOperationContext.tableRef().size(); idx++) {
                srcTableList.add(reindexOperationContext.tableRef(idx).indexName.getText());
            }
            reindexRequest.setDestIndex(dstTable);
            reindexRequest.setSourceIndices(srcTableList.toArray(new String[0]));
            if (reindexOperationContext.whereClause() != null) {
                NeoBooleanExpParser booleanExpParser = new NeoBooleanExpParser();
                QueryBuilder builder = booleanExpParser.parseExpression(reindexOperationContext.whereClause().expression());
                if (reindexOperationContext.host != null) {
                    buildQueryScript(reindexRequest, builder, reindexOperationContext);
                } else {
                    reindexRequest.setSourceQuery(builder);
                }
            }
            if (reindexOperationContext.batchClause() != null) {
                reindexRequest.setSourceBatchSize(Integer.parseInt(reindexOperationContext.batchClause().size.getText()));
            }
            if (reindexOperationContext.limitClause() != null) {
                reindexRequest.setMaxDocs(Integer.parseInt(reindexOperationContext.limitClause().size.getText()));
            }
            reindexRequest.setConflicts("proceed");
            reindexRequest.setDestOpType("create");
            reindexRequest.setScroll(TimeValue.timeValueMinutes(10));
            reindexRequest.setShouldStoreResult(true);
            reindexRequest.setSlices(2);
            ActionRequestValidationException validationException = reindexRequest.validate();
            if (validationException != null) {
                throw validationException;
            }
            elasticDslContext.getElasticSqlParseResult().setReindexRequest(reindexRequest);
        }

    }

    private void buildQueryScript(ReindexRequest reindexRequest, QueryBuilder builder, ElasticsearchParser.ReindexOperationContext reindexOperationContext) {
        String[] hostInfo = reindexOperationContext.STRING(0).getText().split("://|:");
        String user = null;
        String password = null;
        if (reindexOperationContext.user != null) {
            user = reindexOperationContext.STRING(1).getText();
            password = reindexOperationContext.STRING(2).getText();
        }
        RemoteInfo remoteInfo = new RemoteInfo(hostInfo[0], hostInfo[1], Integer.parseInt(hostInfo[2]),
                null, new BytesArray(builder.toString()), user, password, Collections.emptyMap(), new TimeValue(1000, TimeUnit.MILLISECONDS),
                new TimeValue(100, TimeUnit.SECONDS));
        reindexRequest.setRemoteInfo(remoteInfo);

    }
}
