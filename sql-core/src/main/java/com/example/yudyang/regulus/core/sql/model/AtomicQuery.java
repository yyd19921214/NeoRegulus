package com.example.yudyang.regulus.core.sql.model;

import org.elasticsearch.index.query.QueryBuilder;

import java.util.HashSet;
import java.util.Set;

public class AtomicQuery {
    private Set<String> highlighter = new HashSet<>(0);
    private QueryBuilder queryBuilder;

    public Set<String> getHighlighter() {
        return highlighter;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public AtomicQuery(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
}
