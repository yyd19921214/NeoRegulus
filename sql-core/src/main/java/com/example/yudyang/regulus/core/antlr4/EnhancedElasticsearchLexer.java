package com.example.yudyang.regulus.core.antlr4;

import org.antlr.v4.runtime.CharStream;

public class EnhancedElasticsearchLexer extends  ElasticsearchLexer{
    public EnhancedElasticsearchLexer(CharStream input) {
        super(input);
    }

    @Override
    protected boolean isType(String name) {
        return false;
    }

    @Override
    protected boolean slashIsRegex() {
        return false;
    }
}
