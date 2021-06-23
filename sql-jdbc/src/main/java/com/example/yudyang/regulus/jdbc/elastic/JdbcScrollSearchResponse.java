package com.example.yudyang.regulus.jdbc.elastic;

public class JdbcScrollSearchResponse extends JdbcSearchResponse{
    private String scrollId;

    JdbcScrollSearchResponse(JdbcSearchResponse response, String scrollId) {
        this.scrollId = scrollId;
        setResult(response.getResult());
        setSize(response.getSize());
        setAliasMap(response.getAliasMap());
        setTook(response.getTook());
        setTotal(response.getTotal());
    }

    public String getScrollId() {
        return scrollId;
    }

}
