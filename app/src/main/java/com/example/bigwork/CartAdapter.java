package com.example.bigwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<Goods> cartList;
    private OnCartChangeListener listener;

    public CartAdapter(Context context, List<Goods> cartList, OnCartChangeListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    public void updateData(List<Goods> newCartList) {
        this.cartList = newCartList != null ? newCartList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cartList == null ? 0 : cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder();
            holder.ivImg = convertView.findViewById(R.id.iv_cart_img);
            holder.tvName = convertView.findViewById(R.id.tv_cart_name);
            holder.tvTaste = convertView.findViewById(R.id.tv_cart_taste);
            holder.tvPrice = convertView.findViewById(R.id.tv_cart_price);
            holder.tvCount = convertView.findViewById(R.id.tv_cart_count);
            holder.btnMinus = convertView.findViewById(R.id.btn_minus);
            holder.btnPlus = convertView.findViewById(R.id.btn_plus);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Goods goods = cartList.get(position);
        holder.ivImg.setImageResource(goods.getImageResId());
        holder.tvName.setText(goods.getName());
        holder.tvTaste.setText("口味：" + goods.getTaste());
        holder.tvPrice.setText("¥" + String.format("%.2f", goods.getPrice()));
        holder.tvCount.setText(String.valueOf(goods.getCount()));

        // 减少数量
        holder.btnMinus.setOnClickListener(v -> {
            System.out.println("点击减少: " + goods.getName() + " - " + goods.getTaste());
            CartManager.getInstance().removeOneGoods(goods);
            if (listener != null) {
                listener.onChange();
            }
            // 刷新当前item显示
            holder.tvCount.setText(String.valueOf(CartManager.getInstance().getGoodsCountInCart(goods)));
        });

        // 增加数量
        holder.btnPlus.setOnClickListener(v -> {
            System.out.println("点击增加: " + goods.getName() + " - " + goods.getTaste());
            Goods addGoods = new Goods(
                    goods.getId(),
                    goods.getName(),
                    goods.getPrice(),
                    goods.getImageResId(),
                    goods.getTaste(),
                    1
            );
            CartManager.getInstance().addGoods(addGoods);
            if (listener != null) {
                listener.onChange();
            }
            // 刷新当前item显示
            holder.tvCount.setText(String.valueOf(CartManager.getInstance().getGoodsCountInCart(goods)));
        });

        // 删除商品
        holder.btnDelete.setOnClickListener(v -> {
            System.out.println("点击删除: " + goods.getName() + " - " + goods.getTaste());
            CartManager.getInstance().removeGoods(goods);
            if (listener != null) {
                listener.onChange();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView ivImg;
        TextView tvName, tvTaste, tvPrice, tvCount;
        Button btnMinus, btnPlus, btnDelete;
    }

    public interface OnCartChangeListener {
        void onChange();
    }
}