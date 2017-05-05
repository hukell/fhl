package com.mb.fhl.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mb.fhl.R;
import com.mb.fhl.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OrderDataFragment extends BaseFragment implements TabLayout.OnTabSelectedListener{

    @Bind(R.id.tabs)
    TabLayout mTabs;
    private FragmentManager mFragmentManager;
    private ChartFragment mChartFragment;
    private String[] Titles;
    private ChartListFragment mChartListFragment;

    public static OrderDataFragment newInstance() {
        Bundle bundle = new Bundle();
        OrderDataFragment orderDataFragment = new OrderDataFragment();
        orderDataFragment.setArguments(bundle);
        return orderDataFragment;
    }


    @Override
    protected void init() {
        mFragmentManager = getChildFragmentManager();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order_data;
    }

    @Override
    protected void initView(View view) {
        getStatus();

    }

    @Override
    protected void initOperation() {

    }


    private void getStatus() {

        Titles = new String[]{"图表数据", "数据"};

        for (int i = 0; i < Titles.length; i++) {
            mTabs.addTab(mTabs.newTab().setText(Titles[i])); //添加tab
        }
        mTabs.addOnTabSelectedListener(this);
        mTabs.getTabAt(0).select();
        addFragment("图表数据");
    }


    /**
     * 添加Fragment
     */
    public void addFragment(String i) {
        //开启一个事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        //先全部隐藏
        hideFragment(transaction);
        switch (i) {
            case "图表数据":
                if (mChartFragment == null) {
                    mChartFragment = ChartFragment.newInstance();
                    transaction.add(R.id.replace, mChartFragment);
                } else {
                    transaction.show(mChartFragment);
                }
                break;
            case "数据":
                if (mChartListFragment == null) {
                    mChartListFragment = ChartListFragment.newInstance();
                    transaction.add(R.id.replace, mChartListFragment);
                } else {
                    transaction.show(mChartListFragment);
                }

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
        if (mChartFragment != null) {
            transaction.hide(mChartFragment);
        } if (mChartListFragment != null) {
            transaction.hide(mChartListFragment);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getText().equals("图表数据")) {
            addFragment("图表数据");
        } else if (tab.getText().equals("数据")) {
            addFragment("数据");
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
