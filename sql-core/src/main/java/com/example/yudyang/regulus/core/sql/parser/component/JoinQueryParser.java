package com.example.yudyang.regulus.core.sql.parser.component;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
public class JoinQueryParser implements ExpressionQueryParser<JoinContext> {

    private HasChildQueryParser hasChildQueryParser;
    private HasParentQueryParser hasParentQueryParser;

    public JoinQueryParser() {
        this.hasChildQueryParser = new HasChildQueryParser();
        this.hasParentQueryParser = new HasParentQueryParser();
    }

    @Override
    public AtomicQuery parse(JoinContext expression) {
        if (expression.hasChildClause()!=null){
            return hasChildQueryParser.parse(expression.hasChildClause());
        }
        else {
            return hasParentQueryParser.parse(expression.hasParentClause());
        }
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return JoinContext.class == clazz;
    }
}
