package com.example.bigwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigwork.Goods;
import com.example.bigwork.MainActivity;
import com.example.bigwork.Order;
import com.example.bigwork.OrderDetailGoodsAdapter;
import com.example.bigwork.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {
    private TextView tvOrderId, tvOrderTime, tvOrderStatus, tvGoodsAmount, tvServiceFee, tvActualPay;
    private TextView tvAddress, tvEatInTip;
    private LinearLayout llAddress, llEatInTip;
    private ListView lvGoods;
    private Button btnBack;
    private Order order;

    public static OrderDetailFragment newInstance(Order order) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            order = (Order) bundle.getSerializable("order");
        }
    }

    private void initView(View view) {
        tvOrderId = view.findViewById(R.id.tv_detail_order_id);
        tvOrderTime = view.findViewById(R.id.tv_detail_order_time);
        tvOrderStatus = view.findViewById(R.id.tv_detail_order_status);
        tvGoodsAmount = view.findViewById(R.id.tv_detail_goods_amount);
        tvServiceFee = view.findViewById(R.id.tv_detail_service_fee);
        tvActualPay = view.findViewById(R.id.tv_detail_actual_pay);
        tvAddress = view.findViewById(R.id.tv_detail_address);
        tvEatInTip = view.findViewById(R.id.tv_eat_in_tip);
        llAddress = view.findViewById(R.id.ll_detail_address);
        llEatInTip = view.findViewById(R.id.ll_eat_in_tip);
        lvGoods = view.findViewById(R.id.lv_detail_goods);
        btnBack = view.findViewById(R.id.btn_back);

        // 修改返回按钮点击事件 - 直接返回到个人中心
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                // 直接显示个人中心页面
                activity.showFragment(activity.pearsonFragment);
            }
        });

        if (order != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            String timeStr = sdf.format(new Date(order.getCreateTime().getTime()));

            tvOrderId.setText("订单编号：" + order.getOrderId());
            tvOrderTime.setText("下单时间：" + timeStr);
            tvOrderStatus.setText("订单状态：" + order.getStatus());
            tvGoodsAmount.setText("¥" + String.format("%.2f", order.getGoodsAmount()));
            tvServiceFee.setText("¥" + String.format("%.2f", order.getServiceFee()));
            tvActualPay.setText("¥" + String.format("%.2f", order.getActualPay()));

            // 根据配送方式显示不同的信息
            if (order.isDelivery()) {
                // 配送订单：显示地址，隐藏堂食提示
                llAddress.setVisibility(View.VISIBLE);
                llEatInTip.setVisibility(View.GONE);

                // 使用订单创建时保存的地址，而不是当前地址
                String orderAddress = order.getAddress();
                tvAddress.setText(orderAddress != null ? orderAddress : "暂无地址");

                System.out.println("订单详情页面 - 显示订单创建时的地址: " + orderAddress);
            } else {
                // 堂食/自取订单：隐藏地址，显示堂食提示
                llAddress.setVisibility(View.GONE);
                llEatInTip.setVisibility(View.VISIBLE);
                tvEatInTip.setText("堂食/自取");
            }

            List<Goods> goodsList = order.getGoodsList();
            OrderDetailGoodsAdapter adapter = new OrderDetailGoodsAdapter(getContext(), goodsList);
            lvGoods.setAdapter(adapter);
        }
    }
}