package com.shuishou.jslog.http;

import com.shuishou.jslog.ConstantValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {
    private static final int CONNECTION_TIMEOUT=30;
    private static OkHttpClient mOkHttpClient;
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        builder.cookieJar(new CookieJar() {
            //Cookie缓存区
            private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();
    
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                //移除相同的url的Cookie
                String host = url.host();
                List<Cookie> cookiesList = cookiesMap.get(host);
                if (cookiesList != null){
                    cookiesMap.remove(host);
                }
                //再重新天添加
                cookiesMap.put(host,cookies);
            }
    
            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookiesList = cookiesMap.get(url.host());
                //注：这里不能返回null，否则会报NULLException的错误。
                //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                return cookiesList != null ? cookiesList : new ArrayList<Cookie>();
            }
        });
        mOkHttpClient = builder.build();
    }
    
    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
    
    public static Request buildPostRequest(List<BasicNameValuePair> pairs, String url){
        Request.Builder builder = new Request.Builder().url(url);
        if (pairs != null && !pairs.isEmpty()){
            FormBody.Builder fBuilder = new FormBody.Builder();
            for (int i = 0; i < pairs.size(); i++) {
                fBuilder.add(pairs.get(i).getKey(), pairs.get(i).getValue());
            }
            builder.post(fBuilder.build());
        }
        return builder.build();
    }
    
    public static Request buildGetRequest(List<BasicNameValuePair> pairs, String url){
        if (pairs != null && !pairs.isEmpty()){
            url += "?";
            for (int i = 0; i < pairs.size(); i++) {
                if (i != 0){
                    url += "&";
                }
                url += pairs.get(i).getKey() + "=" + pairs.get(i).getValue();
            }
        }
        Request.Builder builder = new Request.Builder().url(url);
        return builder.build();
    }
    
    /**
     * 该方法不会开启异步线程。
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        request = buildCookiesForRequest(request);
        Response response = mOkHttpClient.newCall(request).execute();
        saveCookies(request.url(), response);
        return response;
    }
    
    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback){
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }
    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * @param request
     */
    public static void enqueue(Request request){
        mOkHttpClient.newCall(request).enqueue(new Callback() {
    
            @Override
            public void onFailure(Call call, IOException e) {
        
            }
    
            @Override
            public void onResponse(Call call, Response response) throws IOException {
        
            }
        });
    }
    
    public static String getStringFromServer(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    private static final String CHARSET_NAME = "UTF-8";
    /**
     * 这里使用了HttpClinet的API。只是为了方便
     * @param params
     * @return
     */
    public static String formatParams(List<BasicNameValuePair> params){
        String s = "";
        for (int i = 0; i < params.size(); i++) {
            if (i != 0){
                s += "&";
            }
            s += params.get(i).getKey() + "=" + params.get(i).getValue();
        }
        return s;
    }
    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params){
        return url + "?" + formatParams(params);
    }
    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value){
        return url + "?" + name + "=" + value;
    }
    
    /**
     * 从header中获取cookies
     * @param url
     * @param response
     * @throws IOException
     */
    public static void saveCookies(HttpUrl url, Response response){
        if (!response.isSuccessful()) return;
        if (mOkHttpClient.cookieJar() == CookieJar.NO_COOKIES) return;
        Headers headers = response.headers();
        List<Cookie> cookies = Cookie.parseAll(url, headers);
        if (cookies.isEmpty()) return;
        
        mOkHttpClient.cookieJar().saveFromResponse(url, cookies);
    }
    
    /**
     * 从缓存中读出cookie, 设置到request的header中
     * @param request
     * @return
     */
    public static Request buildCookiesForRequest(Request request){
        List<Cookie> cookies = mOkHttpClient.cookieJar().loadForRequest(request.url());
        if (!cookies.isEmpty()){
            StringBuilder cookieHeader = new StringBuilder();
            for (int i = 0, size = cookies.size(); i < size; i++) {
                if (i > 0) {
                    cookieHeader.append("; ");
                }
                Cookie cookie = cookies.get(i);
                cookieHeader.append(cookie.name()).append('=').append(cookie.value());
            }
            Request.Builder builder = request.newBuilder();
            builder.header("Cookie", cookieHeader.toString());
            return builder.build();
        }
        return request;
    }
}
