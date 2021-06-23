package com.example.yudyang.regulus.jdbc.elastic;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcResultExtractor {

    public JdbcSearchResponse parseSearchResponse(SearchResponse response, Map<String, String> aliasMap) {
        JdbcSearchResponse jdbcSearchResponse = new JdbcSearchResponse();
        jdbcSearchResponse.setSize(response.getHits().getHits().length);
        jdbcSearchResponse.setAliasMap(aliasMap);
        jdbcSearchResponse.setTook(response.getTook().getMillis());
        if (response.getHits().getTotalHits() != null) {
            jdbcSearchResponse.setTotal(response.getHits().getTotalHits().value);
        }
        SearchHit[] searchHits = response.getHits().getHits();
        if (searchHits != null && searchHits.length > 0) {
            List<Map<String, Object>> result = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                hit.getSourceAsMap().put("_id", hit.getId());
                if (hit.field("_routing") != null) {
                    hit.getSourceAsMap().put("_routing", hit.field("_routing").getValue());
                }
                result.add(new HashMap<>(hit.getSourceAsMap()));
            }
            jdbcSearchResponse.setResult(result);
            jdbcSearchResponse.setAliasMap(aliasMap);
        }
        return jdbcSearchResponse;
    }

    public JdbcSearchResponse parseScrollSearchResponse(SearchResponse response,Map<String,String> aliasMap) {
        JdbcSearchResponse jdbcSearchResponse = parseSearchResponse(response,aliasMap);
        String scrollId = response.getScrollId();
        return new JdbcScrollSearchResponse(jdbcSearchResponse,scrollId);
    }
}
