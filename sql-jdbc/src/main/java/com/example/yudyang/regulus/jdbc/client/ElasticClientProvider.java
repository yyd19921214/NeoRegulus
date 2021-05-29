package com.example.yudyang.regulus.jdbc.client;

import org.elasticsearch.client.RestHighLevelClient;

public interface ElasticClientProvider {
    RestHighLevelClient fromUrl(String url, String username, String password);
}
