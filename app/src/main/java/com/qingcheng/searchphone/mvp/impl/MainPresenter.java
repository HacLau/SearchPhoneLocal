package com.qingcheng.searchphone.mvp.impl;



import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qingcheng.searchphone.business.AnyHelper;
import com.qingcheng.searchphone.business.HttpUntil;
import com.qingcheng.searchphone.model.Phone;
import com.qingcheng.searchphone.mvp.MvpMainView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nanxiaomu on 2018/3/1.
 */

public class MainPresenter extends BasePresenter{
    MvpMainView mvpMainView;
    //String url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
    String url = "http://apis.juhe.cn/mobile/get";
    String key = "b5d00f2e7d0a834c46977bd293c5d4a8";
    Phone phoneAddress;

    public MainPresenter(MvpMainView mvpMainView) {
        this.mvpMainView = mvpMainView;
    }

    public void searchPhoneInfo(String phone){
        if (!AnyHelper.isMobileNO(phone)){
            mvpMainView.showToast("请输入正确的手机号码!!");
            return;
        }
        mvpMainView.showLoading();
        sendHttp(phone);
    }
    private void sendHttp(String phone){
        Map<String,String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("key",key);
        HttpUntil httpUntil = new HttpUntil(new HttpUntil.HttpResponse() {
            @Override
            public void onSuccess(Object object) {
                String json = object.toString();
                int index = json.indexOf("{");
                json = json.substring(index,json.length());
                Log.e("json",json);
                if(parseJson(json).getResultcode().equals("200")) {
                    mvpMainView.updateView();
                }
            }

            @Override
            public void onFail(Object object) {
                mvpMainView.showToast(object.toString());
                mvpMainView.hidenLoading();
            }
        });
        httpUntil.sendGetHttp(url,map);
    }

    private Phone parseJson(String json) {
        Gson gson = new Gson();
        phoneAddress = new Phone();
        phoneAddress = gson.fromJson(json, Phone.class);
        return phoneAddress;
    }

    public Phone getPhoneInfo() {
        return phoneAddress;
    }
}
