package com.example.yudyang.regulus.core.sql.exception;

public class ElasticSql2DslException extends RuntimeException{
    public ElasticSql2DslException(String message) {
        super(message);
    }

    public ElasticSql2DslException(Exception e){
        super(e);
    }
}
