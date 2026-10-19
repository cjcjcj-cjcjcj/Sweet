package com.example.bigwork.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigwork.CartManager;
import com.example.bigwork.Goods;
import com.example.bigwork.GoodsData;
import com.example.bigwork.GoodsListAdapter;
import com.example.bigwork.MainActivity;
import com.example.bigwork.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GoodsFragment extends Fragment implements View.OnClickListener {
    private TextView tvMilkTea, tvCoffee, tvDessert; // 保持为TextView
    private ListView lvGoods;
    private GoodsListAdapter goodsAdapter;
    private List<Goods> currentGoodsList;
    private Goods selectedGoods; // 选中的商品
    private CartManager cartManager;
    private String currentCategory = "milk_tea"; // 当前选中的分类

    // 使用AtomicBoolean解决lambda表达式中的变量问题
    private AtomicBoolean currentDeliveryMode = new AtomicBoolean(false);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_order, container, false);
        cartManager = CartManager.getInstance();
        initView(view);
        return view;
    }

    private void initView(View view) {
        // 绑定分类控件 - 保持为TextView
        tvMilkTea = view.findViewById(R.id.tv_milk_tea);
        tvCoffee = view.findViewById(R.id.tv_coffee);
        tvDessert = view.findViewById(R.id.tv_dessert);
        lvGoods = view.findViewById(R.id.goods_listview);

        // 分类点击事件
        tvMilkTea.setOnClickListener(this);
        tvCoffee.setOnClickListener(this);
        tvDessert.setOnClickListener(this);

        // 初始加载奶茶分类
        currentGoodsList = GoodsData.getMilkTeaList();
        currentCategory = "milk_tea";
        initAdapter();

        // 堂食/配送按钮切换
        Button btnEatIn = view.findViewById(R.id.btn_eat_in);
        Button btnDelivery = view.findViewById(R.id.btn_delivery);

        // 从MainActivity获取初始配送模式
        if (getActivity() instanceof MainActivity) {
            boolean initialMode = ((MainActivity) getActivity()).getCurrentDeliveryMode();
            currentDeliveryMode.set(initialMode);
            updateDeliveryButtonState(btnEatIn, btnDelivery, initialMode);
        }

        btnEatIn.setOnClickListener(v -> {
            btnEatIn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            btnDelivery.setBackground(getResources().getDrawable(R.drawable.btn_bg1));
            currentDeliveryMode.set(false);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setCurrentDeliveryMode(false);
            }
            Toast.makeText(getContext(), "已选择堂食/自取", Toast.LENGTH_SHORT).show();
        });

        btnDelivery.setOnClickListener(v -> {
            btnDelivery.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            btnEatIn.setBackground(getResources().getDrawable(R.drawable.btn_bg1));
            currentDeliveryMode.set(true);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setCurrentDeliveryMode(true);
            }
            Toast.makeText(getContext(), "已选择配送", Toast.LENGTH_SHORT).show();
        });

        // 默认选中奶茶分类
        setSelectedCategory(tvMilkTea);
        setUnselectedCategory(tvCoffee, tvDessert);
    }

    private void updateDeliveryButtonState(Button btnEatIn, Button btnDelivery, boolean isDelivery) {
        if (isDelivery) {
            btnDelivery.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            btnEatIn.setBackground(getResources().getDrawable(R.drawable.btn_bg1));
        } else {
            btnEatIn.setBackground(getResources().getDrawable(R.drawable.btn_bg));
            btnDelivery.setBackground(getResources().getDrawable(R.drawable.btn_bg1));
        }
    }

    private void initAdapter() {
        goodsAdapter = new GoodsListAdapter(getContext(), currentGoodsList, goods -> {
            selectedGoods = goods; // 记录选中的商品

            // 根据当前分类决定是否显示口味选择
            if (currentCategory.equals("dessert")) {
                // 甜点类：直接加入购物车，使用默认口味
                addDessertToCart(goods);
            } else {
                // 饮品类（奶茶、咖啡）：显示口味选择弹窗
                showTasteDialog();
            }
        });
        lvGoods.setAdapter(goodsAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_milk_tea) {
            currentGoodsList = GoodsData.getMilkTeaList();
            currentCategory = "milk_tea";
            setSelectedCategory(tvMilkTea);
            setUnselectedCategory(tvCoffee, tvDessert);
        } else if (id == R.id.tv_coffee) {
            currentGoodsList = GoodsData.getCoffeeList();
            currentCategory = "coffee";
            setSelectedCategory(tvCoffee);
            setUnselectedCategory(tvMilkTea, tvDessert);
        } else if (id == R.id.tv_dessert) {
            currentGoodsList = GoodsData.getDessertList();
            currentCategory = "dessert";
            setSelectedCategory(tvDessert);
            setUnselectedCategory(tvMilkTea, tvCoffee);
        }
        initAdapter();
    }

    // 修改选中分类的样式
    private void setSelectedCategory(TextView selectedTv) {
        selectedTv.setBackgroundColor(getResources().getColor(android.R.color.white));
        selectedTv.setTextColor(getResources().getColor(android.R.color.black));
    }

    // 修改未选中分类的样式
    private void setUnselectedCategory(TextView... unselectedTvs) {
        for (TextView tv : unselectedTvs) {
            tv.setBackgroundColor(getResources().getColor(R.color.f5f5f5));
            tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    // 甜点类直接加入购物车
    private void addDessertToCart(Goods goods) {
        // 创建新的商品对象，甜点使用默认口味
        Goods goodsToAdd = new Goods(
                goods.getId(),
                goods.getName(),
                goods.getPrice(),
                goods.getImageResId(),
                "默认", // 甜点使用默认口味
                1
        );

        // 添加到购物车
        cartManager.addGoods(goodsToAdd);
        Toast.makeText(getContext(), goods.getName() + " 已加入购物车", Toast.LENGTH_SHORT).show();

        // 刷新商品列表和购物车
        goodsAdapter.notifyDataSetChanged();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).refreshCartFragment();
        }
    }

    // 口味选择弹窗（仅饮品使用）
    private void showTasteDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_taste_select, null);
        dialog.setContentView(dialogView);

        RadioGroup rgSweetness = dialogView.findViewById(R.id.rg_sweetness);
        RadioGroup rgIce = dialogView.findViewById(R.id.rg_ice);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        // 默认选中第一个选项
        if (rgSweetness.getChildCount() > 0) {
            ((RadioButton) rgSweetness.getChildAt(0)).setChecked(true);
        }
        if (rgIce.getChildCount() > 0) {
            ((RadioButton) rgIce.getChildAt(0)).setChecked(true);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            int sweetnessId = rgSweetness.getCheckedRadioButtonId();
            int iceId = rgIce.getCheckedRadioButtonId();
            if (sweetnessId == -1 || iceId == -1) {
                Toast.makeText(getContext(), "请选择甜度和冰度", Toast.LENGTH_SHORT).show();
                return;
            }

            String sweetness = ((RadioButton) dialogView.findViewById(sweetnessId)).getText().toString();
            String ice = ((RadioButton) dialogView.findViewById(iceId)).getText().toString();
            String taste = formatTaste(sweetness, ice);

            // 创建新的商品对象，确保口味信息完整
            Goods goodsToAdd = new Goods(
                    selectedGoods.getId(),
                    selectedGoods.getName(),
                    selectedGoods.getPrice(),
                    selectedGoods.getImageResId(),
                    taste,
                    1
            );

            // 添加到购物车
            cartManager.addGoods(goodsToAdd);
            Toast.makeText(getContext(), selectedGoods.getName() + "(" + taste + ") 已加入购物车", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            // 刷新商品列表和购物车
            goodsAdapter.notifyDataSetChanged();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshCartFragment();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // 统一格式化口味显示
    private String formatTaste(String sweetness, String ice) {
        // 甜度格式化
        switch (sweetness) {
            case "默认":
                sweetness = "全糖";
                break;
            case "三分线":
                sweetness = "三分糖";
                break;
            case "个式":
                sweetness = "全糖";
                break;
        }
        // 冰度格式化
        switch (ice) {
            case "默认":
                ice = "标准冰";
                break;
        }
        return sweetness + " + " + ice;
    }

    // 刷新商品列表状态
    public void refreshGoodsList() {
        if (goodsAdapter != null) {
            goodsAdapter.notifyDataSetChanged();
        }
    }

    // 获取当前配送模式的方法
    public boolean isDeliveryMode() {
        return currentDeliveryMode.get();
    }
}