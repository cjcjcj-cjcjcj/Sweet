package com.example.bigwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bigwork.R;

import java.util.List;

public class OrderDetailGoodsAdapter extends BaseAdapter {
    private Context context;
    private List<Goods> goodsList;

    public OrderDetailGoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_detail_goods, parent, false);
            holder = new ViewHolder();
            holder.ivGoodsImg = convertView.findViewById(R.id.iv_detail_goods_img);
            holder.tvGoodsName = convertView.findViewById(R.id.tv_detail_goods_name);
            holder.tvGoodsTaste = convertView.findViewById(R.id.tv_detail_goods_taste);
            holder.tvGoodsPrice = convertView.findViewById(R.id.tv_detail_goods_price);
            holder.tvGoodsCount = convertView.findViewById(R.id.tv_detail_goods_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Goods goods = goodsList.get(position);
        holder.ivGoodsImg.setImageResource(goods.getImageResId());
        holder.tvGoodsName.setText(goods.getName());
        // 确保订单详情口味显示正确
        String tasteDisplay = formatTasteForDisplay(goods.getTaste());
        holder.tvGoodsTaste.setText("口味：" + tasteDisplay);
        holder.tvGoodsPrice.setText("¥" + String.format("%.2f", goods.getPrice()));
        holder.tvGoodsCount.setText(" x" + goods.getCount());

        return convertView;
    }

    // 添加口味显示格式化方法
    private String formatTasteForDisplay(String taste) {
        if (taste == null || taste.isEmpty()) {
            return "默认";
        }
        // 确保显示格式一致
        return taste.replace("三分线", "三分糖")
                .replace("个式", "全糖")
                .replace("默认", "全糖 + 标准冰");
    }

    static class ViewHolder {
        ImageView ivGoodsImg;
        TextView tvGoodsName, tvGoodsTaste, tvGoodsPrice, tvGoodsCount;
    }
}