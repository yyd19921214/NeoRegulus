package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;


public class BetweenAndQueryParser extends AbstractQueryParser implements ExpressionQueryParser<BetweenAndContext> {

    @Override
    public AtomicQuery parse(BetweenAndContext expression) {
        if (expression.rangeClause()!=null){
            RangeClauseContext rangeClauseContext = expression.rangeClause();
            String leftStr = StringManager.removeStringSymbol(rangeClauseContext.left.getText());
            String rightStr = StringManager.removeStringSymbol(rangeClauseContext.right.getText());
            RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(rangeClauseContext.field.getText());
            if (rangeClauseContext.LBRACKET()!=null){
                queryBuilder.gte(leftStr);
            }
            else{
                queryBuilder.gt(leftStr);
            }
            if (rangeClauseContext.RBRACKET()!=null){
                queryBuilder.lte(rightStr);
            }
            else{
                queryBuilder.lt(rightStr);
            }
            return new AtomicQuery(queryBuilder);
        }
        else{
            return parseCondition(expression, SqlOperator.BetweenAnd, new Object[]{expression.left.getText(), expression.right.getText()}, (fieldName, operator, rightVal) -> QueryBuilders.rangeQuery(fieldName).gt(StringManager.removeStringSymbol(rightVal[0].toString())).lt(StringManager.removeStringSymbol(rightVal[1].toString())));
        }
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return BetweenAndContext.class == clazz;
    }
}
