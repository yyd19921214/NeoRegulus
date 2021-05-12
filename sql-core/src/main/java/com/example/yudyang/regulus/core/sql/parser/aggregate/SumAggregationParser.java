package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

public class SumAggregationParser implements AggregationParser{
    private final String SUM_METHOD = "sum";

    @Override
    public AggregationBuilder parseAggregateItemClauseContext(AggregateQuery aggregateQuery, ElasticsearchParser.AggregateItemClauseContext aggregateItemClauseContext) {
        if (StringUtils.containsIgnoreCase(SUM_METHOD, aggregateItemClauseContext.ID().getText())) {
            String field = aggregateItemClauseContext.collection().identity(0).getText();
            AggregationBuilder builder = AggregationBuilders.sum(SUM_METHOD + "_" + field).field(field);
            aggregateQuery.setAggregationBuilder(builder);
            return builder;
        }
        return null;
    }
}
