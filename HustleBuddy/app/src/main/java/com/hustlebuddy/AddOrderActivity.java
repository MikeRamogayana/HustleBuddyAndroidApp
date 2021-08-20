package com.hustlebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hustlebuddy.adapter.SaleProductAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Order;
import com.hustlebuddy.model.OrderedProduct;
import com.hustlebuddy.model.Product;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddOrderActivity extends AppCompatActivity {

    Service service;

    RecyclerView recyclerView;

    ArrayAdapter<String> productsAdapter;
    SaleProductAdapter saleProductAdapter;
    LinearLayoutManager linearLayoutManager;

    TextView txt_orderCustomerName;
    TextView txt_orderContactNumber;
    TextView txt_orderLocation;
    TextView txt_orderDate;
    TextView txt_orderTime;
    ImageView img_orderDate;
    ImageView img_orderTime;
    TextView txt_orderDescription;
    LinearLayout layout;
    Spinner spinnerAddProduct;
    TextView txt_orderQuantity;
    TextView txt_orderSubTotal;
    Button btnBack;
    Button btnDone;
    Button btnAddOrder;
    Button btnAddOrderProduct;

    int dateYear, dateMonth, dateDay, timeHour, timeMinute;

    List<Product> productList = new ArrayList<>();
    List<String> productNameList = new ArrayList<>();

    int vendorId;
    Product product;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        Intent intent = getIntent();
        vendorId = intent.getIntExtra("vendorId",0);

        service = new Service(this);

        productNameList.add("Select Product");

        txt_orderCustomerName = findViewById(R.id.txt_add_orderCustomerName);
        txt_orderContactNumber = findViewById(R.id.txt_add_orderContactNumber);
        txt_orderLocation = findViewById(R.id.txt_add_orderLocation);
        txt_orderDate = findViewById(R.id.txt_add_orderDate);
        txt_orderTime = findViewById(R.id.txt_add_orderTime);
        img_orderDate = findViewById(R.id.img_add_orderDate);
        img_orderTime = findViewById(R.id.img_add_orderTime);
        txt_orderDescription = findViewById(R.id.txt_add_orderDescription);
        btnAddOrderProduct = findViewById(R.id.btn_add_orderAddProduct);
        btnAddOrder = findViewById(R.id.btn_orderAdd);

        layout = findViewById(R.id.linear_addProduct);
        btnBack = findViewById(R.id.btn_order_addBack);
        btnDone = findViewById(R.id.btn_order_addDoneOrderedProduct);
        txt_orderQuantity = findViewById(R.id.txt_add_orderQuantity);
        txt_orderSubTotal = findViewById(R.id.txt_add_orderSubTotal);
        spinnerAddProduct = findViewById(R.id.spinner_add_orderOrderedProductName);

        recyclerView = findViewById(R.id.recycler_add_orderOrderedProducts);
        linearLayoutManager = new LinearLayoutManager(this);
        saleProductAdapter = new SaleProductAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(saleProductAdapter);

        spinnerAddProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product = productList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txt_orderQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    txt_orderSubTotal.setText("Sub Total       R " + decimalFormat.format(product.getSellingPrice() * Integer.parseInt(s.toString())));
                } else {
                    txt_orderSubTotal.setText("Sub Total       R 0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        img_orderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                dateYear = calendar.get(Calendar.YEAR);
                dateMonth = calendar.get(Calendar.MONTH);
                dateDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddOrderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                if(month < 10 && dayOfMonth < 10) {
                                    txt_orderDate.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);
                                } else if(month < 10) {
                                    txt_orderDate.setText(year + "-0" + (month + 1) + "-" + dayOfMonth);
                                } else if(dayOfMonth < 10) {
                                    txt_orderDate.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                                } else {
                                    txt_orderDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                                }
                            }
                        }, dateYear, dateMonth, dateDay);
                datePickerDialog.show();
            }
        });

        img_orderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                timeHour = calendar.get(Calendar.HOUR_OF_DAY);
                timeMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddOrderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeHour = hourOfDay;
                                timeMinute = minute;
                                if(minute == 0 && hourOfDay == 0){
                                    txt_orderTime.setText("00:00");
                                } else if(minute == 0){
                                    txt_orderTime.setText(hourOfDay + ":00");
                                } else  if(hourOfDay == 0) {
                                    txt_orderTime.setText("00" + minute);
                                }else if(minute < 10 && hourOfDay < 10){
                                    txt_orderTime.setText("0" + hourOfDay + ":0" + minute);
                                } else if(minute < 10){
                                    txt_orderTime.setText(hourOfDay + ":0" + minute);
                                } else  if(hourOfDay < 10){
                                    txt_orderTime.setText("0" + hourOfDay + ":" + minute);
                                } else {
                                    txt_orderTime.setText(hourOfDay + ":" + minute);
                                }
                            }
                        }, timeHour, timeMinute, true);
                timePickerDialog.show();
            }
        });

        productsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNameList);
        productsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddProduct.setAdapter(productsAdapter);

        btnAddOrderProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saleProductAdapter.getProductList().add(product);
                    saleProductAdapter.getQuantityList().add(Integer.parseInt(txt_orderQuantity.getText().toString()));
                    saleProductAdapter.notifyDataSetChanged();
                    layout.setVisibility(View.GONE);
                    Toast.makeText(AddOrderActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(AddOrderActivity.this, "Could not add product!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Order order = new Order(0, vendorId, txt_orderCustomerName.getText().toString(),
                            txt_orderContactNumber.getText().toString(), txt_orderLocation.getText().toString(),
                            "pending", LocalDateTime.now(), LocalDateTime.parse(txt_orderDate.getText().toString() + " " + txt_orderTime.getText().toString(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), txt_orderDescription.getText().toString());
                    service.CreateOrder(order, new Service.VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(AddOrderActivity.this, "Could not create order", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Object response) {
                            service.GetRecentOrder(vendorId, txt_orderCustomerName.getText().toString(),
                                    txt_orderContactNumber.getText().toString(), new Service.OrderListener() {
                                        @Override
                                        public void onResponse(Order order) {
                                            for(int i = 0; i < saleProductAdapter.getItemCount(); i++) {
                                                OrderedProduct orderedProduct = new OrderedProduct(order.getOrderId(),
                                                        saleProductAdapter.getProductList().get(i).getProductCode(),
                                                        saleProductAdapter.getQuantityList().get(i));
                                                service.CreateOrderedProduct(orderedProduct, new Service.VolleyResponseListener() {
                                                    @Override
                                                    public void onError(String message) {
                                                        Toast.makeText(AddOrderActivity.this, "Could not add product to order!!!", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onResponse(Object response) {

                                                    }
                                                });
                                            }
                                            Toast.makeText(AddOrderActivity.this, "Order created successfully...", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddOrderActivity.this, MainActivity.class);
                                            intent.putExtra("vendorId", vendorId);
                                            intent.putExtra("fragment", 0);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(AddOrderActivity.this, "Could not create!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(AddOrderActivity.this, "Could not get order, check order form!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GetProducts();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddOrderActivity.this, MainActivity.class);
        intent.putExtra("vendorId", vendorId);
        intent.putExtra("fragment", 0);
        startActivity(intent);
        finish();
    }

    private void GetProducts() {
        service.GetProductsByVendorId(vendorId, new Service.ProductsListener() {
            @Override
            public void onResponse(ArrayList<Product> products) {
                productList.clear();
                productNameList.clear();
                for(Product product: products) {
                    productNameList.add(product.getProductName());
                    productList.add(product);
                    productsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AddOrderActivity.this, "Could not fetch products!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}