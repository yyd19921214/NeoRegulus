package com.example.yudyang.regulus.core.sql.parser.component.like;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;

public interface LikeQueryParser extends ExpressionQueryParser<ElasticsearchParser.LikeClauseContext> {
    default boolean isMatchInvocation(Class clazz) {
        return clazz == ElasticsearchParser.LikeClauseContext.class;
    }

}
