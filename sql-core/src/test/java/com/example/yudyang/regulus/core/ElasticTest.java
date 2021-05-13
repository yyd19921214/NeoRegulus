package com.example.yudyang.regulus.core;

import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import org.junit.Test;

public class ElasticTest {
    @Test
    public void parse1() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where age>18";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse2() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where name~='jack' and age >15 or salary<2000";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse3() {
        long now = System.currentTimeMillis();
        String sql = "select name from student where name between 'A' and 'Z'";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse4() {
        long now = System.currentTimeMillis();
        String sql = "select name from student where [name,name.firstName='jack' and [tag,tag.tt='A']]";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse5() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit where has_parent(vegetable,weight between 100 and 400)";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse6() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit where has_child(apple,price in (10,20,30))";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }


    @Test
    public void parse7() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit aggregate by avg(price)";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse8() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit aggregate by terms(name,1)>(terms(aa,2))";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse9() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit aggregate by [apple,terms(type,2),terms(loc,3)]";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse10() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit aggregate by avg(price),terms(type,2)>(terms(sub_type,3)),[apple,terms(loc,3)>(terms(sub_loc,5))]";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse11() {
        long now = System.currentTimeMillis();
        String sql = "select max(price) as max_price,min(price) from fruit group by type,origin";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse12() {
        long now = System.currentTimeMillis();
        String sql = "select max(price) as max_price from fruit group by type having max(price)>100";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse13() {
        long now = System.currentTimeMillis();
        String sql = "select max(price) as max_price from fruit group by type having max(price)>100 and max(price)<200";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse14() {
        long now = System.currentTimeMillis();
        String sql = "select sum(price) from fruit group by type having max(price)>100";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }


}
