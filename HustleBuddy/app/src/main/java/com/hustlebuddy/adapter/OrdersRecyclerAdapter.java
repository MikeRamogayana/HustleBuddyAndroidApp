package com.hustlebuddy.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hustlebuddy.R;
import com.hustlebuddy.ViewOrderActivity;
import com.hustlebuddy.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public OrdersRecyclerAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_card, null);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(orderList.get(position).getStatus().equalsIgnoreCase("cancelled")) {
            holder.card_order.setCardBackgroundColor(Color.parseColor("#99FF3000"));
        } else if(orderList.get(position).getStatus().equalsIgnoreCase("completed")) {
            holder.card_order.setCardBackgroundColor(Color.parseColor("#9987FA00"));
        } else {
            holder.card_order.setCardBackgroundColor(Color.parseColor("#9973D7FF"));
        }

        holder.txt_orderId.setText(String.valueOf(orderList.get(position).getOrderId()));
        holder.txt_orderCustomerName.setText(orderList.get(position).getCustomerName());
        holder.txt_orderDateExpected.setText(dateTimeFormatter.format(orderList.get(position).getDateExpected()));

        holder.card_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewOrderActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                intent.putExtra("vendorId", orderList.get(position).getVendorId());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public CardView card_order;
        public TextView txt_orderId;
        public TextView txt_orderCustomerName;
        public TextView txt_orderDateExpected;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            card_order = itemView.findViewById(R.id.card_order);
            txt_orderId = itemView.findViewById(R.id.txt_orderId);
            txt_orderCustomerName = itemView.findViewById(R.id.txt_orderCustomerName);
            txt_orderDateExpected = itemView.findViewById(R.id.txt_orderDateExpected);
        }
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }
}
