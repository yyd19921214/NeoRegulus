package com.example.yudyang.regulus.core.sql.parser;

import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;

public abstract class AbstractParser
{
    public abstract ElasticSqlParseResult parse(String sql);
}
