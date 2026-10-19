package com.example.bigwork;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private String orderId;        // 订单ID
    private Date createTime;       // 下单时间
    private List<Goods> goodsList; // 订单商品列表
    private double goodsAmount;    // 商品金额
    private double serviceFee;     // 服务费
    private double actualPay;      // 实付金额
    private String status;         // 订单状态（如"已支付"）
    private boolean isEvaluated;   // 是否已评价
    private boolean isDelivery;    // 是否为配送订单
    private String address;        // 新增：下单时的地址

    // 构造方法
    public Order(String orderId, Date createTime, List<Goods> goodsList,
                 double goodsAmount, double serviceFee, double actualPay,
                 boolean isDelivery, String address) {  // 新增address参数
        this.orderId = orderId;
        this.createTime = createTime;
        this.goodsList = goodsList;
        this.goodsAmount = goodsAmount;
        this.serviceFee = serviceFee;
        this.actualPay = actualPay;
        this.status = "已支付";
        this.isEvaluated = false;
        this.isDelivery = isDelivery;
        this.address = address;  // 保存下单时的地址
    }

    // Getter/Setter 方法
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public List<Goods> getGoodsList() { return goodsList; }
    public void setGoodsList(List<Goods> goodsList) { this.goodsList = goodsList; }
    public double getGoodsAmount() { return goodsAmount; }
    public void setGoodsAmount(double goodsAmount) { this.goodsAmount = goodsAmount; }
    public double getServiceFee() { return serviceFee; }
    public void setServiceFee(double serviceFee) { this.serviceFee = serviceFee; }
    public double getActualPay() { return actualPay; }
    public void setActualPay(double actualPay) { this.actualPay = actualPay; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isEvaluated() { return isEvaluated; }
    public void setEvaluated(boolean evaluated) { isEvaluated = evaluated; }
    public boolean isDelivery() { return isDelivery; }
    public void setDelivery(boolean delivery) { isDelivery = delivery; }
    public String getAddress() { return address; }  // 新增getter
    public void setAddress(String address) { this.address = address; }  // 新增setter
}