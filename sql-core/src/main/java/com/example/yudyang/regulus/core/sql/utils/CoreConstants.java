package com.example.yudyang.regulus.core.sql.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CoreConstants {

    public  static ObjectMapper OBJECT_MAPPER=new ObjectMapper();


    String COMMA = ",";
    String COLON = ":";
    String DOLLAR = "$";
    String MODULE = "%";
    String STAR = "*";
    String UNDERLINE = "_";
    String COND = "?";
    String DOT=".";
    String GRAVE_ACCENT="`";
    String UP_ARROW="^";
    String POUND="#";
    String DEFAULT_ES_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
