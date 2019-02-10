package com.android.renly.meetingreservation.module.user.login;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.renly.meetingreservation.App;
import com.android.renly.meetingreservation.R;
import com.android.renly.meetingreservation.api.RetrofitService;
import com.android.renly.meetingreservation.listener.MyTextWatcher;
import com.android.renly.meetingreservation.module.base.BaseActivity;
import com.android.renly.meetingreservation.utils.toast.MyToast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.myToolBar)
    FrameLayout myToolBar;
    @BindView(R.id.name)
    TextInputEditText name;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.cb_rem_user)
    CheckBox cbRemUser;
    @BindView(R.id.login)
    Button login;
    public static final int requestCode = 64;

    private MyTextWatcher textWatcher = new MyTextWatcher() {
        @Override
        public void afterMyTextChanged(Editable editable) {
            btnLoginSetEnabled();
        }
    };

    @Override
    protected int getLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        initToolBar(true,"登陆账号");
        initSlidr();
        initText();

        btnLoginSetEnabled();
        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> showSoftInput());
    }

    private void initText() {
        if (App.isRemeberPwdUser()) {
            name.setText(App.getEmail());
            password.setText(App.getPwd());
            cbRemUser.setChecked(true);
        } else{
            name.setText(App.getEmail());
            cbRemUser.setChecked(false);
        }
        name.setSelection(name.getText().length());
        password.setSelection(password.getText().length());

        name.addTextChangedListener(textWatcher);

        password.addTextChangedListener(textWatcher);
    }

    /**
     * 设置判断按钮是否可以点击
     */
    private void btnLoginSetEnabled() {
        if (!TextUtils.isEmpty(name.getText().toString().trim())
                && !TextUtils.isEmpty(password.getText().toString().trim()))
            login.setEnabled(true);
        else
            login.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.login, R.id.register, R.id.forgetPwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                String email = name.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd))
                    doLogin(email, pwd);
                break;
            case R.id.register:
//                jumpToActivity(SignActivity.class);
                break;
            case R.id.forgetPwd:
//                jumpToActivity(FindpwdActivity.class);
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void doLogin(String email, String pwd) {

//        RetrofitService.doLogin(email, pwd)
//                .subscribe(responseBody -> {
//                    JSONObject obj = null;
//                    try {
//                        obj = JSON.parseObject(responseBody.string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    int statusCode = obj.getInteger("code");
//                    String token = obj.getString("result");
//                    printLog("code:" + statusCode + " result:" + token);
//
//                    if (statusCode == 20000){
//                        afterLoginSuccess(email, pwd, token);
//                    }else{
//                        afterLoginFail();
//                    }
//                });
    }

    private void afterLoginFail() {
        MyToast.showText(getApplicationContext(),"账号或密码错误", Toast.LENGTH_SHORT,false);
    }

    private void afterLoginSuccess(String email, String pwd, String token) {
        SharedPreferences sp = getSharedPreferences(App.MY_SP_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(App.USER_EMAIL_KEY, email);
        editor.putString(App.USER_PWD_KEY, pwd);
        editor.putBoolean(App.IS_REMEBER_PWD_USER,cbRemUser.isChecked());
        editor.putBoolean(App.IS_LOGIN, true);
        editor.apply();
        MyToast.showText(getApplicationContext(),"登录成功", Toast.LENGTH_SHORT,true);
        printLog("登录成功");

        getUserInfo();
    }

    /**
     * 登陆成功后获取用户信息
     */
    @SuppressLint("CheckResult")
    private void getUserInfo() {
//        RetrofitService.getUserDetails()
//                .subscribe(responseBody -> {
//                    JSONObject jsonObject = JSON.parseObject(responseBody.string());
//                    if (jsonObject.getInteger("code") != 20000){
//                        ToastShort("服务器出状况惹，再试试( • ̀ω•́ )✧");
//                    }else{
//                        afterGetUserInfo(jsonObject.getString("result"));
//                    }
//                });
    }

    private void afterGetUserInfo(String jsonObj){
        JSONObject obj = JSON.parseObject(jsonObj);
        SharedPreferences sp = getSharedPreferences(App.MY_SP_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(App.USER_UID_KEY, obj.getLong("id"));
        editor.apply();

        setResult(RESULT_OK);
        finishActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
