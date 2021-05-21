package com.example.yudyang.regulus.plugin;

import com.example.yudyang.regulus.core.sql.model.ElasticSqlParseResult;
import com.example.yudyang.regulus.core.sql.parser.ElasticSql2DslParser;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestSqlAction extends BaseRestHandler {

    private final ElasticSql2DslParser elasticSql2DslParser;

    public RestSqlAction() {
        this.elasticSql2DslParser = new ElasticSql2DslParser();
    }

    @Override
    public String getName() {
        return "regulus";
    }

    @Override
    public List<Route> routes() {
        List<RestHandler.Route> routes = new ArrayList<>(4);
        routes.add(new Route(RestRequest.Method.GET, "/_regulus/_test"));
        return routes;
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) throws IOException {
        System.out.println("Hello");
        try (XContentParser parser = restRequest.contentOrSourceParamParser()) {
            parser.mapStrings().forEach((k, v) -> restRequest.params().putIfAbsent(k, v));
        } catch (IOException e) {
            return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, XContentType.JSON.mediaType(), "please use json format params, like: {\"sql\":\"select * from student\"}"));
        }
        try {
            String sql = restRequest.param("sql");
            if (StringUtils.isBlank(sql)) {
                return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, XContentType.JSON.mediaType(), "{\n\t\"error\":\"sql can not be empty!!!\"\n}"));
            }
//            ElasticSqlParseResult parseResult = sql2DslParser.parse(sql);
            XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
            return explain(new ElasticSqlParseResult(),builder);
//            if (restRequest.path().endsWith("/_explain")) {
//                return explain(parseResult, builder);
//            } else {
//                return execute(parseResult, builder, nodeClient);
//            }
        } catch (Exception e) {
            return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.INTERNAL_SERVER_ERROR, XContentType.JSON.mediaType(), "{\n\t\"error\":\"" + e.getMessage() + "\"\n}"));
        }
    }

    private RestChannelConsumer explain(ElasticSqlParseResult parseResult, XContentBuilder builder) {
//        if (parseResult.getSqlOperation() == SqlOperation.SELECT) {
//            return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder.value(parseResult.getSearchRequest().source())));
//        }
        return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.BAD_REQUEST, XContentType.JSON.mediaType(), "{\n\t\"error\":\"not support explaining!!!!! desc,delete,update syntax yet!!!\"\n}"));
    }
}
