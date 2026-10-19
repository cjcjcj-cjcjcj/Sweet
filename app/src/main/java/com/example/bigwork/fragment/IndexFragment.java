package com.example.bigwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bigwork.MainActivity;
import com.example.bigwork.R;

public class IndexFragment extends Fragment {
    private Button btn_pick, btn_delivery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_index, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        // 绑定按钮
        btn_pick = view.findViewById(R.id.btn_pick);
        btn_delivery = view.findViewById(R.id.btn_delivery);

        // 到店自取点击事件
        btn_pick.setOnClickListener(v -> {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "选择到店自取", Toast.LENGTH_SHORT).show();
                jumpToGoodsFragmentWithDeliveryMode(false);
            }
        });

        // 外送到家点击事件
        btn_delivery.setOnClickListener(v -> {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "选择外送到家", Toast.LENGTH_SHORT).show();
                jumpToGoodsFragmentWithDeliveryMode(true);
            }
        });
    }

    private void jumpToGoodsFragmentWithDeliveryMode(boolean isDelivery) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            // 设置配送模式
            activity.setCurrentDeliveryMode(isDelivery);
            // 跳转到商品页面
            activity.jumpToGoodsFragment();
        }
    }
}