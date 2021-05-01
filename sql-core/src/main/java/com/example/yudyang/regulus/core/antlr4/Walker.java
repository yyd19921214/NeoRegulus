package com.example.yudyang.regulus.core.antlr4;

import com.example.yudyang.regulus.core.sql.exception.ElasticSql2DslException;
import com.example.yudyang.regulus.core.sql.model.DefaultElasticSqlResult;
import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang3.StringUtils;


public class Walker {

    private final String sql;

    private ElasticSqlParseResult elasticSqlParseResult;

    public Walker(String sql){
        this.sql=sql;
        this.elasticSqlParseResult = new DefaultElasticSqlResult();
    }

    public ElasticsearchParser.SqlContext buildAntlrTree(){
        if (StringUtils.isEmpty(sql))
        {
            throw new ElasticSql2DslException("blank ssql is not allowed");
        }
        CharStream stream = CharStreams.fromString(this.sql);
        ElasticsearchLexer lexer = new EnhancedElasticsearchLexer(stream);
        ElasticsearchParser parser = new ElasticsearchParser(new CommonTokenStream(lexer));
        ParserErrorStrategy strategy = new ParserErrorStrategy(this.sql);
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        parser.setErrorHandler(strategy);
        return parser.sql();
    }
}
