package com.example.bigwork;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Goods> cartList = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            synchronized (CartManager.class) {
                instance = new CartManager();
            }
        }
        return instance;
    }

    // 添加到购物车 - 完全重写逻辑
    public void addGoods(Goods newGoods) {
        System.out.println("=== 添加到购物车 ===");
        System.out.println("新商品: " + newGoods.getName() + ", 口味: " + newGoods.getTaste());
        // 确保新商品数量为1
        newGoods.setCount(1);
        // 查找是否已有完全相同的商品（ID和口味都相同）
        for (Goods existingGoods : cartList) {
            if (isSameGoods(existingGoods, newGoods)) {
                // 找到相同商品，只增加数量
                existingGoods.setCount(existingGoods.getCount() + 1);
                System.out.println("增加现有商品数量: " + existingGoods.getName() + " - " + existingGoods.getTaste() + " 数量: " + existingGoods.getCount());
                printCartContents();
                return;
            }
        }
        // 没有找到相同商品，添加新商品
        Goods goodsToAdd = new Goods(
                newGoods.getId(),
                newGoods.getName(),
                newGoods.getPrice(),
                newGoods.getImageResId(),
                newGoods.getTaste(),
                1
        );
        cartList.add(goodsToAdd);
        System.out.println("添加新商品: " + goodsToAdd.getName() + " - " + goodsToAdd.getTaste());
        printCartContents();
    }

    // 从购物车移除一个商品（数量减1）
    public void removeOneGoods(Goods goodsToRemove) {
        System.out.println("=== 移除一个商品 ===");
        System.out.println("要移除: " + goodsToRemove.getName() + " - " + goodsToRemove.getTaste());
        for (int i = 0; i < cartList.size(); i++) {
            Goods existingGoods = cartList.get(i);
            if (isSameGoods(existingGoods, goodsToRemove)) {
                if (existingGoods.getCount() > 1) {
                    existingGoods.setCount(existingGoods.getCount() - 1);
                    System.out.println("减少数量: " + existingGoods.getName() + " 新数量: " + existingGoods.getCount());
                } else {
                    cartList.remove(i);
                    System.out.println("完全移除商品");
                }
                printCartContents();
                return;
            }
        }
        System.out.println("未找到要移除的商品");
    }

    // 从购物车移除商品（完全移除）
    public void removeGoods(Goods goodsToRemove) {
        System.out.println("=== 完全移除商品 ===");
        System.out.println("要移除: " + goodsToRemove.getName() + " - " + goodsToRemove.getTaste());
        for (int i = 0; i < cartList.size(); i++) {
            Goods existingGoods = cartList.get(i);
            if (isSameGoods(existingGoods, goodsToRemove)) {
                cartList.remove(i);
                System.out.println("商品已完全移除");
                printCartContents();
                return;
            }
        }
        System.out.println("未找到要移除的商品");
    }

    // 新增：根据商品ID移除所有相关商品（所有口味）
    public void removeAllGoodsById(int goodsId) {
        System.out.println("=== 移除ID为 " + goodsId + " 的所有商品 ===");
        List<Goods> toRemove = new ArrayList<>();
        for (Goods goods : cartList) {
            if (goods.getId() == goodsId) {
                toRemove.add(goods);
            }
        }
        cartList.removeAll(toRemove);
        System.out.println("已移除 " + toRemove.size() + " 个相关商品");
        printCartContents();
    }

    // 检查是否相同商品（ID和口味都相同）
    private boolean isSameGoods(Goods goods1, Goods goods2) {
        if (goods1.getId() != goods2.getId()) {
            return false;
        }
        String taste1 = goods1.getTaste() != null ? goods1.getTaste() : "全糖 + 标准冰";
        String taste2 = goods2.getTaste() != null ? goods2.getTaste() : "全糖 + 标准冰";
        return taste1.equals(taste2);
    }

    // 检查商品是否在购物车中
    public boolean hasGoodsInCart(Goods goods) {
        for (Goods existingGoods : cartList) {
            if (isSameGoods(existingGoods, goods)) {
                return true;
            }
        }
        return false;
    }

    // 获取购物车中某个商品的数量
    public int getGoodsCountInCart(Goods goods) {
        for (Goods existingGoods : cartList) {
            if (isSameGoods(existingGoods, goods)) {
                return existingGoods.getCount();
            }
        }
        return 0;
    }

    // 获取购物车列表
    public List<Goods> getCartList() {
        return new ArrayList<>(cartList);
    }

    // 清空购物车
    public void clearCart() {
        System.out.println("=== 清空购物车 ===");
        cartList.clear();
        System.out.println("购物车已清空");
    }

    // 获取购物车商品总数
    public int getTotalCount() {
        int total = 0;
        for (Goods goods : cartList) {
            total += goods.getCount();
        }
        System.out.println("总商品数量: " + total);
        return total;
    }

    // 获取购物车总金额
    public double getTotalAmount() {
        double total = 0;
        for (Goods goods : cartList) {
            total += goods.getPrice() * goods.getCount();
        }
        System.out.println("总金额: " + total);
        return total;
    }

    // 调试方法：打印购物车内容
    public void printCartContents() {
        System.out.println("=== 当前购物车内容 ===");
        if (cartList.isEmpty()) {
            System.out.println("购物车为空");
        } else {
            for (int i = 0; i < cartList.size(); i++) {
                Goods goods = cartList.get(i);
                System.out.println(i + ". " + goods.getName() +
                        " | 口味:" + goods.getTaste() +
                        " | 数量:" + goods.getCount() +
                        " | 单价:" + goods.getPrice());
            }
        }
        System.out.println("总数量:" + getTotalCount() + ", 总金额:" + getTotalAmount());
        System.out.println("=================");
    }
}