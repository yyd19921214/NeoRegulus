package com.example.yudyang.regulus.core.sql.parser.groupBy;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class MaxGroupByParser implements GroupByParser {

    private static final String AGG_NAME = "max";

    @Override
    public AggregationBuilder parse(FunctionNameContext functionNameContext) {
        if (StringUtils.equalsIgnoreCase(AGG_NAME, functionNameContext.functionName.getText())) {
            String field = functionNameContext.params.identity(0).getText();
            return AggregationBuilders.max(AGG_NAME + "_" + field).field(field);
        }
        return null;
    }
}
