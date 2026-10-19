package com.example.bigwork.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigwork.MainActivity;
import com.example.bigwork.Order;
import com.example.bigwork.OrderListAdapter;
import com.example.bigwork.OrderManager;
import com.example.bigwork.R;
import com.example.bigwork.LoginActivity; // 新增导入
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class PearsonFragment extends Fragment {
    private ListView lvOrders;
    private TextView tvName, tvAddress;
    private LinearLayout llEmpty;
    private Button btnEditAddress, btnSwitchAccount;
    private OrderListAdapter adapter;
    private OrderManager orderManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pearson, container, false);
        initView(view);
        loadUserInfo();
        loadOrderData();
        return view;
    }

    private void initView(View view) {
        lvOrders = view.findViewById(R.id.lv_orders);
        tvName = view.findViewById(R.id.tv_name);
        tvAddress = view.findViewById(R.id.tv_address);
        llEmpty = view.findViewById(R.id.ll_empty);
        btnEditAddress = view.findViewById(R.id.btn_edit_address);
        btnSwitchAccount = view.findViewById(R.id.btn_switch_account);

        orderManager = OrderManager.getInstance(getActivity());

        // 修改地址按钮点击事件
        btnEditAddress.setOnClickListener(v -> showEditAddressDialog());

        // 修改切换账号按钮点击事件 - 跳转到登录页面
        btnSwitchAccount.setOnClickListener(v -> showSwitchAccountDialog());

        // 在 PearsonFragment 的 initView 方法中修改订单列表点击事件
        lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order) parent.getItemAtPosition(position);
                if (getActivity() instanceof MainActivity) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.switchToOrderDetailFragment(order);
                }
            }
        });
    }

    private void loadUserInfo() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
        String username = preferences.getString("username", "用户");
        String address = preferences.getString("address", "暂无地址");

        tvName.setText(username);
        tvAddress.setText("地址：" + address);
    }

    // 显示修改地址对话框
    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改收货地址");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_address, null);
        TextInputEditText etNewAddress = dialogView.findViewById(R.id.et_new_address);

        // 设置当前地址
        SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
        String currentAddress = preferences.getString("address", "");
        etNewAddress.setText(currentAddress);

        builder.setView(dialogView);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String newAddress = etNewAddress.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                // 保存新地址
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("address", newAddress);
                editor.apply();

                // 更新显示
                tvAddress.setText("地址：" + newAddress);
                Toast.makeText(getContext(), "地址修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "地址不能为空", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 修改切换账号对话框 - 跳转到登录页面
    private void showSwitchAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("切换账号");
        builder.setMessage("确定要切换账号吗？当前订单数据将会清空。");
        builder.setPositiveButton("确定", (dialog, which) -> {
            // 清空当前用户的订单数据
            orderManager.clearAllOrders();
            // 清除登录状态
            SharedPreferences preferences = getActivity().getSharedPreferences("user_info", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            // 跳转到登录页面
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // 结束当前Activity
            if (getActivity() != null) {
                getActivity().finish();
            }

            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 修改为公共方法，可以从外部调用
    public void loadOrderData() {
        if (orderManager == null) {
            orderManager = OrderManager.getInstance(getActivity());
        }

        List<Order> orderList = orderManager.getOrderList();
        System.out.println("PearsonFragment - 加载订单数据，数量: " + orderList.size());

        // 根据订单数量显示/隐藏空状态
        if (orderList.isEmpty()) {
            lvOrders.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
            System.out.println("显示空状态，无订单数据");
        } else {
            lvOrders.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            System.out.println("显示订单列表，数量: " + orderList.size());
        }

        // 更新适配器数据
        if (adapter == null) {
            adapter = new OrderListAdapter(getContext(), orderList);
            lvOrders.setAdapter(adapter);
        } else {
            adapter = new OrderListAdapter(getContext(), orderList);
            lvOrders.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("PearsonFragment onResume - 重新加载订单数据");
        loadOrderData();
    }
}