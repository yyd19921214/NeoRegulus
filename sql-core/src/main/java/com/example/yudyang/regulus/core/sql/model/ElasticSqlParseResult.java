package com.example.yudyang.regulus.core.sql.model;

import com.example.yudyang.regulus.core.sql.enumerate.SqlOperation;
import com.example.yudyang.regulus.core.sql.utils.HighlightBuilders;
import com.example.yudyang.regulus.core.sql.utils.StringManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import java.util.*;
import java.util.stream.Collectors;

public class ElasticSqlParseResult {
    private int from = 0;
    private int size = 15;
    private Map<String,String> aliasMap=new HashMap<>(0);
    private List<String> includeFields = new ArrayList<>(0);
    private List<String> excludeFields = new ArrayList<>(0);
    private Set<String> highlighter= new HashSet<>();
    private SqlOperation sqlOperation = SqlOperation.SELECT;
    private List<String> indices = new ArrayList<>(0);
    private transient List<String> orderBy = new ArrayList<>(0);
    private List<String> routingBy = new ArrayList<>(0);
    private transient boolean trackTotalHits = false;
    private transient List<AggregationBuilder> groupBy = new ArrayList<>(0);
    private transient String distinctName;
    private transient QueryBuilder whereCondition = QueryBuilders.matchAllQuery();
    private transient SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    private transient SearchRequest searchRequest;

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }

    public List<String> getIncludeFields() {
        return includeFields;
    }

    public List<String> getExcludeFields() {
        return excludeFields;
    }

    public SqlOperation getSqlOperation() {
        return sqlOperation;
    }

    public void setSqlOperation(SqlOperation sqlOperation) {
        this.sqlOperation = sqlOperation;
    }

    public boolean isTrackTotalHits() {
        return trackTotalHits;
    }

    public void setTrackTotalHits(boolean trackTotalHits) {
        this.trackTotalHits = trackTotalHits;
    }

    public List<String> getIndices() {
        return indices;
    }

    public SearchRequest getSearchRequest(){
        if (searchRequest==null){
            searchRequest = toRequest();
        }
        return searchRequest;
    }

    private SearchRequest toRequest(){
        SearchRequest searchRequest = new SearchRequest();
        List<String> idxList = indices.stream().map(s-> StringManager.removeStringSymbol(s)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idxList)){
            searchRequest.indices(idxList.toArray(new String[0]));
        }
        else{
            throw new IllegalArgumentException("indices should not be empty");
        }
        searchSourceBuilder.from(Math.max(0,from));
        searchSourceBuilder.size(Math.max(0,size));
        searchSourceBuilder.trackTotalHits(trackTotalHits);
        if (CollectionUtils.isNotEmpty(highlighter)){
            HighlightBuilder highlightBuilder = HighlightBuilders.highlighter(highlighter);
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        searchSourceBuilder.query(whereCondition);
        if (StringUtils.isNotEmpty(distinctName)){
            searchSourceBuilder.collapse(new CollapseBuilder(distinctName));
        }
        if (CollectionUtils.isNotEmpty(orderBy)){
            for (String order:orderBy){
                searchSourceBuilder.sort(SortBuilders.fieldSort(order));
            }
        }
        searchSourceBuilder.fetchSource(includeFields.toArray(new String[0]),excludeFields.toArray(new String[0]));
        if (CollectionUtils.isNotEmpty(routingBy)){
            searchRequest.routing(routingBy.toArray(new String[0]));
        }
        if (CollectionUtils.isNotEmpty(groupBy)) {
            for (AggregationBuilder group:groupBy){
                searchSourceBuilder.aggregation(group);
            }
        }
        return searchRequest.source(searchSourceBuilder);
    }
}
