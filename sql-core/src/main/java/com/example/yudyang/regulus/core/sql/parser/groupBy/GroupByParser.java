package com.example.yudyang.regulus.core.sql.parser.groupBy;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public interface GroupByParser {
    AggregationBuilder parse(FunctionNameContext functionNameContext);
}
