package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.component.like.DelegateLikeQueryParser;
import com.example.yudyang.regulus.core.sql.parser.component.text.FullTextQueryParser;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.antlr.v4.runtime.ParserRuleContext;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashMap;
import java.util.Map;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
import static com.example.yudyang.regulus.core.sql.enumerate.SqlOperator.*;

public class BinaryQueryParser extends AbstractQueryParser {
    @Override
    public AtomicQuery parseCondition(ParserRuleContext expression, SqlOperator operator, Object[] params, IConditionQueryFunc conditionQueryFunc) {
        return super.parseCondition(expression, operator, params, conditionQueryFunc);
    }

    private Map<SqlOperator, IConditionQueryFunc> queryBuildFuncMap;
    private ExistQueryParser existQueryParser;
    private DelegateLikeQueryParser delegateLikeQueryParser;
    private BetweenAndQueryParser betweenAndQueryParser;
    private InQueryParser inQueryParser;
    private FullTextQueryParser fullTextQueryParser;
    private NestedQueryParser nestedQueryParser;
    private JoinQueryParser joinQueryParser;

    private NeoBooleanExpParser neoBooleanExpParser;



    public BinaryQueryParser() {
        this.queryBuildFuncMap = buildQueryFuncMap();
        existQueryParser = new ExistQueryParser();
        delegateLikeQueryParser = new DelegateLikeQueryParser();
        betweenAndQueryParser = new BetweenAndQueryParser();
        inQueryParser = new InQueryParser();
        fullTextQueryParser = new FullTextQueryParser();
        nestedQueryParser = new NestedQueryParser();
        joinQueryParser = new JoinQueryParser();
    }

    public AtomicQuery parseExpression(ExpressionContext expressionContext) {
        if (expressionContext instanceof BinaryContext) {
            BinaryContext binaryContext = (BinaryContext) expressionContext;
            return parseBinaryQuery(binaryContext);
        } else if (expressionContext instanceof LrExprContext) {
            LrExprContext lrExprContext = (LrExprContext) expressionContext;
            NeoBooleanExpParser neoBooleanExpParser = new NeoBooleanExpParser();
            AtomicQuery atomicQuery = new AtomicQuery( neoBooleanExpParser.parseExpression(lrExprContext.expression()));
            atomicQuery.getHighlighter().addAll(neoBooleanExpParser.getHighlighters());
            return atomicQuery;
        } else if (expressionContext instanceof BetweenAndContext) {
            BetweenAndContext betweenAndContext = (BetweenAndContext) expressionContext;
            return betweenAndQueryParser.parse(betweenAndContext);
        } else if (expressionContext instanceof InContext) {
            InContext inContext = (InContext) expressionContext;
            return inQueryParser.parse(inContext);
        } else if (expressionContext instanceof NameExprContext) {
            // todo handle with this scenario
        } else if (expressionContext instanceof FullTextContext) {
            FullTextContext fullTextContext = (FullTextContext) expressionContext;
            return fullTextQueryParser.parse(fullTextContext);
        } else if (expressionContext instanceof NestedContext) {
            NestedContext nestedContext = (NestedContext) expressionContext;
            return nestedQueryParser.parse(nestedContext);
        } else if (expressionContext instanceof JoinContext) {
            JoinContext joinContext = (JoinContext) expressionContext;
            return joinQueryParser.parse(joinContext);
        }
        throw new IllegalArgumentException("unsupported expression type");
    }

    public AtomicQuery parseBinaryQuery(BinaryContext binaryContext) {
        if (binaryContext.operator != null) {
            int operatorType = binaryContext.operator.getType();
            if (operatorType == EQ || operatorType == NE || operatorType == AEQ || operatorType == TEQ || operatorType == NAEQ
                    || operatorType == NTEQ || operatorType == MPPEQ || operatorType == NMPPEQ) {
                SqlOperator operator;
                switch (operatorType) {
                    case NE: {
                        operator = NotEqual;
                        break;
                    }
                    case AEQ: {
                        operator = ApproximatelyEqual;
                        break;
                    }
                    case NAEQ: {
                        operator = ApproximatelyNotEqual;
                        break;
                    }
                    case TEQ: {
                        operator = MatchPhrase;
                        break;
                    }
                    case NTEQ: {
                        operator = NotMatchPhrase;
                        break;
                    }
                    case MPPEQ: {
                        operator = MatchPhrasePrefix;
                        break;
                    }
                    case NMPPEQ: {
                        operator = NotMatchPhrasePrefix;
                        break;
                    }
                    default:
                    case EQ: {
                        operator = Equality;
                        break;
                    }
                }
                return parseCondition(binaryContext.leftExpr, operator, new Object[]{binaryContext.rightExpr.getText()}, queryBuildFuncMap.get(operator));
            }
            if (operatorType == LT || operatorType == GT || operatorType == LTE || operatorType == GTE) {
                SqlOperator operator;
                switch (operatorType) {
                    case LT:
                        operator = LessThan;
                        break;
                    case GT:
                        operator = GreaterThan;
                        break;
                    case LTE:
                        operator = LessThanOrEqual;
                        break;
                    default:
                    case GTE:
                        operator = GreaterThanOrEqual;
                        break;
                }
                return parseCondition(binaryContext.leftExpr, operator, new Object[]{binaryContext.rightExpr.getText()}, queryBuildFuncMap.get(operator));
            }
            throw new IllegalArgumentException("unsupported operation");
        } else if (binaryContext.isClause() != null) {
            IsClauseContext isClauseContext = binaryContext.isClause();
            return existQueryParser.parse(isClauseContext);
        } else if (binaryContext.likeClause() != null) {
            LikeClauseContext likeClauseContext = binaryContext.likeClause();
            int idx = likeClauseContext.funName.getType();
            return delegateLikeQueryParser.parse(likeClauseContext,idx);
        } else if (binaryContext.notClause() != null) {
            AtomicQuery query = parseExpression(binaryContext.notClause().expression());
            return new AtomicQuery(QueryBuilders.boolQuery().mustNot(query.getQueryBuilder()));
        }
        return null;
    }


    private Map<SqlOperator, IConditionQueryFunc> buildQueryFuncMap() {
        Map<SqlOperator, IConditionQueryFunc> map = new HashMap<>();
        map.put(Equality, (fieldName, operator1, rightParams) -> QueryBuilders.termQuery(fieldName, StringManager.removeStringSymbol(rightParams[0].toString())));
        map.put(NotEqual, (fieldName, operator1, rightParams) -> QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery(fieldName, StringManager.removeStringSymbol(rightParams[0].toString()))));
        map.put(ApproximatelyEqual, (fieldName, operator1, rightParams) -> QueryBuilders.matchQuery(fieldName, StringManager.removeStringSymbol(rightParams[0].toString())));
        // ?????????????????????????????????must_not????????????match??????
        map.put(ApproximatelyNotEqual, (fieldName, operator1, rightParams) -> QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery(fieldName, StringManager.removeStringSymbol(rightParams[0].toString()))));
        map.put(MatchPhrase, (fieldName, operator1, rightParams) -> QueryBuilders.matchPhraseQuery(fieldName, StringManager.removeStringSymbol(rightParams[0].toString())));
        map.put(NotMatchPhrase, (fieldName, operator1, rightParams) -> QueryBuilders.boolQuery().mustNot(QueryBuilders.matchPhraseQuery(fieldName, StringManager.removeStringSymbol(rightParams[0].toString()))));
        map.put(MatchPhrasePrefix, (fieldName, operator, rightVal) -> QueryBuilders.matchPhrasePrefixQuery(fieldName, StringManager.removeStringSymbol(rightVal[0].toString())));
        map.put(NotMatchPhrasePrefix, (fieldName, operator, rightVal) -> QueryBuilders.boolQuery().mustNot(QueryBuilders.matchPhrasePrefixQuery(fieldName, StringManager.removeStringSymbol(rightVal[0].toString()))));

        map.put(GreaterThan, (fieldName, operator, rightVal) -> QueryBuilders.rangeQuery(fieldName).gt(StringManager.removeStringSymbol(rightVal[0].toString())));
        map.put(GreaterThanOrEqual, (fieldName, operator, rightVal) -> QueryBuilders.rangeQuery(fieldName).gte(StringManager.removeStringSymbol(rightVal[0].toString())));
        map.put(LessThan, (fieldName, operator, rightVal) -> QueryBuilders.rangeQuery(fieldName).lt(StringManager.removeStringSymbol(rightVal[0].toString())));
        map.put(LessThanOrEqual, (fieldName, operator, rightVal) -> QueryBuilders.rangeQuery(fieldName).lte(StringManager.removeStringSymbol(rightVal[0].toString())));
        return map;
    }

}
