package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.sql.parser.groupBy.*;
import com.google.common.collect.Lists;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class QueryGroupByParser implements QueryParser {

    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation() != null && elasticDslContext.getSqlContext().selectOperation().groupByClause() != null) {
            parseGroupBy(elasticDslContext);
        }
    }

    private void parseGroupBy(ElasticDslContext elasticDslContext) {
        GroupByClauseContext groupByClauseContext = elasticDslContext.getSqlContext().selectOperation().groupByClause();
        FieldListContext fieldListContext = elasticDslContext.getSqlContext().selectOperation().fieldList();
        List<AggregationBuilder> terminalAggregateNodeList = new ArrayList<>();
        if (fieldListContext.nameOperand().size() > 0) {
            terminalAggregateNodeList = buildTerminalAggregationNode(fieldListContext.nameOperand());
        }
        AggregationBuilder aggregationBuilder = buildAggregateBuilder(groupByClauseContext, terminalAggregateNodeList);
        elasticDslContext.getElasticSqlParseResult().getGroupBy().add(aggregationBuilder);
    }

    private AggregationBuilder buildAggregateBuilder(GroupByClauseContext groupByClauseContext, List<AggregationBuilder> terminalAggregateNodeList) {
        List<String> groupByFieldList = groupByClauseContext.ID().stream().map(node -> node.getText()).collect(Collectors.toList());
        TermGroupByParser termGroupByParser = new TermGroupByParser();
        List<AggregationBuilder> groupByAggregateBuilds = groupByFieldList.stream().map(field -> termGroupByParser.parse(field)).collect(Collectors.toList());
        AggregationBuilder lastBuilder = groupByAggregateBuilds.get(groupByAggregateBuilds.size() - 1);
        for (AggregationBuilder terminalBuilder : terminalAggregateNodeList) {
            lastBuilder.subAggregation(terminalBuilder);
        }
        AggregationBuilder root = groupByAggregateBuilds.get(0);
        for (int i = 1; i < groupByAggregateBuilds.size(); i++) {
            root = root.subAggregation(groupByAggregateBuilds.get(i));
        }
        return root;
        // todo consider the scenario of having
    }

    private List<AggregationBuilder> buildTerminalAggregationNode(List<NameOperandContext> nameOperandContexts) {
        List<AggregationBuilder> terminalAggNodeList = Lists.newArrayList();
        List<GroupByParser> groupByParsers = buildParseChain();
        for (NameOperandContext ctx : nameOperandContexts) {
            if (ctx.fieldName instanceof FunctionNameContext) {
                FunctionNameContext functionNameContext = (FunctionNameContext) ctx.fieldName;
                for (GroupByParser groupByParser : groupByParsers) {
                    AggregationBuilder builder = groupByParser.parse(functionNameContext);
                    if (builder != null) {
                        terminalAggNodeList.add(builder);
                        break;
                    }
                }
            }
        }
        return terminalAggNodeList;
    }

    private List<GroupByParser> buildParseChain() {
        return Lists.newArrayList(new MinGroupByParser(), new MaxGroupByParser(), new SumGroupByParser());
    }


}
