package com.example.bigwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SettleAdapter extends BaseAdapter {
    private Context context;
    private List<Goods> goodsList;

    public SettleAdapter(Context context, List<Goods> goodsList) {
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
        return goodsList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 使用正确的布局文件 R.layout.item_settle
            convertView = LayoutInflater.from(context).inflate(R.layout.item_settle, parent, false);
            holder = new ViewHolder();
            // 正确初始化所有视图组件
            holder.ivImg = convertView.findViewById(R.id.iv_settle_img);
            holder.tvName = convertView.findViewById(R.id.tv_settle_name);
            holder.tvTaste = convertView.findViewById(R.id.tv_settle_taste);
            holder.tvPrice = convertView.findViewById(R.id.tv_settle_price);
            holder.tvCount = convertView.findViewById(R.id.tv_settle_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前商品数据
        Goods goods = goodsList.get(position);
        // 设置商品信息
        holder.ivImg.setImageResource(goods.getImageResId());
        holder.tvName.setText(goods.getName());
        // 确保结算页面口味显示正确
        String tasteDisplay = formatTasteForDisplay(goods.getTaste());
        holder.tvTaste.setText("口味：" + tasteDisplay);
        holder.tvPrice.setText("¥" + String.format("%.2f", goods.getPrice()));
        holder.tvCount.setText("×" + goods.getCount());

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

    // ViewHolder类
    static class ViewHolder {
        ImageView ivImg;
        TextView tvName;
        TextView tvTaste;
        TextView tvPrice;
        TextView tvCount;
    }
}