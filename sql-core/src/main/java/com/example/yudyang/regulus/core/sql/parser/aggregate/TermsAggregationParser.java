package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;

public class TermsAggregationParser implements AggregationParser{
    private final String AGG_TERMS_METHOD = "terms";

    @Override
    public AggregationBuilder parseAggregateItemClauseContext(AggregateQuery aggregateQuery, ElasticsearchParser.AggregateItemClauseContext aggregateItemClauseContext) {
        if (StringUtils.containsIgnoreCase(AGG_TERMS_METHOD,aggregateItemClauseContext.ID().getText())){
            String field = aggregateItemClauseContext.collection().identity(0).getText();
            int size = 10;
            if (aggregateItemClauseContext.collection().identity().size()>1){
                size = Integer.parseInt(aggregateItemClauseContext.collection().identity(1).getText());
            }
            AggregationBuilder builder = AggregationBuilders.terms(field + "_terms").field(field).size(size)
                    .minDocCount(1).shardMinDocCount(1).shardSize(size << 1).order(BucketOrder.count(false));
            aggregateQuery.setAggregationBuilder(builder);
            return builder;
        }
        return null;

    }
}
