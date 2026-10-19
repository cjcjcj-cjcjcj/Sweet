package com.example.bigwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.bigwork.MainActivity;
import com.example.bigwork.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;

    public OrderListAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    // 关键修改：返回订单唯一ID的哈希值，确保每个条目ID不重复
    @Override
    public long getItemId(int position) {
        Order order = orderList.get(position);
        return order.getOrderId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
            holder = new ViewHolder();
            holder.tvOrderId = convertView.findViewById(R.id.tv_order_id);
            holder.tvDeliveryType = convertView.findViewById(R.id.tv_delivery_type);
            holder.tvOrderTime = convertView.findViewById(R.id.tv_order_time);
            holder.tvOrderStatus = convertView.findViewById(R.id.tv_order_status);
            holder.tvOrderAmount = convertView.findViewById(R.id.tv_order_amount);
            holder.btnViewDetail = convertView.findViewById(R.id.btn_view_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = orderList.get(position);
        // 格式化订单时间（确保每个订单时间不同）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String timeStr = sdf.format(new Date(order.getCreateTime().getTime()));

        // 设置配送方式提示
        String deliveryType = order.isDelivery() ? "配送" : "堂食/自取";

        // 设置订单信息（编号、金额与实际一致）
        holder.tvOrderId.setText("订单编号：" + order.getOrderId());
        holder.tvDeliveryType.setText(deliveryType);
        holder.tvOrderTime.setText("下单时间：" + timeStr);
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderAmount.setText("实付金额：¥" + String.format("%.2f", order.getActualPay()));

        // 点击"查看详情"或整个订单条目，跳转到详情页
        View.OnClickListener detailListener = v -> {
            if (context instanceof MainActivity) {
                // 使用新的方法跳转到订单详情页
                ((MainActivity) context).switchToOrderDetailFragment(order);
            }
        };
        convertView.setOnClickListener(detailListener); // 点击整个条目跳转
        holder.btnViewDetail.setOnClickListener(detailListener); // 点击按钮跳转

        return convertView;
    }

    static class ViewHolder {
        TextView tvOrderId, tvDeliveryType, tvOrderTime, tvOrderStatus, tvOrderAmount;
        Button btnViewDetail;
    }
}