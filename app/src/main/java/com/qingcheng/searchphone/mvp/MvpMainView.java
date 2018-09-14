package com.qingcheng.searchphone.mvp;



/**
 * Created by nanxiaomu on 2018/3/1.
 */

public interface MvpMainView extends MvpLoadingView{
    void showToast(String msg);
    void updateView();

}
