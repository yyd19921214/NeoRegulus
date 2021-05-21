package com.example.yudyang.regulus.core;

import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import org.junit.Test;

public class ElasticTest {

    // test condition
    @Test
    public void parse1() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where salary>3000";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_2() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where (salary>3000 or age>18)";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_3() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where (salary>3000 or (age>18 and loc='shanghai'))";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_4() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where (salary>3000 and (age>18 or loc='shanghai'))";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_5() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where salary>3000 and age ranged in (1,18]";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_6() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where gender in ('F','M')";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_7() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where name FUZZY like 'tony'";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_8() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where name WildCard like 'to*y'";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_9() {
        long now = System.currentTimeMillis();
        String sql = "select * from student where (firstname,lastname) ~= 'toy'";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }



    @Test
    public void parse1_10() {
        long now = System.currentTimeMillis();
        String sql = "select name from student where [name,name.firstName in ('A','B') and name.age>19]";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_11() {
        long now = System.currentTimeMillis();
        String sql = "select name from student where [name,name.firstName='jack' and [tag,tag.tt='A']] and (age>18 or salary<2000)";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_12() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit where has_parent(vegetable,weight between 100 and 400)";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_13() {
        long now = System.currentTimeMillis();
        String sql = "select * from fruit where has_child(apple,price in (10,20,30))";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse1_14() {
        long now = System.currentTimeMillis();
        String sql = "select loc,avg(salary) from employee where age>18 group by loc";
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


    @Test
    public void parse15() {
        long now = System.currentTimeMillis();
        String sql = "select name from employee where age>25 order by salary desc";
        ElasticSql2DslParser parser = new ElasticSql2DslParser();
        ElasticSqlParseResult parseResult = parser.parse(sql);
        System.out.println(parseResult.toPrettyDsl(parseResult.getSearchRequest()));
        System.out.println(System.currentTimeMillis() - now);
    }

    @Test
    public void parse16() {
//        long now = System

    }

}
