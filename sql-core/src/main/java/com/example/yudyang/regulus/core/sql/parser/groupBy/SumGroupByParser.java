package com.example.yudyang.regulus.core.sql.parser.groupBy;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

public class SumGroupByParser implements GroupByParser{

    private static final String AGG_NAME = "sum";

    @Override
    public AggregationBuilder parse(ElasticsearchParser.FunctionNameContext functionNameContext) {
        if (StringUtils.equalsIgnoreCase(AGG_NAME, functionNameContext.functionName.getText())) {
            String field = functionNameContext.params.identity(0).getText();
            return AggregationBuilders.sum(AGG_NAME + "_" + field).field(field);
        }
        return null;

    }
}
