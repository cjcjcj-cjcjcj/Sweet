package com.example.bigwork;

import java.io.Serializable;

public class Goods implements Serializable {
    private int id;
    private String name;
    private double price;
    private int imageResId;
    private String taste;
    private int count;

    public Goods() {
    }

    public Goods(int id, String name, double price, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.taste = "全糖 + 标准冰";
        this.count = 1;
    }

    public Goods(int id, String name, double price, int imageResId, String taste) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.taste = taste != null ? taste : "全糖 + 标准冰";
        this.count = 1;
    }

    public Goods(int id, String name, double price, int imageResId, String taste, int count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.taste = taste != null ? taste : "全糖 + 标准冰";
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Goods goods = (Goods) obj;
        if (id != goods.id) return false;
        String thisTaste = this.taste != null ? this.taste : "全糖 + 标准冰";
        String otherTaste = goods.taste != null ? goods.taste : "全糖 + 标准冰";
        return thisTaste.equals(otherTaste);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (taste != null ? taste.hashCode() : 0);
        return result;
    }

    // getter 和 setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public String getTaste() { return taste != null ? taste : "全糖 + 标准冰"; }
    public void setTaste(String taste) { this.taste = taste; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}