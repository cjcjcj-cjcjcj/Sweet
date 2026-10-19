package com.example.bigwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigwork.CartAdapter;
import com.example.bigwork.CartManager;
import com.example.bigwork.Goods;
import com.example.bigwork.MainActivity;
import com.example.bigwork.R;

import java.util.List;

public class CartFragment extends Fragment {
    private ListView lvCart;
    private TextView tvTotalPrice;
    private Button btnSettle;
    private CartAdapter cartAdapter;
    private List<Goods> cartList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        lvCart = view.findViewById(R.id.lv_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnSettle = view.findViewById(R.id.btn_settle);
        refreshCartData();

        // 在 CartFragment 的 initView 方法中修改结算按钮的点击事件
        btnSettle.setOnClickListener(v -> {
            if (cartList == null || cartList.isEmpty()) {
                Toast.makeText(getContext(), "购物车为空，无法结算", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                // 使用MainActivity中设置的配送模式
                boolean isDelivery = activity.getCurrentDeliveryMode();

                // 创建结算Fragment并传递配送方式
                SettleFragment settleFragment = new SettleFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("is_delivery", isDelivery);
                settleFragment.setArguments(bundle);

                // 使用新的跳转方法，避免回退栈问题
                activity.switchToSettleFragment(settleFragment);
            }
        });
    }

    public void refreshCartData() {
        System.out.println("CartFragment - refreshCartData 开始");
        cartList = CartManager.getInstance().getCartList();
        System.out.println("CartFragment - 购物车商品数量: " + cartList.size());
        CartManager.getInstance().printCartContents();

        if (cartAdapter == null) {
            System.out.println("CartFragment - 创建新的适配器");
            cartAdapter = new CartAdapter(getContext(), cartList, new CartAdapter.OnCartChangeListener() {
                @Override
                public void onChange() {
                    System.out.println("CartAdapter - 数据变化，重新刷新");
                    refreshCartData();
                }
            });
            lvCart.setAdapter(cartAdapter);
        } else {
            System.out.println("CartFragment - 更新现有适配器数据");
            cartAdapter.updateData(cartList);
            cartAdapter.notifyDataSetChanged();
        }

        updateTotalPrice();
        System.out.println("CartFragment - refreshCartData 完成");
    }

    private void updateTotalPrice() {
        if (cartList == null || cartList.isEmpty()) {
            tvTotalPrice.setText("合计：¥0.00");
            btnSettle.setEnabled(false);
            btnSettle.setBackgroundResource(R.drawable.btn_gray_bg);
            btnSettle.setText("结算");
            return;
        }

        double total = CartManager.getInstance().getTotalAmount();
        tvTotalPrice.setText("合计：¥" + String.format("%.2f", total));
        btnSettle.setEnabled(true);
        btnSettle.setBackgroundResource(R.drawable.btn_red_bg);
        btnSettle.setText("结算(" + cartList.size() + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("CartFragment onResume - 重新刷新数据");
        refreshCartData();
    }
}