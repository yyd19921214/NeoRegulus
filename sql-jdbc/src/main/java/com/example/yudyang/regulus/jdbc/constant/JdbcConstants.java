package com.example.yudyang.regulus.jdbc.constant;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public interface JdbcConstants {
    int DEFAULT_PORT = 9200;

    String DEFAULT_IP = "localhost";

    String ELASTIC_DRIVER_PREFIX = "jdbc:es:";

    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    Pattern IP_PORT_PATTERN = Pattern.compile("://(.*):(\\d+)/");

    Scroll SCROLL = new Scroll(TimeValue.timeValueMinutes(1));

    int DEFAULT_SCROLL_SIZE = 3000;

    String COND = "?";

}
