package com.qingcheng.searchphone;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qingcheng.searchphone.databinding.ActivityMainBinding;
import com.qingcheng.searchphone.model.Phone;
import com.qingcheng.searchphone.mvp.MvpMainView;
import com.qingcheng.searchphone.mvp.impl.MainPresenter;

public class MainActivity extends Activity implements MvpMainView {
    private ActivityMainBinding mBinding;
    MainPresenter mainPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        initView();
        mainPresenter = new MainPresenter(this);
        mainPresenter.attach(this);
    }

    private void initView() {
        mBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.searchPhoneInfo(mBinding.inputPhone.getText().toString().trim());
            }
        });
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hidenLoading() {

    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateView() {
        Phone phone = mainPresenter.getPhoneInfo();
        mBinding.resultPhone.setText("手机号："+mBinding.inputPhone.getText().toString().trim());
        mBinding.resultAddress.setText("归属地："+phone.getResult().getProvince() + phone.getResult().getCity());
        mBinding.resultType.setText("运营商：中国"+phone.getResult().getCompany());
    }
}
