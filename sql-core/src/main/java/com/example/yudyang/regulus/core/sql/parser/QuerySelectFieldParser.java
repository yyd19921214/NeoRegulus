package com.example.yudyang.regulus.core.sql.parser;


import java.util.ArrayList;
import java.util.List;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class QuerySelectFieldParser extends BasicParser implements QueryParser {
    @Override
    public void parse(ElasticDslContext elasticDslContext) {
        if (elasticDslContext.getSqlContext().selectOperation()!=null){
            SelectOperationContext selectOperationContext = elasticDslContext.getSqlContext().selectOperation();
            if (selectOperationContext.groupByClause()==null){
                // todo process highlight
                processWithNonGroupBy(elasticDslContext);
            }
            else{
                //todo process with group by
            }
        }
    }

    private void processWithNonGroupBy(ElasticDslContext elasticDslContext){
        FieldListContext fieldListContext = elasticDslContext.getSqlContext().selectOperation().fieldList();
        List<String> includeList = new ArrayList<>();
        List<String> excludeList = new ArrayList<>();

        if (!fieldListContext.isEmpty()){
            for (NameOperandContext nameOperandContext:fieldListContext.nameOperand()){
                if (nameOperandContext.exclude!=null){
                    excludeList.add(nameOperandContext.fieldName.getText());
                }
                else{
                    includeList.add(nameOperandContext.fieldName.getText());
                    if (nameOperandContext.alias!=null){
                        elasticDslContext.getElasticSqlParseResult().getAliasMap().put(nameOperandContext.alias.getText(),nameOperandContext.fieldName.getText());
                    }
                }
            }
        }
        if (!includeList.isEmpty()){
            elasticDslContext.getElasticSqlParseResult().getIncludeFields().addAll(includeList);
        }
        if (!excludeList.isEmpty()){
            elasticDslContext.getElasticSqlParseResult().getExcludeFields().addAll(excludeList);
        }

    }
}
