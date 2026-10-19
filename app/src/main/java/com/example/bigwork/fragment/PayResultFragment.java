package com.example.bigwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bigwork.MainActivity;
import com.example.bigwork.Order;
import com.example.bigwork.R;

public class PayResultFragment extends Fragment implements View.OnClickListener {
    private Button btnViewOrder, btnContinue;
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_result, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        btnViewOrder = view.findViewById(R.id.btn_view_order);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnViewOrder.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            if (v.getId() == R.id.btn_view_order) {
                // 点击查看订单：跳转到订单详情页
                if (order != null) {
                    // 清除支付相关的回退栈
                    activity.getSupportFragmentManager().popBackStack("settle", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    activity.getSupportFragmentManager().popBackStack("pay_result", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    // 跳转到订单详情
                    activity.switchToOrderDetailFragment(order);
                }
            } else if (v.getId() == R.id.btn_continue) {
                // 点击继续购物：回到商品页面，并清除所有回退栈
                activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                activity.showFragment(activity.productFragment);

                // 确保购物车已刷新
                activity.refreshCartFragment();

                // 刷新个人中心订单数据
                activity.refreshPearsonFragment();
            }
        }
    }
}