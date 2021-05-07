package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class InQueryParser extends AbstractQueryParser implements ExpressionQueryParser<InContext> {

    @Override
    public AtomicQuery parse(InContext expression) {
        InClauseContext inClauseContext = expression.inClause();
        SqlOperator operator = inClauseContext.NOT() == null ? SqlOperator.In : SqlOperator.NotIn;
        List<String> rangeList = new ArrayList<>();
        for (InRightOperandContext rightCtx : inClauseContext.inRightOperandList().inRightOperand()) {
            if (rightCtx instanceof ConstLiteralContext) {
                ConstLiteralContext constLiteralContext = (ConstLiteralContext) rightCtx;
                rangeList.add(constLiteralContext.identity().getText());
            }
        }
        return parseCondition(expression, operator, rangeList.toArray(new String[0]), (fieldName1, operator1, rightVal) -> {
            if (operator1 == SqlOperator.In) {
                return QueryBuilders.termsQuery(fieldName1, Arrays.stream(rightVal).map(s -> StringManager.removeStringSymbol(s.toString())).collect(Collectors.toList()));
            } else {
                return QueryBuilders.boolQuery().mustNot(QueryBuilders.termsQuery(fieldName1, Arrays.stream(rightVal).map(s -> StringManager.removeStringSymbol(s.toString())).collect(Collectors.toList())));
            }
        });
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return InContext.class == clazz;
    }
}
