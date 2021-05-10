package com.example.yudyang.regulus.core.sql.model;

import org.elasticsearch.search.aggregations.AggregationBuilder;

public class AggregateQuery {

    private AggregationBuilder aggregationBuilder;

    public AggregationBuilder getAggregationBuilder() {
        return aggregationBuilder;
    }

    public void setAggregationBuilder(AggregationBuilder aggregationBuilder) {
        this.aggregationBuilder = aggregationBuilder;
    }
}
