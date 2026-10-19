package com.example.bigwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.bigwork.fragment.CartFragment;
import com.example.bigwork.fragment.GoodsFragment;
import com.example.bigwork.fragment.IndexFragment;
import com.example.bigwork.fragment.OrderDetailFragment;
import com.example.bigwork.fragment.PearsonFragment;
import com.example.bigwork.fragment.PayResultFragment;
import com.example.bigwork.fragment.SettleFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llIndex, llGoods, llCart, llPearson;
    public IndexFragment indexFragment;
    public GoodsFragment productFragment;
    public CartFragment cartFragment;
    public PearsonFragment pearsonFragment;
    public SettleFragment settleFragment;
    public PayResultFragment payResultFragment;
    public OrderDetailFragment orderDetailFragment;

    private OrderManager orderManager;
    private boolean currentDeliveryMode = true; // 默认配送模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 首先初始化 OrderManager
        orderManager = OrderManager.getInstance(this);
        SharedPreferences preferences = getSharedPreferences("user_info", 0);
        String username = preferences.getString("username", "");
        if (!username.isEmpty()) {
            orderManager.setCurrentUser(username);
        }

        System.out.println("MainActivity - OrderManager初始化完成，当前用户: " + username + ", 订单数量: " + orderManager.getOrderCount());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initBottomNav();
        initFragments();
        showFragment(indexFragment);
    }

    private void initBottomNav() {
        llIndex = findViewById(R.id.ll_index);
        llGoods = findViewById(R.id.ll_goods);
        llCart = findViewById(R.id.ll_cart);
        llPearson = findViewById(R.id.ll_pearson);

        llIndex.setOnClickListener(this);
        llGoods.setOnClickListener(this);
        llCart.setOnClickListener(this);
        llPearson.setOnClickListener(this);
    }

    private void initFragments() {
        indexFragment = new IndexFragment();
        productFragment = new GoodsFragment();
        cartFragment = new CartFragment();
        pearsonFragment = new PearsonFragment();
        settleFragment = new SettleFragment();
        payResultFragment = new PayResultFragment();
        orderDetailFragment = new OrderDetailFragment();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 清除所有回退栈，确保导航状态正确
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (id == R.id.ll_index) {
            showFragment(indexFragment);
        } else if (id == R.id.ll_goods) {
            showFragment(productFragment);
        } else if (id == R.id.ll_cart) {
            showFragment(cartFragment);
        } else if (id == R.id.ll_pearson) {
            showFragment(pearsonFragment);
        }
    }



    // 修改 showFragment 方法，确保正确隐藏其他Fragment
    public void showFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 先隐藏所有已添加的Fragment
        hideAllFragments(transaction);

        if (!targetFragment.isAdded()) {
            transaction.add(R.id.main_content, targetFragment);
        } else {
            transaction.show(targetFragment);
        }

        transaction.commit();

        // 如果是个人中心页面，刷新订单数据
        if (targetFragment instanceof PearsonFragment) {
            refreshPearsonFragment();
        }
    }

    // 确保 hideAllFragments 方法正确实现
    public void hideAllFragments(FragmentTransaction transaction) {
        if (indexFragment != null && indexFragment.isAdded()) {
            transaction.hide(indexFragment);
        }
        if (productFragment != null && productFragment.isAdded()) {
            transaction.hide(productFragment);
        }
        if (cartFragment != null && cartFragment.isAdded()) {
            transaction.hide(cartFragment);
        }
        if (pearsonFragment != null && pearsonFragment.isAdded()) {
            transaction.hide(pearsonFragment);
        }
        // 隐藏其他可能的Fragment（订单详情、结算、支付结果等）
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isAdded() &&
                    !(fragment instanceof IndexFragment) &&
                    !(fragment instanceof GoodsFragment) &&
                    !(fragment instanceof CartFragment) &&
                    !(fragment instanceof PearsonFragment)) {
                transaction.hide(fragment);
            }
        }
    }

    // 添加Fragment标签方法
    private String getFragmentTag(Fragment fragment) {
        if (fragment instanceof IndexFragment) return "index";
        if (fragment instanceof GoodsFragment) return "goods";
        if (fragment instanceof CartFragment) return "cart";
        if (fragment instanceof PearsonFragment) return "pearson";
        if (fragment instanceof SettleFragment) return "settle";
        if (fragment instanceof PayResultFragment) return "pay_result";
        if (fragment instanceof OrderDetailFragment) return "order_detail";
        return fragment.getClass().getSimpleName();
    }

    // 跳转到商品列表页
    public void jumpToGoodsFragment() {
        System.out.println("跳转到商品页面");
        showFragment(productFragment);
    }

    // 添加设置配送模式的方法
    public void setCurrentDeliveryMode(boolean isDelivery) {
        this.currentDeliveryMode = isDelivery;
        System.out.println("MainActivity - 设置配送模式: " + (isDelivery ? "配送" : "堂食/自取"));
    }

    // 添加获取配送模式的方法
    public boolean getCurrentDeliveryMode() {
        return currentDeliveryMode;
    }

    // 修改跳转到结算页的方法，传递配送模式
    public void switchToSettleFragment(boolean isDelivery) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 隐藏当前显示的Fragment
        hideAllFragments(transaction);

        SettleFragment settleFragment = new SettleFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_delivery", isDelivery);
        settleFragment.setArguments(bundle);

        transaction.add(R.id.main_content, settleFragment, "settle");
        transaction.addToBackStack("settle");
        transaction.commit();

        System.out.println("跳转到结算页面，配送模式: " + (isDelivery ? "配送" : "堂食/自取"));
    }

    // 修改跳转到结算页的方法
    public void switchToSettleFragment(SettleFragment settleFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 隐藏当前显示的Fragment
        if (indexFragment != null && indexFragment.isAdded()) {
            transaction.hide(indexFragment);
        }
        if (productFragment != null && productFragment.isAdded()) {
            transaction.hide(productFragment);
        }
        if (cartFragment != null && cartFragment.isAdded()) {
            transaction.hide(cartFragment);
        }
        if (pearsonFragment != null && pearsonFragment.isAdded()) {
            transaction.hide(pearsonFragment);
        }

        // 添加结算Fragment
        transaction.add(R.id.main_content, settleFragment);
        transaction.addToBackStack("settle");
        transaction.commit();

        System.out.println("跳转到结算页面");
    }

    // 跳转到支付结果页
    public void switchToPayResultFragment(PayResultFragment payResultFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 先移除结算页面（如果存在）
        Fragment settleFragment = getSupportFragmentManager().findFragmentByTag("settle");
        if (settleFragment != null) {
            transaction.remove(settleFragment);
        }

        // 添加支付结果Fragment
        transaction.add(R.id.main_content, payResultFragment, "pay_result");
        transaction.addToBackStack("pay_result");
        transaction.commit();

        System.out.println("跳转到支付结果页面");

        // 刷新个人中心数据，确保新订单能立即显示
        refreshPearsonFragment();
    }

    // 跳转到订单详情页
    public void switchToOrderDetailFragment(Order order) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 隐藏当前显示的Fragment
        hideAllFragments(transaction);

        OrderDetailFragment fragment = OrderDetailFragment.newInstance(order);
        transaction.add(R.id.main_content, fragment);
        transaction.addToBackStack("order_detail");
        transaction.commit();

        System.out.println("跳转到订单详情页面");
    }

    // 刷新购物车Fragment
    public void refreshCartFragment() {
        if (cartFragment != null && cartFragment.isAdded()) {
            cartFragment.refreshCartData();
        }
    }

    // 新增：刷新个人中心Fragment
    public void refreshPearsonFragment() {
        if (pearsonFragment != null && pearsonFragment.isAdded()) {
            // 调用 PearsonFragment 的刷新方法
            pearsonFragment.loadOrderData();
            System.out.println("MainActivity - 刷新个人中心订单数据");
        } else {
            System.out.println("MainActivity - 个人中心未加载，无法刷新");
        }
    }

    public void clearFragmentStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        initFragments();
        showFragment(indexFragment);
    }

    public void replaceFragment(Fragment fragment) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideAllFragments(transaction);
            transaction.replace(R.id.main_content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 切换到导航栏对应的Fragment
    public void switchToNavFragment(Fragment fragment) {
        showFragment(fragment);
    }

    // 获取 OrderManager 实例
    public OrderManager getOrderManager() {
        return orderManager;
    }

    // 添加返回键处理
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // 检查是否有Fragment在回退栈中
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // 有回退栈，执行正常的返回操作
            fragmentManager.popBackStack();
        } else {
            // 没有回退栈，检查当前显示的Fragment
            Fragment currentFragment = getCurrentVisibleFragment();
            if (currentFragment instanceof IndexFragment) {
                // 如果在首页，按返回键退出应用
                super.onBackPressed();
            } else {
                // 如果在其他页面，返回首页
                showFragment(indexFragment);
            }
        }
    }

    // 获取当前可见的Fragment
    private Fragment getCurrentVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }
}