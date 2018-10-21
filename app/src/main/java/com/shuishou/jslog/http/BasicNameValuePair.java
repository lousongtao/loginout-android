package com.shuishou.jslog.http;

public class BasicNameValuePair {
    private String key;
    private String value;
    
    public BasicNameValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        if (value == null)
            return "";
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
