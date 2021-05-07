package com.example.yudyang.regulus.core.sql.parser.component.text;

import com.example.yudyang.regulus.core.antlr4.ElasticsearchParser;
import com.example.yudyang.regulus.core.sql.model.AtomicQuery;
import com.example.yudyang.regulus.core.sql.parser.ExpressionQueryParser;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;
public class MultiStringQueryParser implements ExpressionQueryParser<MultiMatchClauseContext> {

    @Override
    public AtomicQuery parse(MultiMatchClauseContext expression) {
        List<String> nameList = new ArrayList<>();
        Set<String> highLight = new HashSet<>();
        List<NameClauseContext> nameClauseContextList = expression.nameClause();
        for (NameClauseContext nameClauseContext:nameClauseContextList){
            if (nameClauseContext instanceof FieldNameContext){
                FieldNameContext fieldNameContext = (FieldNameContext) nameClauseContext;
                if (fieldNameContext.highlighter!=null){
                    highLight.add(fieldNameContext.field.getText());
                }
                nameList.add(fieldNameContext.field.getText());
            }
        }
        AtomicQuery atomicQuery = new AtomicQuery(QueryBuilders.multiMatchQuery(expression.value.getText(),nameList.toArray(new String[0])));
        atomicQuery.getHighlighter().addAll(highLight);
        return atomicQuery;
    }

    @Override
    public boolean isMatchInvocation(Class clazz) {
        return MultiMatchClauseContext.class == clazz;
    }
}
