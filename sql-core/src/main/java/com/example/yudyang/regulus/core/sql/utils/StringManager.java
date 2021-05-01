package com.example.yudyang.regulus.core.sql.utils;

public class StringManager {
    public static String removeStringSymbol(String str){
        return str.replaceAll("[`']","");
    }
}
