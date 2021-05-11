package com.example.yudyang.regulus.core.sql.parser.aggregate;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AggregateQuery;
import com.example.yudyang.regulus.core.sql.parser.ElasticDslContext;
import com.example.yudyang.regulus.core.sql.parser.QueryParser;
import com.google.common.collect.Lists;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class AggregateQueryParser implements QueryParser {

    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation() != null && elasticDslContext.getSqlContext().selectOperation().aggregateByClause() != null) {
            ElasticsearchParser.AggregateByClauseContext aggregateByClauseContext = elasticDslContext.getSqlContext().selectOperation().aggregateByClause();
            elasticDslContext.getElasticSqlParseResult().getGroupBy().addAll(parseAggregationClauseContext(aggregateByClauseContext.aggregationClause()));
        }
    }


    private List<AggregationBuilder> parseAggregationClauseContext(AggregationClauseContext aggregationClauseContext) {
        List<AggregationBuilder> aggregationBuilderList = new ArrayList<>();
        if (aggregationClauseContext.aggregateItemClause() != null) {
            aggregationBuilderList = parseAggregationItemClause(aggregationClauseContext.aggregateItemClause());
            return aggregationBuilderList;
        } else if (aggregationClauseContext.nestedAggregationClause() != null) {
            combineNestedLastAggregation(aggregationBuilderList, aggregationClauseContext.nestedAggregationClause());
            return aggregationBuilderList;
        }
        throw new IllegalArgumentException("not support aggregate");
    }

    private void combineNestedLastAggregation(List<AggregationBuilder> aggregationBuilders, ElasticsearchParser.NestedAggregationClauseContext nestedAggregationClauseContext) {
        aggregationBuilders.add(parseNestedAggregationClause(nestedAggregationClauseContext));
        for (int i = 1; i < nestedAggregationClauseContext.aggregationClause().size(); i++) {
            aggregationBuilders.addAll(parseAggregationClauseContext(nestedAggregationClauseContext.aggregationClause(i)));
        }
    }

    private AggregationBuilder parseNestedAggregationClause(NestedAggregationClauseContext nestedAggregationClauseContext){
        String filed = nestedAggregationClauseContext.nestedPath.getText();
        String nestedField = "nested_" + filed;
        AggregationBuilder aggregationBuilder = AggregationBuilders.nested(nestedField,filed);
        if (nestedAggregationClauseContext.aggregationClause(0).aggregateItemClause()!=null){
            List<AggregationBuilder> list = parseAggregationItemClause(nestedAggregationClauseContext.aggregationClause(0).aggregateItemClause());
            for (AggregationBuilder builder:list){
                aggregationBuilder.subAggregation(builder);
            }
        } else if (nestedAggregationClauseContext.aggregationClause(0).nestedAggregationClause()!=null){
            AggregationBuilder subNestedBuilder = parseNestedAggregationClause(nestedAggregationClauseContext.aggregationClause(0).nestedAggregationClause());
            aggregationBuilder.subAggregation(subNestedBuilder);
        }
        if (nestedAggregationClauseContext.subAggregationClause().size()>0){
            parseSubAggregationClauseContext(aggregationBuilder,nestedAggregationClauseContext.subAggregationClause());
        }
        return aggregationBuilder;
    }

    private List<AggregationBuilder> parseAggregationItemClause(ElasticsearchParser.AggregateItemClauseContext aggregateItemClauseContext) {
        List<AggregationBuilder> aggregationBuilders = new ArrayList<>();
        AggregateQuery aggregateQuery = new AggregateQuery();
        for (AggregationParser aggregationParser : buildAggregationChain()) {
            aggregationParser.parseAggregateItemClauseContext(aggregateQuery, aggregateItemClauseContext);
        }
        if (aggregateItemClauseContext.subAggregationClause().size() > 0) {
            parseSubAggregationClauseContext(aggregateQuery.getAggregationBuilder(), aggregateItemClauseContext.subAggregationClause());
        }
        aggregationBuilders.add(aggregateQuery.getAggregationBuilder());
        if (aggregateItemClauseContext.aggregationClause().size() > 0) {
            for (AggregationClauseContext itemClauseContext : aggregateItemClauseContext.aggregationClause()) {
                if (itemClauseContext.aggregateItemClause() != null) {
                    aggregationBuilders.addAll(parseAggregationItemClause(itemClauseContext.aggregateItemClause()));
                } else if (itemClauseContext.nestedAggregationClause() != null) {
                    aggregationBuilders.add(parseNestedAggregationClause(itemClauseContext.nestedAggregationClause()));
                }
            }
        }
        return aggregationBuilders;
    }

    private void parseSubAggregationClauseContext(AggregationBuilder aggregationBuilder, List<SubAggregationClauseContext> subAggregationClauseContextList) {
        for (SubAggregationClauseContext subAggregationClauseContext:subAggregationClauseContextList){
            if (subAggregationClauseContext.aggregationClause().aggregateItemClause()!=null){
                List<AggregationBuilder> aggregationBuilderList = parseAggregationItemClause(subAggregationClauseContext.aggregationClause().aggregateItemClause());
                for (AggregationBuilder builder:aggregationBuilderList){
                    aggregationBuilder.subAggregation(builder);
                }
            }
            else if (subAggregationClauseContext.aggregationClause().nestedAggregationClause()!=null){
                AggregationBuilder nestedBuilder = parseNestedAggregationClause(subAggregationClauseContext.aggregationClause().nestedAggregationClause());
                aggregationBuilder.subAggregation(nestedBuilder);
            }
        }
        return;
    }

    private List<AggregationParser> buildAggregationChain() {
        return Lists.newArrayList(new MaxAggregationParser()
                , new MinAggregationParser()
                , new SumAggregationParser()
                , new TermsAggregationParser()
                , new CardinalityAggregationParser()
                , new AvgAggregationParser());
    }


}
