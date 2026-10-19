package com.example.bigwork;

import java.util.ArrayList;
import java.util.List;

public class GoodsData {
    // 奶茶分类的商品列表
    public static List<Goods> getMilkTeaList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(1, "茉韵冷萃", 15.0, R.drawable.drink1));
        list.add(new Goods(2, "红豆抹茶", 18.0, R.drawable.drink2));
        list.add(new Goods(10, "珍珠奶茶", 16.0, R.drawable.drink3));
        list.add(new Goods(11, "芋圆波波", 20.0, R.drawable.drink4));
        list.add(new Goods(12, "四季春茶", 14.0, R.drawable.drink5));
        list.add(new Goods(13, "黑糖鲜奶", 22.0, R.drawable.drink6));
        list.add(new Goods(14, "芝士奶盖", 19.0, R.drawable.drink7));
        list.add(new Goods(15, "水果茶", 18.0, R.drawable.drink8));
        return list;
    }

    // 咖啡分类的商品列表
    public static List<Goods> getCoffeeList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(3, "云顶奶咖", 20.0, R.drawable.coffee1));
        list.add(new Goods(4, "卡布奇诺", 25.0, R.drawable.coffee2));
        list.add(new Goods(5, "金桂浮香", 23.0, R.drawable.coffee3));
        list.add(new Goods(16, "美式咖啡", 18.0, R.drawable.coffee4));
        list.add(new Goods(17, "拿铁咖啡", 22.0, R.drawable.coffee5));
        list.add(new Goods(18, "摩卡咖啡", 26.0, R.drawable.coffee6));
        list.add(new Goods(19, "焦糖玛奇朵", 28.0, R.drawable.coffee7));
        list.add(new Goods(20, "香草拿铁", 24.0, R.drawable.coffee8));
        return list;
    }

    // 甜点分类的商品列表
    public static List<Goods> getDessertList() {
        List<Goods> list = new ArrayList<>();
        list.add(new Goods(6, "草莓蛋糕", 30.0, R.drawable.cake1));
        list.add(new Goods(7, "抹茶慕斯", 30.0, R.drawable.cake2));
        list.add(new Goods(8, "提拉米苏", 32.0, R.drawable.cake3));
        list.add(new Goods(9, "布丁蛋糕", 28.0, R.drawable.cake4));
        list.add(new Goods(21, "巧克力蛋糕", 35.0, R.drawable.cake5));
        list.add(new Goods(22, "芝士蛋糕", 33.0, R.drawable.cake6));
        list.add(new Goods(23, "马卡龙", 25.0, R.drawable.cake7));
        list.add(new Goods(24, "泡芙", 20.0, R.drawable.cake8));
        list.add(new Goods(25, "曲奇饼干", 18.0, R.drawable.cake9));
        list.add(new Goods(26, "甜甜圈", 22.0, R.drawable.cake10));
        return list;
    }
}