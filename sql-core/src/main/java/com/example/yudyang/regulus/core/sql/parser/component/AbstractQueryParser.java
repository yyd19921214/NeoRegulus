package com.example.yudyang.regulus.core.sql.parser.component;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperator;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class AbstractQueryParser {
    public AtomicQuery parseCondition(ParserRuleContext expression, SqlOperator operator, Object[] params, IConditionQueryFunc conditionQueryFunc){
        return null;
    }
}
