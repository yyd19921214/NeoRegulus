package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import com.example.yudyang.regulus.core.sql.parser.ElasticDslContext;
import com.example.yudyang.regulus.core.sql.parser.QueryParser;
import com.google.common.collect.Lists;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class NeoAggregateQueryParser implements QueryParser {

    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation() != null && elasticDslContext.getSqlContext().selectOperation().aggregateByClause() != null) {
            AggregateByClauseContext aggregateByClauseContext = elasticDslContext.getSqlContext().selectOperation().aggregateByClause();
            elasticDslContext.getElasticSqlParseResult().getGroupBy().addAll(parseAggregateByClause(aggregateByClauseContext));
        }
    }

    /**
     * return separated aggregate operation
     *
     * @return
     */
    private List<AggregationBuilder> parseAggregateByClause(AggregateByClauseContext aggregateByClauseContext) {
        return parseAggregationClause(aggregateByClauseContext.aggregationClause());
    }

    private List<AggregationBuilder> parseAggregationClause(AggregationClauseContext aggregationClauseContext) {
        List<AggregationBuilder> aggregationBuilderList = new ArrayList<>();
        if (aggregationClauseContext.aggregateItemClause() != null) {
            parseAggregateItemClause(aggregationBuilderList,aggregationClauseContext.aggregateItemClause());
            return aggregationBuilderList;
        } else if (aggregationClauseContext.nestedAggregationClause() != null) {
            parseNestedAggregationClause(aggregationBuilderList,aggregationClauseContext.nestedAggregationClause());
            return aggregationBuilderList;
        }
        throw new IllegalArgumentException("not supported aggregate grammar");
    }

    private void parseAggregateItemClause(List<AggregationBuilder> aggregationBuilderList, AggregateItemClauseContext aggregateItemClauseContext) {
        AggregateQuery aggregateQuery = new AggregateQuery();
        AggregationBuilder aggregationBuilder = null;
        for (AggregationParser parser : buildParserChain()) {
            aggregationBuilder = parser.parseAggregateItemClauseContext(aggregateQuery, aggregateItemClauseContext);
            if (aggregationBuilder != null) {
                break;
            }
        }
        if (aggregationBuilder == null) {
            throw new IllegalArgumentException("can not extract aggregate by context");
        }
        aggregationBuilderList.add(aggregationBuilder);

        // process multi aggregation clause
        if (aggregateItemClauseContext.aggregationClause().size() > 0) {
            for (AggregationClauseContext ctx : aggregateItemClauseContext.aggregationClause()) {
                aggregationBuilderList.addAll(parseAggregationClause(ctx));
            }
        }

        // process sub aggregation clause
        if (aggregateItemClauseContext.subAggregationClause().size()>0){
            for (SubAggregationClauseContext ctx:aggregateItemClauseContext.subAggregationClause()){
                aggregationBuilder.subAggregation(parseSubAggregationClause(ctx));
            }
        }
    }

    private AggregationBuilder parseSubAggregationClause(SubAggregationClauseContext subAggregationClauseContext){
        List<AggregationBuilder> subList = parseAggregationClause(subAggregationClauseContext.aggregationClause());
        if (subList.size()>1){
            throw new IllegalArgumentException("unsupported sub aggregation");
        }
        return subList.get(0);
    }

    private void parseNestedAggregationClause(List<AggregationBuilder> aggregationBuilderList,NestedAggregationClauseContext nestedAggregationClauseContext){
        String filed = nestedAggregationClauseContext.nestedPath.getText();
        String nestedName = "nested_" + filed;
        AggregationBuilder aggregationBuilder = AggregationBuilders.nested(nestedName, filed);
        if (nestedAggregationClauseContext.aggregationClause().size() < 1) {
            throw new IllegalArgumentException("unsupported nested aggregate");
        }
        List<AggregationBuilder> nestedAggList = parseAggregationClause(nestedAggregationClauseContext.aggregationClause(0));
        for (AggregationBuilder builder:nestedAggList){
            aggregationBuilder.subAggregation(builder);
        }
        aggregationBuilderList.add(aggregationBuilder);

        if (nestedAggregationClauseContext.aggregationClause().size()>1){
            for (int i=1;i<nestedAggregationClauseContext.aggregationClause().size();i++){
                aggregationBuilderList.addAll(parseAggregationClause(nestedAggregationClauseContext.aggregationClause(i)));
            }
        }
        if (nestedAggregationClauseContext.subAggregationClause().size()>0){
            for (SubAggregationClauseContext ctx:nestedAggregationClauseContext.subAggregationClause()){
                aggregationBuilder.subAggregation(parseSubAggregationClause(ctx));
            }
        }
    }




    private List<AggregationParser> buildParserChain() {
        return Lists.newArrayList(new MaxAggregationParser()
                , new MinAggregationParser()
                , new SumAggregationParser()
                , new TermsAggregationParser()
                , new CardinalityAggregationParser()
                , new AvgAggregationParser());
    }
}
