package com.example.yudyang.regulus.jdbc.client;

import com.example.yudyang.regulus.jdbc.constant.JdbcConstants;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.NodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;

import static com.example.yudyang.regulus.jdbc.constant.JdbcConstants.*;
import static com.example.yudyang.regulus.jdbc.ssl.SslContextManager.TRUST_ALL_CERTS;

public class ElasticClientManager implements ElasticClientProvider{

    private final ConcurrentMap<String,RestHighLevelClient> clientMap = new ConcurrentHashMap<>();

    @Override
    public RestHighLevelClient fromUrl(String url, String username, String password) {
        if (clientMap.containsKey(url)){
            return clientMap.get(url);
        }
        Map<String,Object> params = parseUrlParam(url);
        boolean useSsl = Boolean.parseBoolean(params.getOrDefault("useSSL", false).toString());
        String mode = params.getOrDefault("mode", "single").toString();
        Matcher matcher = IP_PORT_PATTERN.matcher(url);
        List<HttpHost> httpHosts = new ArrayList<>(0);
        while (matcher.find()) {
            String ip = matcher.group(1) == null ? DEFAULT_IP : matcher.group(1);
            int port = matcher.group(2) == null ? DEFAULT_PORT : Integer.parseInt(matcher.group(2));
            httpHosts.add(new HttpHost(ip, port, useSsl ? "https" : "http"));
        }
        String token = username + ":" + password;
        clientMap.put(url, initClient(httpHosts, useSsl, ClusterMode.getByValue(mode), token));
        return clientMap.get(url);

    }

    private Map<String,Object> parseUrlParam(String url){
        if (StringUtils.containsIgnoreCase(url, JdbcConstants.ELASTIC_DRIVER_PREFIX)) {
            throw new RuntimeException("[" + url + "] is an invalid elasticsearch jdbc url");
        }
        if (url.contains(JdbcConstants.COND)) {
            String subString = url.substring(url.indexOf(JdbcConstants.COND) + 1);
            String[] items = subString.split("[&]");
            Map<String, Object> param = new HashMap<>();
            for (String item : items) {
                String[] kv = item.split("=");
                param.put(kv[0], kv[1]);
            }
            return param;
        }
        return Maps.newHashMap();
    }

    private RestHighLevelClient initClient(List<HttpHost> httpHosts,boolean useSSL,ClusterMode mode, String token){
        RestHighLevelClient restHighLevelClient;
        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts.toArray(new HttpHost[0]))
                .setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS)
                .setRequestConfigCallback(builder -> builder.setConnectTimeout(50000)
                        .setSocketTimeout(600000));
        if (useSSL){
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                // todo 解决ssl加解密问题
                httpAsyncClientBuilder.setSSLHostnameVerifier((s, sslSession) -> true);
                SSLContext sslContext;
                try {
                    sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    throw new RuntimeException(e.getMessage());
                }
                httpAsyncClientBuilder.setSSLContext(sslContext);
                String basicToken = Base64.getEncoder().encodeToString(token.getBytes());
                httpAsyncClientBuilder.setDefaultHeaders(Collections.singletonList(new BasicHeader("Authorization", "Basic " + basicToken)));
                return httpAsyncClientBuilder;
            });
        }
        if (mode==ClusterMode.CLUSTER){
            // 失败后立刻进行更新嗅探
            SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
            restClientBuilder.setFailureListener(sniffOnFailureListener);
            restHighLevelClient = new RestHighLevelClient(restClientBuilder);

            // 是否使用https服务 如果是则需要重新实现NodesSniffer
            NodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(
                    restHighLevelClient.getLowLevelClient(),
                    ElasticsearchNodesSniffer.DEFAULT_SNIFF_REQUEST_TIMEOUT,
                    useSSL ? ElasticsearchNodesSniffer.Scheme.HTTPS : ElasticsearchNodesSniffer.Scheme.HTTP);

            Sniffer sniffer = Sniffer.builder(restHighLevelClient.getLowLevelClient())
                    .setSniffAfterFailureDelayMillis(30000).setNodesSniffer(nodesSniffer)
                    .build();
            sniffOnFailureListener.setSniffer(sniffer);
        }
        else{
            restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        }
        return restHighLevelClient;
    }


    public static void main(String[] args) {
        String url="https://www.baidu.com?p=1&a=12&c=1234";
        String subString = url.substring(url.indexOf(JdbcConstants.COND)+1);
        String[] items = subString.split("[&]");
        for (String i:items){
            System.out.println(i);
        }
    }
}
