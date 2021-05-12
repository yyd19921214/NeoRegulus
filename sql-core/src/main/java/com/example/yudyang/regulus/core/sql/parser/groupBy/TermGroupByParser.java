package com.example.yudyang.regulus.core.sql.parser.groupBy;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;

public class TermGroupByParser implements GroupByParser{
    private static final String AGG_NAME = "terms";
    @Override
    public AggregationBuilder parse(ElasticsearchParser.FunctionNameContext functionNameContext) {
        if (StringUtils.containsIgnoreCase(AGG_NAME,functionNameContext.functionName.getText())){
            String field = functionNameContext.collection().identity(0).getText();
            int size = 10;
            if (functionNameContext.collection().identity().size()>1){
                size = Integer.parseInt(functionNameContext.collection().identity(1).getText());
            }
            AggregationBuilder builder = AggregationBuilders.terms(field + "_terms").field(field).size(size)
                    .minDocCount(1).shardMinDocCount(1).shardSize(size << 1).order(BucketOrder.count(false));
            return builder;
        }
        return null;
    }

    public AggregationBuilder parse(String field) {
        return AggregationBuilders.terms(field + "_terms").field(field);
    }
}
