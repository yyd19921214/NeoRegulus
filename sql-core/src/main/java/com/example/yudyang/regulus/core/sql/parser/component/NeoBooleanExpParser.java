package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashSet;
import java.util.Set;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class NeoBooleanExpParser {

    private Set<String> highlighters;

    private BinaryQueryParser binaryQueryParser;

    public NeoBooleanExpParser() {
        this.highlighters = new HashSet<>();
        binaryQueryParser = new BinaryQueryParser();
    }

    public QueryBuilder parseExpression(ExpressionContext expressionContext) {
        return parseExpressionRecursively(expressionContext);
    }

    private QueryBuilder parseExpressionRecursively(ExpressionContext expressionContext) {
        if (expressionContext instanceof BinaryContext) {
            BinaryContext binaryContext = (BinaryContext) expressionContext;
            return parseBinaryContext(binaryContext);
        } else {
            AtomicQuery atomicQuery = binaryQueryParser.parseExpression(expressionContext);
            this.highlighters.addAll(atomicQuery.getHighlighter());
            return atomicQuery.getQueryBuilder();
        }

    }

    private QueryBuilder parseBinaryContext(BinaryContext binaryContext) {
        if (binaryContext.operator!=null && (binaryContext.operator.getType() == AND || binaryContext.operator.getType() == OR)) {
            QueryBuilder leftQueryBuilder = parseExpressionRecursively(binaryContext.leftExpr);
            QueryBuilder rightQueryBuilder = parseExpressionRecursively(binaryContext.rightExpr);
            if (binaryContext.operator.getType() == AND) {
                return QueryBuilders.boolQuery().must(leftQueryBuilder).must(rightQueryBuilder);
            } else {
                return QueryBuilders.boolQuery().should(leftQueryBuilder).should(rightQueryBuilder).minimumShouldMatch(1);
            }
        } else {
            AtomicQuery query = binaryQueryParser.parseBinaryQuery(binaryContext);
            this.highlighters.addAll(query.getHighlighter());
            return query.getQueryBuilder();
        }
    }

    public Set<String> getHighlighters() {
        return highlighters;
    }
}
