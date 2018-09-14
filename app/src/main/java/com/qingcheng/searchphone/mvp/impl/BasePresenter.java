package com.qingcheng.searchphone.mvp.impl;

import android.content.Context;

/**
 * Created by nanxiaomu on 2018/3/1.
 */

public class BasePresenter {
    Context mContext;
    public void attach(Context context){
        mContext = context;
    }
    public void onPause(){}
    public void onResume(){}
    public void onDestroy(){
        mContext = null;
    }
}
