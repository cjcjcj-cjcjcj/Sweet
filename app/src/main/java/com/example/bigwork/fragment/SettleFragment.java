package com.example.bigwork.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigwork.CartManager;
import com.example.bigwork.Goods;
import com.example.bigwork.MainActivity;
import com.example.bigwork.Order;
import com.example.bigwork.OrderManager;
import com.example.bigwork.R;
import com.example.bigwork.SettleAdapter;

import java.util.List;

public class SettleFragment extends Fragment {
    private TextView tvGoodsCount, tvGoodsAmount, tvServiceFee, tvSubtotal, tvActualPay;
    private TextView tvAddress;
    private LinearLayout llAddress;
    private Button btnPay;
    private ListView lvSettleGoods;
    private List<Goods> goodsList;
    private double serviceFee = 3.00; // 默认服务费
    private OrderManager orderManager;
    private boolean isDelivery = true; // 默认是配送

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settle, container, false);
        orderManager = OrderManager.getInstance(getActivity());
        initView(view);
        return view;
    }

    private void initView(View view) {
        // 绑定控件
        tvGoodsCount = view.findViewById(R.id.tv_goods_count);
        tvGoodsAmount = view.findViewById(R.id.tv_goods_amount);
        tvServiceFee = view.findViewById(R.id.tv_service_fee);
        tvSubtotal = view.findViewById(R.id.tv_subtotal);
        tvActualPay = view.findViewById(R.id.tv_actual_pay);
        tvAddress = view.findViewById(R.id.tv_address);
        llAddress = view.findViewById(R.id.ll_address);
        btnPay = view.findViewById(R.id.btn_pay);
        lvSettleGoods = view.findViewById(R.id.lv_settle_goods);

        // 获取配送方式（从参数传递过来）
        Bundle bundle = getArguments();
        if (bundle != null) {
            isDelivery = bundle.getBoolean("is_delivery", true);
        }

        // 根据配送方式显示/隐藏地址并设置服务费
        if (isDelivery) {
            // 配送：显示地址，服务费3元
            llAddress.setVisibility(View.VISIBLE);
            serviceFee = 3.00;
            loadUserAddress();

            // 地址点击事件
            llAddress.setOnClickListener(v -> {
                Toast.makeText(getContext(), "如需修改地址，请到个人中心修改", Toast.LENGTH_SHORT).show();
            });
        } else {
            // 堂食/自取：隐藏地址，服务费0元
            llAddress.setVisibility(View.GONE);
            serviceFee = 0.00;
        }

        // 刷新数据
        refreshData();

        // 支付按钮点击事件
        btnPay.setOnClickListener(v -> {
            if (goodsList == null || goodsList.isEmpty()) {
                Toast.makeText(getContext(), "购物车为空，无法支付", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查当前用户
            if (orderManager == null) {
                orderManager = OrderManager.getInstance(getActivity());
            }

            SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
            String username = preferences.getString("username", "");
            if (username.isEmpty()) {
                Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }

            // 设置当前用户
            orderManager.setCurrentUser(username);

            // 创建订单（传递配送方式）
            Order order = orderManager.createOrder(goodsList, serviceFee, isDelivery);
            // 清空购物车
            CartManager.getInstance().clearCart();

            // 刷新购物车数据
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.refreshCartFragment();

                // 立即刷新个人中心数据
                activity.refreshPearsonFragment();
            }

            // 跳转到支付成功页面
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                PayResultFragment payResultFragment = new PayResultFragment();
                payResultFragment.setOrder(order);
                activity.switchToPayResultFragment(payResultFragment);
            }

            String orderType = isDelivery ? "配送" : "堂食";
            Toast.makeText(getContext(), orderType + "订单支付成功！订单号: " + order.getOrderId(), Toast.LENGTH_SHORT).show();

            // 调试信息
            System.out.println("支付完成，订单类型: " + orderType + ", 当前订单总数: " + orderManager.getOrderCount());
        });

        // 修改返回按钮逻辑 - 直接返回到购物车页面
        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    MainActivity activity = (MainActivity) getActivity();
                    // 直接返回到购物车页面，不经过回退栈
                    activity.showFragment(activity.cartFragment);
                }
            });
        }
    }

    // 加载用户地址
    private void loadUserAddress() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
        String address = preferences.getString("address", "暂无地址");
        tvAddress.setText(address);

        System.out.println("结算页面 - 加载用户地址: " + address);
    }

    // 刷新结算页面数据
    private void refreshData() {
        // 获取购物车商品列表
        goodsList = CartManager.getInstance().getCartList();
        if (goodsList == null || goodsList.isEmpty()) {
            // 购物车为空时的处理
            tvGoodsCount.setText("0件商品");
            tvGoodsAmount.setText("¥0.00");
            tvServiceFee.setText("¥" + String.format("%.2f", serviceFee));
            tvSubtotal.setText("¥0.00");
            tvActualPay.setText("¥0.00");
            btnPay.setEnabled(false);
            btnPay.setBackgroundResource(R.drawable.btn_gray_bg);
            return;
        }

        // 计算总金额
        double goodsAmount = CartManager.getInstance().getTotalAmount();
        double subtotal = goodsAmount + serviceFee;
        int totalCount = CartManager.getInstance().getTotalCount();

        // 设置文本内容
        tvGoodsCount.setText(totalCount + "件商品");
        tvGoodsAmount.setText("¥" + String.format("%.2f", goodsAmount));
        tvServiceFee.setText("¥" + String.format("%.2f", serviceFee));
        tvSubtotal.setText("¥" + String.format("%.2f", subtotal));
        tvActualPay.setText("¥" + String.format("%.2f", subtotal)); // 实付金额 = 小计

        // 设置商品列表适配器
        SettleAdapter settleAdapter = new SettleAdapter(getContext(), goodsList);
        lvSettleGoods.setAdapter(settleAdapter);

        // 启用支付按钮
        btnPay.setEnabled(true);
        btnPay.setBackgroundResource(R.drawable.btn_red_bg);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 页面重新显示时刷新数据和地址
        if (isDelivery) {
            loadUserAddress();
        }
        refreshData();
    }
}