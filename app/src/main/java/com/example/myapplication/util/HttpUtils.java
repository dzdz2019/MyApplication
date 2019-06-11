package com.example.myapplication.util;

import android.content.Context;
import android.util.Log;


import com.example.myapplication.util.TextCallBack;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/17.
 */
public class HttpUtils {

    static OkHttpClient sClient = null;

    public synchronized static OkHttpClient getClient(Context context)
    {
        if(sClient == null)
        {
            sClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10,TimeUnit.SECONDS)
                    .build();
        }

        return sClient;
    }

    public static void postSync(String str)
    {
//        private static final String ENDPOINT = "https://api.github.com/repos/square/okhttp/contributors";
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(str)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void get(Context context, String url, HashMap<String,String> params, TextCallBack callBack)
    {
        OkHttpClient client = getClient(context);
        Set<Map.Entry<String, String>> set = params.entrySet();

        boolean isFirst = true;
        for (Map.Entry<String, String> entry:set)
        {
            if(isFirst)
            {
                url += "?";
                isFirst = false;
            }else
            {
                url += "&";
            }
            url += entry.getKey()+"="+entry.getValue();
        }
        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(callBack);
    }
    public static void post(Context context, String url, HashMap<String,String> params, TextCallBack callBack)
    {
        OkHttpClient client = getClient(context);
        FormBody.Builder builder = new FormBody.Builder();
        Set<Map.Entry<String, String>> set = params.entrySet();
        for (Map.Entry<String, String> entry:set)
        {
            builder.add(entry.getKey(),entry.getValue());
        }
        Request request = new Request.Builder().url(url).post(builder.build())
                .build();
        client.newCall(request).enqueue(callBack);
    }
    public static void post(Context context, String url, JSONObject jsonObject, TextCallBack callBack)
    {
        OkHttpClient client = getClient(context);
        FormBody.Builder builder = new FormBody.Builder();
        Iterator<String> keys = jsonObject.keys();
        Log.d(url, "post: "+jsonObject);
        while (keys.hasNext())
        {
            String key = String.valueOf(keys.next());
            try {
                String value = jsonObject.get(key).toString();
                builder.add(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        client.newCall(request).enqueue(callBack);
    }

    public static void postSync(Context context, String url, JSONObject jsonObject)
    {
        OkHttpClient client = getClient(context);
        Log.e("gouwuche",url+jsonObject);
        FormBody.Builder builder = new FormBody.Builder();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext())
        {
            String key = String.valueOf(keys.next());
            try {
                String value = jsonObject.get(key).toString();
                builder.add(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postVideo(Context context, String url, JSONObject jsonObject, TextCallBack callBack)
    {
        OkHttpClient client = getClient(context);

        FormBody.Builder builder = new FormBody.Builder();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext())
        {
            String key = String.valueOf(keys.next());
            try {
                String value = jsonObject.get(key).toString();
                byte[] bytes = string2bytes(value);
                String byte2String = new String(bytes);
                builder.add(key,byte2String);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Request request = new Request.Builder().url(url)
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(callBack);
    }

    private static byte[] string2bytes(String filePath)
    {
        FileInputStream fileInputStream =null;
        byte[] bytes ;
        try {
            fileInputStream = new FileInputStream(new File(filePath));
            bytes = new byte[1024];
            int read = 0;
            while ((read = fileInputStream.read()) != -1)
            {
                fileInputStream.read(bytes,0,read);
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class StringConverter implements JsonSerializer<String>,
            JsonDeserializer<String> {
        public JsonElement serialize(String src, Type typeOfSrc,
                        JsonSerializationContext context) {
            if (src == null) {
                return new JsonPrimitive("");
            } else {
                return new JsonPrimitive(src.toString());
            }
        }

        public String deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context)
                throws JsonParseException {
            return json.getAsJsonPrimitive().getAsString();
        }

    }
}
