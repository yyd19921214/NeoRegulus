package com.example.yudyang.regulus.core.sql.parser.groupBy;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;

public class MinGroupByParser implements GroupByParser {

    private static final String AGG_NAME = "min";

    @Override
    public AggregationBuilder parse(ElasticsearchParser.FunctionNameContext functionNameContext) {
        if (StringUtils.equalsIgnoreCase(AGG_NAME, functionNameContext.functionName.getText())) {
            String field = functionNameContext.params.identity(0).getText();
            return AggregationBuilders.min(AGG_NAME + "_" + field).field(field);
        }
        return null;

    }

    @Override
    public AggregationBuilder parse(String name,  List<ElasticsearchParser.IdentityContext> identityContexts) {
        if (StringUtils.equalsIgnoreCase(AGG_NAME, name)) {
            String field = identityContexts.get(0).getText();
            return AggregationBuilders.min(AGG_NAME + "_" + field).field(field);
        }
        return null;
    }
}
