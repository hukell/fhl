package com.mb.fhl.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mb.fhl.R;
import com.mb.fhl.models.OrderBean;
import com.mb.fhl.models.ShopBean;
import com.mb.fhl.ui.ItemAdapter;
import com.mb.fhl.utils.DialogUtils;
import com.mb.fhl.utils.DisplayUtil;

import java.util.List;

/**
 * Created by admin on 2017/4/26.
 */

public class MerchantAdapter extends BaseQuickAdapter<ShopBean.OrderinfoBean, BaseViewHolder>{

    public MerchantAdapter(@LayoutRes int layoutResId, @Nullable List<ShopBean.OrderinfoBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShopBean.OrderinfoBean item) {
        final RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.item_recyclerview);
        TextView tvSq = (TextView) helper.getView(R.id.tv_sq);
        final TextView tvPsPhone = (TextView) helper.getView(R.id.tv_ps_phone);

        tvPsPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showPopPhoneDown((Activity) mContext,tvPsPhone);
            }
        });


        ItemAdapter itemAdapter = new ItemAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemAdapter);

        final int itemCount = itemAdapter.getItemCount()*40;

        tvSq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int measuredHeight = recyclerView.getMeasuredHeight();

                ValueAnimator anim;

                if (measuredHeight != 0){
                    anim = ValueAnimator.ofFloat(DisplayUtil.dip2px(mContext,itemCount), 0);
                }else {
                    anim = ValueAnimator.ofFloat(0, DisplayUtil.dip2px(mContext,itemCount));
                }
                anim.setDuration(300);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float currentValue = (float) animation.getAnimatedValue();
                        long duration = animation.getDuration();
                        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                        linearParams.height = (int)currentValue;// 控件的高强制设成20
                        recyclerView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                    }
                });
                anim.start();
            }
        });

    }

}
