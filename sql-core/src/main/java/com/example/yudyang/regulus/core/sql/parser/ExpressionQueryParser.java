package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import org.antlr.v4.runtime.ParserRuleContext;

public interface ExpressionQueryParser<T extends ParserRuleContext>{
    AtomicQuery parse(T expression);

    boolean isMatchInvocation(Class clazz);
}
