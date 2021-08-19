package com.hustlebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hustlebuddy.adapter.SaleProductAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Order;
import com.hustlebuddy.model.OrderedProduct;
import com.hustlebuddy.model.Product;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ViewOrderActivity extends AppCompatActivity {

    TextView txt_orderId;
    RecyclerView recyclerView;
    TextView txt_orderCustomerName;
    TextView txt_orderContactNumber;
    TextView txt_orderLocation;
    Spinner spinner_orderStatus;
    TextView txt_orderDateMade;
    TextView txt_orderDateExpected;
    TextView txt_orderDescription;
    Button btn_updateOrder;
    TextView txt_orderTotal;

    SaleProductAdapter saleProductAdapter;
    LinearLayoutManager linearLayoutManager;

    ArrayAdapter<String> arrayAdapter;
    List<String> statusList = Arrays.asList(new String[]{"cancelled", "completed", "pending"});

    Service service;

    private int orderId;
    private Order order = new Order();
    private List<OrderedProduct> orderedProductList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        Intent intent = getIntent();
        service = new Service(ViewOrderActivity.this);
        orderId = intent.getIntExtra("orderId", 0);

        recyclerView = findViewById(R.id.recycler_view_orderOrderedProducts);
        txt_orderCustomerName = findViewById(R.id.txt_view_orderCustomerName);
        txt_orderContactNumber = findViewById(R.id.txt_view_orderContactNumber);
        txt_orderLocation = findViewById(R.id.txt_view_orderLocation);
        spinner_orderStatus = findViewById(R.id.spinner_view_orderStatus);
        txt_orderDateMade = findViewById(R.id.txt_view_orderTimeMade);
        txt_orderDateExpected = findViewById(R.id.txt_view_orderTimeExpected);
        txt_orderDescription = findViewById(R.id.txt_view_orderDescription);
        btn_updateOrder = findViewById(R.id.btn_orderUpdate);

        arrayAdapter = new ArrayAdapter<>(ViewOrderActivity.this, android.R.layout.simple_spinner_item, statusList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_orderStatus.setAdapter(arrayAdapter);

        saleProductAdapter = new SaleProductAdapter();
        linearLayoutManager = new LinearLayoutManager(ViewOrderActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(saleProductAdapter);

        btn_updateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.UpdateOrder(order, new Service.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(ViewOrderActivity.this, "Could Not Update Order!!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Object response) {
                        Toast.makeText(ViewOrderActivity.this, "Order Updated Successfully...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        spinner_orderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ViewOrderActivity.this.order.setStatus(statusList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RefreshOrderData();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RefreshOrderData() {
        service.GetOrderByOrderId(orderId, new Service.OrderListener() {
            @Override
            public void onResponse(Order order) {
                ViewOrderActivity.this.order = order;
                txt_orderCustomerName.setText(order.getCustomerName());
                txt_orderContactNumber.setText(order.getContactNumber());
                txt_orderLocation.setText(order.getLocation());
                txt_orderDateMade.setText(dateTimeFormatter.format(order.getDateMade()));
                txt_orderDateExpected.setText(dateTimeFormatter.format(order.getDateExpected()));
                txt_orderDescription.setText(order.getDescription());
                for(int i = 0; i < statusList.size(); i++) {
                    if(order.getStatus().equals(statusList.get(i))) {
                        spinner_orderStatus.setSelection(i);
                    }
                }

                service.GetOrderedProductsByOrderId(orderId, new Service.OrderedProductsListener() {
                    @Override
                    public void onResponse(ArrayList<OrderedProduct> orderedProducts) {
                        for(OrderedProduct orderedProduct: orderedProducts) {
                            service.GetProductByProductCode(orderedProduct.getProductCode(), new Service.ProductListener() {
                                @Override
                                public void onResponse(Product product) {
                                    saleProductAdapter.getProductList().add(product);
                                    saleProductAdapter.getQuantityList().add(orderedProduct.getQuantity());
                                    saleProductAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(ViewOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ViewOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ViewOrderActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}