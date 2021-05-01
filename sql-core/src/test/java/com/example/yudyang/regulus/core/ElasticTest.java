package com.example.yudyang.regulus.core;

import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import org.junit.Test;

public class ElasticTest
{
    @Test
    public void parse2(){
        long now=System.currentTimeMillis();
        String sql="select name,age from student where age>18";
        ElasticSql2DslParser parser=new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis()-now);
    }
    
}
