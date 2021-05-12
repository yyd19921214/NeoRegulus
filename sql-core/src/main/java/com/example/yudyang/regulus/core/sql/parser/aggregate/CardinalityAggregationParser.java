package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

public class CardinalityAggregationParser implements AggregationParser{
    private final String CARDINALITY_METHOD = "cardinality";

    @Override
    public AggregationBuilder parseAggregateItemClauseContext(AggregateQuery aggregateQuery, ElasticsearchParser.AggregateItemClauseContext aggregateItemClauseContext) {
        if (StringUtils.containsIgnoreCase(CARDINALITY_METHOD, aggregateItemClauseContext.ID().getText())) {
            String field = aggregateItemClauseContext.collection().identity(0).getText();
            AggregationBuilder builder = AggregationBuilders.cardinality(CARDINALITY_METHOD + "_" + field).field(field);
            aggregateQuery.setAggregationBuilder(builder);
            return builder;
        }
        return null;
    }
}
