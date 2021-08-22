package com.hustlebuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OrderFragment extends Fragment {

    ProgressBar progressBar;
    FloatingActionButton btn_orderRefresh;
    Button btn_orderAdd;
    Spinner spinnerStatus;
    ArrayAdapter<String> arrayAdapter;
    RecyclerView recyclerView;
    OrdersRecyclerAdapter ordersRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;
    TextView txtCancelled;
    TextView txtPending;
    TextView txtCompleted;
    View view;

    List<String> statusList = Arrays.asList("All", "Pending", "Completed", "Cancelled");
    List<Order> orderList = new ArrayList<>();
    private Service service;
    private int vendorId;
    private int cur_position = 0;
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

        btn_orderRefresh = view.findViewById(R.id.btn_orderRefresh);
        progressBar = view.findViewById(R.id.progress_order);
        btn_orderAdd = view.findViewById(R.id.btn_createOrder);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        recyclerView = view.findViewById(R.id.recycler_Orders);
        txtCancelled = view.findViewById(R.id.txt_status_cancelled);
        txtPending = view.findViewById(R.id.txt_status_pending);
        txtCompleted = view.findViewById(R.id.txt_status_completed);

        arrayAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, statusList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(arrayAdapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cur_position = position;
                if(statusList.get(position).equalsIgnoreCase("cancelled")) {
                    txtCancelled.setBackgroundColor(Color.parseColor("#99FF3000"));
                    txtCompleted.setBackgroundColor(0);
                    txtPending.setBackgroundColor(0);
                } else if(statusList.get(position).equalsIgnoreCase("pending")) {
                    txtCancelled.setBackgroundColor(0);
                    txtCompleted.setBackgroundColor(0);
                    txtPending.setBackgroundColor(Color.parseColor("#9973D7FF"));
                } else if(statusList.get(position).equalsIgnoreCase("completed")) {
                    txtCancelled.setBackgroundColor(0);
                    txtCompleted.setBackgroundColor(Color.parseColor("#9987FA00"));
                    txtPending.setBackgroundColor(0);
                } else {
                    txtCancelled.setBackgroundColor(Color.parseColor("#99FF3000"));
                    txtCompleted.setBackgroundColor(Color.parseColor("#9987FA00"));
                    txtPending.setBackgroundColor(Color.parseColor("#9973D7FF"));
                }
                RefreshOrderData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        progressBar.setVisibility(View.VISIBLE);
        service.GetOrdersByVendorId(vendorId, new Service.OrdersListener() {
            @Override
            public void onResponse(ArrayList<Order> orders) {
                if(!statusList.get(cur_position).equalsIgnoreCase("All")) {
                    orders.removeIf(o -> !o.getStatus().equalsIgnoreCase(String.valueOf(statusList.get(cur_position))));
                }
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
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(view.getContext(), "Could not load orders...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
