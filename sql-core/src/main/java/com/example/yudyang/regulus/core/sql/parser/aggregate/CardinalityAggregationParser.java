package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;

public class CardinalityAggregationParser implements AggregationParser{
    private final String CARDINALITY_METHOD = "cardinality";

    @Override
    public void parseAggregateItemClauseContext(AggregateQuery aggregateQuery, ElasticsearchParser.AggregateItemClauseContext aggregateItemClauseContext) {
        if (StringUtils.containsIgnoreCase(CARDINALITY_METHOD, aggregateItemClauseContext.ID().getText())) {
            String field = aggregateItemClauseContext.collection().identity(0).getText();
            aggregateQuery.setAggregationBuilder(AggregationBuilders.cardinality(CARDINALITY_METHOD + "-" + field).field(field));
        }
    }
}
