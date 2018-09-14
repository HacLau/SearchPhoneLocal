package com.qingcheng.searchphone.business;



import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nanxiaomu on 2018/3/1.
 */

public class HttpUntil {
    String mUrl;
    Map<String ,String> mParam;
    HttpResponse httpResponse;
    private final OkHttpClient client = new OkHttpClient();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            Log.e("result",result);
            switch (result){
                case "1":
                    httpResponse.onFail("请求错误");
                    break;
                case "2":
                    httpResponse.onFail("请求失败：code" + data.getString("value"));
                    break;
                case "3":
                    httpResponse.onFail("请求失败");
                    break;
                default:
                    httpResponse.onSuccess(result);
            }
        }
    };
    public interface HttpResponse{
        void onSuccess(Object object);
        void onFail(Object object);
    }

    public HttpUntil(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public void sendPostHttp(String url, Map<String ,String> param){
        sendHttp(url,param,true);
    }
    public void sendGetHttp(String url, Map<String ,String> param){
        sendHttp(url,param,false);
    }
    public void sendHttp(String url, Map<String ,String> param,boolean isPost){
        mUrl = url;
        mParam = param;
        run(isPost);
    }

    private void run(boolean isPost){
        Request request = createRequest(isPost);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(httpResponse != null){
                   /* handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpResponse.onFail("请求错误");
                        }
                    });*/
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result","1");
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(httpResponse == null)
                    return;
                final String result = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!response.isSuccessful()){
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("result","2");
                            data.putString("value",result + "");
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("result",result);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }
                });
            }
        });
    }

    private Request createRequest(boolean isPost){
        Request request;
        if(isPost){
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
            requestBodyBuilder.setType(MultipartBody.FORM);
            Iterator<Map.Entry<String,String>> iterator = mParam.entrySet().iterator();
            while ((iterator.hasNext())){
                Map.Entry<String,String> entry = iterator.next();
                requestBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue());
            }
            request = new Request.Builder().url(mUrl).post(requestBodyBuilder.build()).build();
        }else{
            String urlStr = mUrl + "?" + MapParamToString(mParam);
            request = new Request.Builder().url(urlStr).build();
        }
        return request;
    }

    private String MapParamToString(Map<String,String> param){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String,String>> iterator = param.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            stringBuilder.append(entry.getKey() + "=" + entry.getValue()+"&");
        }
        String str = stringBuilder.toString().substring(0,stringBuilder.length() - 1);
        return str;
    }
}
