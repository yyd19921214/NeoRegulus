package com.example.yudyang.regulus.core.sql.parser.component.text;

import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import com.example.yudyang.regulus.core.sql.parser.component.AbstractQueryParser;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class FullTextQueryParser extends AbstractQueryParser implements ExpressionQueryParser<FullTextContext> {

    QueryStringParser queryStringParser;
    MultiStringQueryParser multiStringQueryParser;


    public FullTextQueryParser() {
        this.queryStringParser = new QueryStringParser();
        this.multiStringQueryParser = new MultiStringQueryParser();
    }

    @Override
    public AtomicQuery parse(FullTextContext expression) {
        if (expression.fullTextClause().queryStringClause()!=null){
            QueryStringClauseContext queryStringClauseContext = expression.fullTextClause().queryStringClause();
            return this.queryStringParser.parse(queryStringClauseContext);
        }
        else if (expression.fullTextClause().multiMatchClause()!=null){
            MultiMatchClauseContext multiMatchClauseContext = expression.fullTextClause().multiMatchClause();
            return this.multiStringQueryParser.parse(multiMatchClauseContext);
        }
        return null;
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return false;
    }
}
