package com.mb.fhl.ui;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mb.fhl.R;
import com.mb.fhl.base.BaseActivity;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.User;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.MD5;
import com.mb.fhl.utils.T;
import com.mb.fhl.utils.TextUtil;
import com.mb.fhl.utils.UserManager;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_name)
    EditText mEtName;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.tv_sure)
    TextView mTvSure;
    @Bind(R.id.radiogroup)
    RadioGroup mRadiogroup;
    private int mUserType = 1;

    @Override
    protected void setParams() {
        if (UserManager.getIns().getUser()!=null){
            if (UserManager.getIns().getUser().userType.equals("1")){
                startActivity(new Intent(LoginActivity.this,MerchantActivity.class));
                finish();
            }else {
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                finish();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        mRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.ra_sj:
                        mUserType = 1;
                        break;
                    case R.id.ra_psy:
                        mUserType = 2;
                        break;
                }
            }
        });

    }

    @Override
    protected void releaseResource() {

    }

    @OnClick(R.id.tv_sure)
    public void onViewClicked() {
        if (TextUtil.isSpace(mEtName.getText().toString())){
            T.showLong(this,"请输入用户名");
            return;
        }if (TextUtil.isSpace(mEtPassword.getText().toString())){
            T.showLong(this,"请输入密码");
            return;
        }
        login();
    }


    public void login(){
        UserManager.getIns().clearUserInfo();
        HashMap<String, Object> params = new HashMap<>();

        params.put("phoneNum",mEtName.getText().toString());
        if (mUserType==1){
            params.put("userPass",mEtPassword.getText().toString());
        }else {
            params.put("userPass", MD5.encryptMD5(mEtPassword.getText().toString()));
        }

        params.put("type","android");
        params.put("type",1);
        params.put("userType",mUserType);


        Api.getRetrofit().login(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new BaseSubscriber<BaseBean<User>>(LoginActivity.this){
                   @Override
                   public void onNext(BaseBean<User> userBaseBean) {
                       super.onNext(userBaseBean);
                       UserManager.getIns().saveUserInfo(userBaseBean.getData()); //保存用户信息

                       if (userBaseBean.getData().userType.equals("1")){
                           startActivity(new Intent(LoginActivity.this,MerchantActivity.class));
                       }
                       else {
                           startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                       }

                       finish();
                   }
               });
    }

}
