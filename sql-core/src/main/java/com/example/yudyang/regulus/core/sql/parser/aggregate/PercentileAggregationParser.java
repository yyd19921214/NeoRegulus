package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.ArrayList;
import java.util.List;

public class PercentileAggregationParser implements AggregationParser{
    private final String PERCENTILE_METHOD = "percentile";
    @Override
    public void parseAggregateItemClauseContext(AggregateQuery aggregateQuery, ElasticsearchParser.AggregateItemClauseContext aggregateItemClauseContext) {
        if (StringUtils.containsIgnoreCase(PERCENTILE_METHOD,aggregateItemClauseContext.ID().getText())){
            String field = aggregateItemClauseContext.collection().identity(0).getText();
            double[] doubleArray = new double[aggregateItemClauseContext.collection().identity().size()-1];
            List<Double> list = new ArrayList<>();
            for (int i=1;i<aggregateItemClauseContext.collection().identity().size();i++){
                doubleArray[i-1]=Double.valueOf(aggregateItemClauseContext.collection().identity(i).getText());
            }
            aggregateQuery.setAggregationBuilder(AggregationBuilders.percentileRanks(field,doubleArray));
        }

    }
}
