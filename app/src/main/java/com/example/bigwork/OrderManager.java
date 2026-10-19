package com.example.bigwork;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderManager {
    private static OrderManager instance;
    private Map<String, List<Order>> userOrdersMap = new HashMap<>(); // 按用户名存储订单
    private Context context;
    private static final String ORDER_PREFS = "order_prefs";
    private static final String ORDER_MAP_KEY = "order_map";
    private String currentUser;

    private OrderManager(Context context) {
        this.context = context;
        loadAllOrders();
    }

    public static OrderManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OrderManager.class) {
                if (instance == null) {
                    instance = new OrderManager(context);
                }
            }
        }
        return instance;
    }

    // 设置当前用户
    public void setCurrentUser(String username) {
        this.currentUser = username;
        // 如果该用户没有订单列表，初始化一个空列表
        if (!userOrdersMap.containsKey(username)) {
            userOrdersMap.put(username, new ArrayList<>());
        }
    }

    // 创建订单 - 修改方法签名，添加isDelivery参数和获取当前地址
    public Order createOrder(List<Goods> goodsList, double serviceFee, boolean isDelivery) {
        if (currentUser == null) {
            System.out.println("错误：未设置当前用户");
            return null;
        }

        System.out.println("=== 开始创建订单 ===");
        System.out.println("用户: " + currentUser + ", 商品数量: " + goodsList.size() + ", 配送方式: " + (isDelivery ? "配送" : "堂食/自取"));

        double goodsAmount = 0;
        for (Goods goods : goodsList) {
            goodsAmount += goods.getPrice() * goods.getCount();
        }

        double actualPay = goodsAmount + serviceFee;
        String orderId = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());

        // 获取下单时的地址
        SharedPreferences preferences = context.getSharedPreferences("user_info", 0);
        String currentAddress = preferences.getString("address", "暂无地址");
        System.out.println("创建订单时保存的地址: " + currentAddress);

        // 创建新的商品列表副本
        List<Goods> orderGoodsList = new ArrayList<>();
        for (Goods goods : goodsList) {
            Goods newGoods = new Goods(
                    goods.getId(),
                    goods.getName(),
                    goods.getPrice(),
                    goods.getImageResId(),
                    goods.getTaste(),
                    goods.getCount()
            );
            orderGoodsList.add(newGoods);
        }

        // 创建订单，传递配送方式和当前地址
        Order order = new Order(orderId, new Date(), orderGoodsList, goodsAmount, serviceFee, actualPay, isDelivery, currentAddress);

        // 获取当前用户的订单列表
        List<Order> userOrders = userOrdersMap.get(currentUser);
        if (userOrders == null) {
            userOrders = new ArrayList<>();
            userOrdersMap.put(currentUser, userOrders);
        }

        // 添加到用户订单列表的开头
        userOrders.add(0, order);

        // 保存到持久化存储
        boolean saveSuccess = saveAllOrders();
        System.out.println("订单保存结果: " + (saveSuccess ? "成功" : "失败"));
        System.out.println("用户 " + currentUser + " 的订单总数: " + userOrders.size());
        System.out.println("=== 订单创建完成 ===");

        return order;
    }

    // 获取当前用户的所有订单
    public List<Order> getOrderList() {
        if (currentUser == null) {
            System.out.println("错误：未设置当前用户");
            return new ArrayList<>();
        }

        List<Order> userOrders = userOrdersMap.get(currentUser);
        if (userOrders == null) {
            userOrders = new ArrayList<>();
            userOrdersMap.put(currentUser, userOrders);
        }

        System.out.println("获取用户 " + currentUser + " 的订单列表，数量: " + userOrders.size());
        return new ArrayList<>(userOrders);
    }

    // 清空当前用户的所有订单
    public void clearCurrentUserOrders() {
        if (currentUser != null && userOrdersMap.containsKey(currentUser)) {
            userOrdersMap.get(currentUser).clear();
            saveAllOrders();
            System.out.println("已清空用户 " + currentUser + " 的所有订单");
        }
    }

    // 清空所有用户的订单（用于切换账号时）
    public void clearAllOrders() {
        userOrdersMap.clear();
        saveAllOrders();
        System.out.println("已清空所有用户的订单数据");
    }

    // 保存所有用户的订单数据
    private boolean saveAllOrders() {
        try {
            SharedPreferences preferences = context.getSharedPreferences(ORDER_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(userOrdersMap);
            editor.putString(ORDER_MAP_KEY, json);
            boolean result = editor.commit();

            System.out.println("所有用户订单数据保存" + (result ? "成功" : "失败"));
            return result;
        } catch (Exception e) {
            System.out.println("保存订单数据失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 加载所有用户的订单数据
    private void loadAllOrders() {
        try {
            SharedPreferences preferences = context.getSharedPreferences(ORDER_PREFS, Context.MODE_PRIVATE);
            String json = preferences.getString(ORDER_MAP_KEY, null);

            if (json != null && !json.isEmpty()) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, List<Order>>>() {}.getType();
                Map<String, List<Order>> loadedMap = gson.fromJson(json, type);

                if (loadedMap != null) {
                    userOrdersMap = loadedMap;
                    System.out.println("订单数据加载成功，用户数量: " + userOrdersMap.size());
                } else {
                    userOrdersMap = new HashMap<>();
                    System.out.println("加载的订单数据为空");
                }
            } else {
                userOrdersMap = new HashMap<>();
                System.out.println("没有找到保存的订单数据");
            }
        } catch (Exception e) {
            System.out.println("加载订单数据失败: " + e.getMessage());
            e.printStackTrace();
            userOrdersMap = new HashMap<>();
        }
    }

    // 获取当前用户的订单数量
    public int getOrderCount() {
        if (currentUser == null) return 0;
        List<Order> userOrders = userOrdersMap.get(currentUser);
        return userOrders != null ? userOrders.size() : 0;
    }
}