package com.example.bigwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GoodsListAdapter extends BaseAdapter {
    private Context context;
    private List<Goods> goodsList;
    private OnAddToCartListener listener;
    private CartManager cartManager;

    public GoodsListAdapter(Context context, List<Goods> goodsList, OnAddToCartListener listener) {
        this.context = context;
        this.goodsList = goodsList;
        this.listener = listener;
        this.cartManager = CartManager.getInstance();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goods, parent, false);
            holder = new ViewHolder();
            holder.ivGoods = convertView.findViewById(R.id.iv_goods_img);
            holder.tvName = convertView.findViewById(R.id.tv_goods_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_goods_price);
            holder.btnAddCart = convertView.findViewById(R.id.btn_add_cart);
            holder.btnRemoveCart = convertView.findViewById(R.id.btn_remove_cart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Goods goods = goodsList.get(position);
        holder.ivGoods.setImageResource(goods.getImageResId());
        holder.tvName.setText(goods.getName());
        holder.tvPrice.setText("¥" + String.format("%.2f", goods.getPrice()));

        // 检查购物车中是否有该商品（基于ID和默认口味）
        Goods tempGoods = new Goods(goods.getId(), goods.getName(), goods.getPrice(), goods.getImageResId());
        boolean hasInCart = cartManager.hasGoodsInCart(tempGoods);
        updateButtonState(holder, hasInCart);

        // 添加到购物车按钮点击事件
        holder.btnAddCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdd(goods); // 这里会触发 GoodsFragment 中的处理逻辑
            }
        });

        // 从购物车移除按钮点击事件
        holder.btnRemoveCart.setOnClickListener(v -> {
            // 移除该商品的所有口味
            cartManager.removeAllGoodsById(goods.getId());
            updateButtonState(holder, false);
            // 刷新购物车
            if (context instanceof MainActivity) {
                ((MainActivity) context).refreshCartFragment();
            }
        });

        return convertView;
    }


    private void updateButtonState(ViewHolder holder, boolean hasInCart) {
        if (hasInCart) {
            holder.btnAddCart.setVisibility(View.GONE);
            holder.btnRemoveCart.setVisibility(View.VISIBLE);
            holder.btnRemoveCart.setBackgroundResource(R.drawable.btn_red_bg);
            holder.btnRemoveCart.setEnabled(true);
        } else {
            holder.btnAddCart.setVisibility(View.VISIBLE);
            holder.btnRemoveCart.setVisibility(View.GONE);
            holder.btnRemoveCart.setBackgroundResource(R.drawable.btn_gray_bg);
            holder.btnRemoveCart.setEnabled(false);
        }
    }

    static class ViewHolder {
        ImageView ivGoods;
        TextView tvName, tvPrice;
        Button btnAddCart, btnRemoveCart;
    }

    public interface OnAddToCartListener {
        void onAdd(Goods goods);
    }
}