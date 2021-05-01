package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.sql.enumerate.SqlBoolOperator;
import com.example.yudyang.regulus.core.sql.enumerate.SqlConditionType;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.model.SqlCondition;
import com.example.yudyang.regulus.core.sql.parser.component.BinaryQueryParser;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;


import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BooleanExpParser {

    public Set<String> highlighter;
    private final BinaryQueryParser binaryQueryParser;

    public BooleanExpParser(Set<String> highlighter, BinaryQueryParser binaryQueryParser) {
        this.highlighter = highlighter;
        this.binaryQueryParser = binaryQueryParser;
    }

    public QueryBuilder parseBoolQueryExpr(ExpressionContext expressionContext) {
        SqlCondition sqlCondition = recursiveParseBoolQueryExpr(expressionContext);
        SqlBoolOperator sqlBoolOperator = sqlCondition.getSqlBoolOperator();
        if (SqlConditionType.Atomic == sqlCondition.getSqlConditionType()) {
            sqlBoolOperator = SqlBoolOperator.AND;
        }
        if (sqlCondition.getQueryList().size() > 1) {
            return mergeAtomicQuery(sqlCondition.getQueryList(), sqlBoolOperator);
        } else {
            this.highlighter.addAll(sqlCondition.getQueryList().get(0).getHighlighter());
            return sqlCondition.getQueryList().get(0).getQueryBuilder();
        }
    }

    private SqlCondition recursiveParseBoolQueryExpr(ExpressionContext expressionContext) {
        if (expressionContext instanceof BinaryContext) {
            BinaryContext binaryContext = (BinaryContext) expressionContext;
            int tokenType = binaryContext.operator.getType();
            SqlBoolOperator sqlBoolOperator;
            if (tokenType == AND || tokenType == BOOLAND) {
                sqlBoolOperator = SqlBoolOperator.AND;
            } else if (tokenType == OR || tokenType == BOOLOR) {
                sqlBoolOperator = SqlBoolOperator.OR;
            } else {
                // case: name='jack' salary>1000;
                AtomicQuery atomicQuery = binaryQueryParser.parseExpression(binaryContext);
                this.highlighter.addAll(atomicQuery.getHighlighter());
                return new SqlCondition(atomicQuery, SqlConditionType.Atomic);
            }
            SqlCondition leftSqlCondition = recursiveParseBoolQueryExpr(binaryContext.leftExpr);
            SqlCondition rightSqlCondition = recursiveParseBoolQueryExpr(binaryContext.rightExpr);
            List<AtomicQuery> combine = new ArrayList<>();
            combineQuery(combine, leftSqlCondition, sqlBoolOperator);
            combineQuery(combine, rightSqlCondition, sqlBoolOperator);
            return new SqlCondition(sqlBoolOperator, combine);
        } else if (expressionContext instanceof NestedContext) {
            //todo handle with nested query
        }
        // handle some case like inClause
        AtomicQuery atomicQuery = binaryQueryParser.parseExpression(expressionContext);
        this.highlighter.addAll(atomicQuery.getHighlighter());
        return new SqlCondition(atomicQuery, SqlConditionType.Atomic);
    }

    private BoolQueryBuilder mergeAtomicQuery(List<AtomicQuery> queryList, SqlBoolOperator sqlBoolOperator) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (AtomicQuery atomicQuery : queryList) {
            if (CollectionUtils.isNotEmpty(atomicQuery.getHighlighter())) {
                this.highlighter.addAll(atomicQuery.getHighlighter());
            }
            if (sqlBoolOperator == SqlBoolOperator.AND) {
                if (atomicQuery.getQueryBuilder() instanceof BoolQueryBuilder) {
                    BoolQueryBuilder builder = (BoolQueryBuilder) atomicQuery.getQueryBuilder();
                    if (CollectionUtils.isNotEmpty(builder.must())) {
                        boolQueryBuilder.must().addAll(builder.must());
                    }
                    if (CollectionUtils.isNotEmpty(builder.mustNot())) {
                        boolQueryBuilder.mustNot().addAll(builder.mustNot());
                    }
                    if (CollectionUtils.isNotEmpty(builder.should())) {
                        boolQueryBuilder.should().addAll(builder.should());
                    }
                } else {
                    //作为整体嵌入
                    boolQueryBuilder.must().add(atomicQuery.getQueryBuilder());
                }
            } else if (sqlBoolOperator == SqlBoolOperator.OR) {
                boolQueryBuilder.should().add(atomicQuery.getQueryBuilder());
            }
        }
        if (boolQueryBuilder.should().size() > 1) {
            boolQueryBuilder.minimumShouldMatch(1);
        }
        return boolQueryBuilder;
    }

    // 使combiner中的query满足sqlBoolOperator表示的交换律与结合律
    private void combineQuery(List<AtomicQuery> combiner, SqlCondition sqlCondition, SqlBoolOperator sqlBoolOperator) {
        if (sqlCondition.getSqlConditionType() == SqlConditionType.Atomic || sqlCondition.getSqlBoolOperator() == sqlBoolOperator) {
            for (AtomicQuery query : sqlCondition.getQueryList()) {
                combiner.add(query);
                this.highlighter.addAll(query.getHighlighter());
            }
        } else {
            QueryBuilder mergedQuery = mergeAtomicQuery(sqlCondition.getQueryList(), sqlCondition.getSqlBoolOperator());
            combiner.add(new AtomicQuery(mergedQuery));
        }

    }


}
