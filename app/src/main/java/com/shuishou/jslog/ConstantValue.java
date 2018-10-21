package com.shuishou.jslog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/12/22.
 */

public final class ConstantValue {
    public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DOLLAR = "$";
    public static final String DOLLARSPACE = "$ ";
    public static final String NULLSTRING = "";
    public static final String SPACESTRING = " ";
    public static final String SLLASHSTRING = " ";
    public static final String ENTERSTRING = "\n";

    public static final String FORMAT_DOUBLE = "%.2f";

    public static final String ERRORLOGPATH = "/data/data/com.shuishou.jslog/errorlog/";
    public static final String ERRORLOGFILENAME = "jslog_log";
    
    public static String URL_TOMCAT = "http://192.168.1.221:1111";

}
