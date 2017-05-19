package com.mb.fhl.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.fhl.R;
import com.mb.fhl.base.BaseActivity;
import com.mb.fhl.models.BaseBean;
import com.mb.fhl.models.ChangBean;
import com.mb.fhl.models.DateBean;
import com.mb.fhl.net.Api;
import com.mb.fhl.net.BaseSubscriber;
import com.mb.fhl.utils.DialogUtils;
import com.mb.fhl.utils.ImageLoader;
import com.mb.fhl.utils.RxBus;
import com.mb.fhl.utils.TimeUtils;
import com.mb.fhl.utils.UserManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.util.Calendar;
import java.util.HashMap;
import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.mb.fhl.R.id.tv_change;

public class MerchantActivity extends BaseActivity implements
        DatePickerDialog.OnDateSetListener{

    @Bind(R.id.img_head)
    ImageView mImgHead;
    @Bind(tv_change)
    TextView mTvChange;
    @Bind(R.id.tv_date)
    TextView mTvDate;

    @Bind(R.id.re_title)
    RelativeLayout mReTitle;


    private Calendar mNow;
    private int mMonthOfYear = -1;
    private int mDayOfMonth= -1;
    private int mYear= -1;

    private Subscription mSubscribe;
    private String mDate1 = TimeUtils.getNowTimeString("yyyy-MM-dd");
    private String mRegistrationID;
    private long exitTime;
    private FragmentManager mFragmentManager;
    private OrderFragment mTakeOutorderFragment;
    private OrderFragment mTakeInorderFragment;
    private OrderDataFragment mOrderDtaFragment;
    private int mOrderStyle = 2;
    private OrderFragment mAdvanceInorderFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_merchant;
    }

    @Override
    protected void initView() {
        mOrderStyle = getIntent().getIntExtra("orderStyle",2);

        //deviceToken 激光
        mRegistrationID = JPushInterface.getRegistrationID(this);
        mFragmentManager = getSupportFragmentManager();

        ImageLoader.loadCicleImage(this, UserManager.getIns().getUser().logo, R.mipmap.img_home, mImgHead);
        mNow = Calendar.getInstance();
        mTvDate.setText((1 + mNow.get(Calendar.MONTH)) + "月" + mNow.get(Calendar.DAY_OF_MONTH) + "日");

        mSubscribe = RxBus.getInstance().toObserverable(ChangBean.class)
                .subscribe(new Action1<ChangBean>() {
                    @Override
                    public void call(ChangBean changBean) {
                        addFragment(changBean.orderStyle);
                    }
                });
        addFragment(mOrderStyle);
        uploadRegistrationId();
    }

    @Override
    protected void releaseResource() {
        if (!mSubscribe.isUnsubscribed()){
            mSubscribe.unsubscribe();
        }
    }

    @OnClick({R.id.img_head, tv_change, R.id.tv_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                DialogUtils.submitLogOut(this);
                break;
            case tv_change:
                DialogUtils.showPopTitleDown(this,mReTitle);
                break;
            case R.id.tv_date:
                  //日历选择日期
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MerchantActivity.this,
                        mYear!=-1?mYear:mNow.get(Calendar.YEAR),
                        mMonthOfYear!=-1?mMonthOfYear:mNow.get(Calendar.MONTH),
                        mDayOfMonth!=-1?mDayOfMonth:mNow.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getFragmentManager(), "Datepickerdialog");

                break;
        }
    }

    //日历选择回调
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date =(++monthOfYear)+"月"+ dayOfMonth +"日";
        mDate1 = year+"-"+(monthOfYear)+"-"+ dayOfMonth;
        mYear = year;
        mMonthOfYear = monthOfYear-1;
        mDayOfMonth = dayOfMonth;
        mTvDate.setText(date);
        RxBus.getInstance().post(new DateBean(mDate1,date));

    }

    /**
     * 添加Fragment
     */
    public void addFragment(int i) {
        //开启一个事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        //先全部隐藏
        hideFragment(transaction);
        switch (i) {
            case 1:
                if (mTakeOutorderFragment == null) {
                    mTakeOutorderFragment = OrderFragment.newInstance(1);
                    transaction.add(R.id.replace, mTakeOutorderFragment);
                } else {
                    transaction.show(mTakeOutorderFragment);
                }
                mTvDate.setVisibility(View.VISIBLE);
                mTvChange.setText("外卖订单");
                break;
            case 2:
                if (mTakeInorderFragment == null) {
                    mTakeInorderFragment = OrderFragment.newInstance(2);
                    transaction.add(R.id.replace, mTakeInorderFragment);
                } else {
                    transaction.show(mTakeInorderFragment);
                }
                mTvDate.setVisibility(View.VISIBLE);
                mTvChange.setText("堂吃订单");
                break;
            case 4:
                if (mOrderDtaFragment == null) {
                    mOrderDtaFragment = OrderDataFragment.newInstance();
                    transaction.add(R.id.replace, mOrderDtaFragment);
                } else {
                    transaction.show(mOrderDtaFragment);
                }
                mTvDate.setVisibility(View.GONE);
                mTvChange.setText("销售数据");
                break;
            case 3:
                if (mAdvanceInorderFragment == null) {
                    mAdvanceInorderFragment = OrderFragment.newInstance(3);
                    transaction.add(R.id.replace, mAdvanceInorderFragment);
                } else {
                    transaction.show(mAdvanceInorderFragment);
                }
                mTvDate.setVisibility(View.VISIBLE);
                mTvChange.setText("预定订单");
                break;

        }
        //提交事务
        //  transaction.commit();
        transaction.commitAllowingStateLoss();
    }
    /**
     * 隐藏Fragment
     *
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (mTakeInorderFragment != null) {
            transaction.hide(mTakeInorderFragment);
        }
        if (mTakeOutorderFragment != null) {
            transaction.hide(mTakeOutorderFragment);
        } if (mOrderDtaFragment != null) {
            transaction.hide(mOrderDtaFragment);
        }if (mAdvanceInorderFragment != null) {
            transaction.hide(mAdvanceInorderFragment);
        }
    }

    /**
     * 双击退出程序
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //激光推送
    public void uploadRegistrationId() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("userId",UserManager.getIns().getUser().uid);
        params.put("registrationId", mRegistrationID);
        params.put("userType", 1);
        params.put("type", 1);
        params.put("token",UserManager.getIns().getUser().accessToken);

        Api.getRetrofit().upDeviceToken(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean>(MerchantActivity.this){
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onNext(BaseBean javaBean) {
                        super.onNext(javaBean);
                    }
                });
    }

}

