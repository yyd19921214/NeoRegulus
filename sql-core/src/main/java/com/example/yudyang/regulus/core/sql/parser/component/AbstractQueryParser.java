package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import org.antlr.v4.runtime.ParserRuleContext;
import org.elasticsearch.index.query.QueryBuilder;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public abstract class AbstractQueryParser {
    public AtomicQuery parseCondition(ParserRuleContext expression, SqlOperator operator, Object[] params, IConditionQueryFunc conditionQueryFunc){
        if (expression instanceof NameExprContext){
            NameExprContext nameExprContext= (NameExprContext) expression;
            NameClauseContext nameContext = nameExprContext.nameClause();
            if(nameContext instanceof FieldNameContext){
                return parseHighlighterField((FieldNameContext) nameContext,operator,params,conditionQueryFunc);
            }

        }
        return null;
    }

    private AtomicQuery parseHighlighterField(FieldNameContext fieldNameContext,SqlOperator operator, Object[] params,IConditionQueryFunc queryBuilder){
        QueryBuilder query = queryBuilder.buildQuery(fieldNameContext.field.getText(), operator, params);
        AtomicQuery atomicQuery=new AtomicQuery(query);
        if(fieldNameContext.highlighter!=null) {
            atomicQuery.getHighlighter().add(fieldNameContext.field.getText());
        }
        return atomicQuery;

    }


}
