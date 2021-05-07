package com.example.yudyang.regulus.core;

import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import org.junit.Test;

public class ElasticTest
{
    @Test
    public void parse1(){
        long now=System.currentTimeMillis();
        String sql="select * from student where age>18";
        ElasticSql2DslParser parser=new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis()-now);
    }

    @Test
    public void parse2(){
        long now=System.currentTimeMillis();
        String sql="select * from student where name~='jack' and age >15 or salary<2000";
        ElasticSql2DslParser parser=new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis()-now);
    }

    @Test
    public void parse3(){
        long now=System.currentTimeMillis();
        String sql="select name from student where name between 'A' and 'Z'";
        ElasticSql2DslParser parser=new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis()-now);
    }
    
}
