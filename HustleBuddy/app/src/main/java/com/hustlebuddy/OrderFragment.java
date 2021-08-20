package com.hustlebuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hustlebuddy.adapter.OrdersRecyclerAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderFragment extends Fragment {

    OrdersRecyclerAdapter ordersRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    Button btn_orderAdd;
    FloatingActionButton btn_orderRefresh;
    View view;

    List<Order> orderList = new ArrayList<>();
    private Service service;
    private int vendorId;
    private String title;
    Context context;

    boolean inflated = false;

    public OrderFragment(Context context, int vendorId) {
        this.context = context;
        this.vendorId = vendorId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_order, container, false);
        service = new Service(view.getContext());

        btn_orderAdd = view.findViewById(R.id.btn_createOrder);
        btn_orderRefresh = view.findViewById(R.id.btn_orderRefresh);
        recyclerView = view.findViewById(R.id.recycler_Orders);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        ordersRecyclerAdapter = new OrdersRecyclerAdapter(view.getContext(), orderList);
        recyclerView.setAdapter(ordersRecyclerAdapter);

        btn_orderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AddOrderActivity.class);
                intent.putExtra("vendorId", vendorId);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btn_orderRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshOrderData();
            }
        });

        if(!inflated) {
            RefreshOrderData();
            inflated = true;
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RefreshOrderData() {
        service.GetOrdersByVendorId(vendorId, new Service.OrdersListener() {
            @Override
            public void onResponse(ArrayList<Order> orders) {
                orderList = orders;
                orderList.sort(new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return o2.getDateMade().compareTo(o1.getDateMade());
                    }
                });
                int size = ordersRecyclerAdapter.getItemCount();
                ordersRecyclerAdapter.getOrderList().clear();
                ordersRecyclerAdapter.notifyItemRangeRemoved(0, size);
                ordersRecyclerAdapter.setOrderList(orderList);
                ordersRecyclerAdapter.notifyItemRangeInserted(0, orderList.size());
            }

            @Override
            public void onError(String message) {
                Toast.makeText(view.getContext(), "Could not load orders...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
