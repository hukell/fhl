package com.mb.fhl.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mb.fhl.R;
import com.mb.fhl.models.ShopBean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ListHolder>{
    private Context mContext;
    private List<ShopBean.OrderinfoBean.GoodsBean> mGoods;

    public ItemAdapter(Context context,List<ShopBean.OrderinfoBean.GoodsBean> goods) {
        mContext = context;
        mGoods = goods;
    }

    @Override
    public ItemAdapter.ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  inflate = LayoutInflater.from(mContext).inflate(R.layout.recycle_item_item, parent, false);
        return new ListHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ListHolder holder, int position) {
        holder.mTvGoods.setText(mGoods.get(position).goodname+"         x"+mGoods.get(position).goodnum);
        holder.mTvPrica.setText("￥"+mGoods.get(position).goodsprice);
    }

    @Override
    public int getItemCount() {
        return mGoods.size();
    }

    class ListHolder extends RecyclerView.ViewHolder{


        private final TextView mTvGoods;
        private final TextView mTvPrica;

        public ListHolder(View itemView) {
            super(itemView);
            mTvGoods = (TextView) itemView.findViewById(R.id.tv_goods);
            mTvPrica = (TextView) itemView.findViewById(R.id.tv_price);

        }

    }
}
