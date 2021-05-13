package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.sql.parser.groupBy.GroupByParser;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class QueryHavingParser implements QueryParser{

    private Map<String, AggregationBuilder> param = new HashMap<>();

    @Override
    public void parse(ElasticDslContext elasticDslContext) { }

    public BucketSelectorPipelineAggregationBuilder buildSingleBSBuilder(HavingClauseContext havingClauseContext) {
        HavingExpressionContext havingExpressionContext = havingClauseContext.havingExpression();
        String expStr = extractScript(havingExpressionContext);
        Map<String, String> bucketsPathsMap = new HashMap<>();
        for (String key : param.keySet()) {
            bucketsPathsMap.put(key, key);
        }
        Script script = new Script(expStr);
        BucketSelectorPipelineAggregationBuilder bs =
                PipelineAggregatorBuilders.bucketSelector("bucket_filter", bucketsPathsMap, script);
        return bs;
    }

    private String extractScript(HavingExpressionContext havingExpressionContext) {
        if (havingExpressionContext instanceof LrHavingExprContext) {
            LrHavingExprContext lrHavingExprContext = (LrHavingExprContext) havingExpressionContext;
            return "(" + extractScript(lrHavingExprContext.havingExpression()) + ")";
        } else if (havingExpressionContext instanceof HavingPrimitiveContext) {
            HavingPrimitiveContext havingPrimitiveContext = (HavingPrimitiveContext) havingExpressionContext;
            return havingPrimitiveContext.getText();
        } else if (havingExpressionContext instanceof FunctionExprContext) {
            FunctionExprContext functionExprContext = (FunctionExprContext) havingExpressionContext;
            List<GroupByParser> groupByParserList = QueryGroupByParser.buildParseChain();
            for (GroupByParser parser : groupByParserList) {
                AggregationBuilder aggregationBuilder = parser.parse(functionExprContext.funcName.getText(), functionExprContext.collection().identity());
                if (aggregationBuilder != null) {
                    param.put(aggregationBuilder.getName(), aggregationBuilder);
                    return "params." + aggregationBuilder.getName();
                }
            }
        } else if (havingExpressionContext instanceof HavingBinaryContext) {
            HavingBinaryContext havingBinaryContext = (HavingBinaryContext) havingExpressionContext;
            if (havingBinaryContext.operator.getType() == AND) {
                return extractScript(havingBinaryContext.leftExpr) + " && " + extractScript(havingBinaryContext.rightExpr);
            } else if (havingBinaryContext.operator.getType() == OR) {
                return extractScript(havingBinaryContext.leftExpr) + " || " + extractScript(havingBinaryContext.rightExpr);
            } else {
                return extractScript(havingBinaryContext.leftExpr) + havingBinaryContext.operator.getText() + extractScript(havingBinaryContext.rightExpr);
            }
        }
        return "";
    }

    public Map<String, AggregationBuilder> getParam() {
        return param;
    }

}
