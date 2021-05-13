package com.example.yudyang.regulus.core.sql.parser.groupBy;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public interface GroupByParser {
    AggregationBuilder parse(FunctionNameContext functionNameContext);

    AggregationBuilder parse(String name, List<IdentityContext> identityContexts);
}
