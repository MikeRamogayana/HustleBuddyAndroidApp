package com.hustlebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ViewOrderActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    EditText txt_orderCustomerName;
    EditText txt_orderContactNumber;
    EditText txt_orderLocation;
    Spinner spinner_orderStatus;
    TextView txt_orderDateMade;
    TextView txt_orderDateExpected;
    EditText txt_orderDescription;
    Button btn_updateOrder;
    TextView txt_orderTotal;

    SaleProductAdapter saleProductAdapter;
    LinearLayoutManager linearLayoutManager;

    ArrayAdapter<String> arrayAdapter;
    List<String> statusList = Arrays.asList(new String[]{"Cancelled", "Completed", "Pending"});

    Service service;

    private int orderId;
    private int vendorId;
    private double total = 0;
    private String txtDateExpected;
    int dateYear, dateMonth, dateDay, timeHour, timeMinute;
    private Order order = new Order();
    private List<OrderedProduct> orderedProductList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();

    DecimalFormat decimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.UK));
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        Intent intent = getIntent();
        service = new Service(ViewOrderActivity.this);
        orderId = intent.getIntExtra("orderId", 0);
        vendorId = intent.getIntExtra("vendorId", 0);

        progressBar = findViewById(R.id.progress_view_order);
        recyclerView = findViewById(R.id.recycler_view_orderOrderedProducts);
        txt_orderCustomerName = findViewById(R.id.txt_view_orderCustomerName);
        txt_orderContactNumber = findViewById(R.id.txt_view_orderContactNumber);
        txt_orderLocation = findViewById(R.id.txt_view_orderLocation);
        spinner_orderStatus = findViewById(R.id.spinner_view_orderStatus);
        txt_orderDateMade = findViewById(R.id.txt_view_orderTimeMade);
        txt_orderDateExpected = findViewById(R.id.txt_view_orderTimeExpected);
        txt_orderDescription = findViewById(R.id.txt_view_orderDescription);
        txt_orderTotal = findViewById(R.id.txt_orderTotal);
        btn_updateOrder = findViewById(R.id.btn_orderUpdate);

        arrayAdapter = new ArrayAdapter<>(ViewOrderActivity.this, android.R.layout.simple_spinner_item, statusList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_orderStatus.setAdapter(arrayAdapter);

        saleProductAdapter = new SaleProductAdapter();
        linearLayoutManager = new LinearLayoutManager(ViewOrderActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(saleProductAdapter);

        txt_orderDateExpected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                dateYear = calendar.get(Calendar.YEAR);
                dateMonth = calendar.get(Calendar.MONTH);
                dateDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ViewOrderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                if(month < 10 && dayOfMonth < 10) {
                                    txtDateExpected = (year + "-0" + (month + 1) + "-0" + dayOfMonth);
                                } else if(month < 10) {
                                    txtDateExpected = (year + "-0" + (month + 1) + "-" + dayOfMonth);
                                } else if(dayOfMonth < 10) {
                                    txtDateExpected = (year + "-" + (month + 1) + "-0" + dayOfMonth);
                                } else {
                                    txtDateExpected = (year + "-" + (month + 1) + "-" + dayOfMonth);
                                }
                                timeHour = calendar.get(Calendar.HOUR_OF_DAY);
                                timeMinute = calendar.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(ViewOrderActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                timeHour = hourOfDay;
                                                timeMinute = minute;
                                                if(minute == 0 && hourOfDay == 0){
                                                    txtDateExpected += " " + ("00:00");
                                                } else if(minute == 0){
                                                    txtDateExpected += " " + (hourOfDay + ":00");
                                                } else  if(hourOfDay == 0) {
                                                    txtDateExpected += " " + ("00" + minute);
                                                }else if(minute < 10 && hourOfDay < 10){
                                                    txtDateExpected += " " + ("0" + hourOfDay + ":0" + minute);
                                                } else if(minute < 10){
                                                    txtDateExpected += " " + (hourOfDay + ":0" + minute);
                                                } else  if(hourOfDay < 10){
                                                    txtDateExpected += " " + ("0" + hourOfDay + ":" + minute);
                                                } else {
                                                    txtDateExpected += " " + (hourOfDay + ":" + minute);
                                                }
                                                txt_orderDateExpected.setText(txtDateExpected);
                                            }
                                        }, timeHour, timeMinute, true);
                                timePickerDialog.show();
                            }
                        }, dateYear, dateMonth, dateDay);
                datePickerDialog.show();

            }
        });

        btn_updateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(ValidateField(txt_orderContactNumber) && ValidateField(txt_orderContactNumber) &&
                ValidateField(txt_orderLocation) && ValidateField(txt_orderDescription))) {
                    Toast.makeText(ViewOrderActivity.this, "Check field highlighted in red!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                order.setCustomerName(txt_orderCustomerName.getText().toString());
                order.setContactNumber(txt_orderContactNumber.getText().toString());
                order.setLocation(txt_orderLocation.getText().toString());
                order.setDateExpected(LocalDateTime.parse(txt_orderDateExpected.getText().toString(), dateTimeFormatter));
                order.setDescription(txt_orderDescription.getText().toString());
                progressBar.setVisibility(View.VISIBLE);
                service.UpdateOrder(order, new Service.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(ViewOrderActivity.this, "Could Not Update Order!!!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(Object response) {
                        Toast.makeText(ViewOrderActivity.this, "Order Updated Successfully...", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(ViewOrderActivity.this, MainActivity.class);
                        intent.putExtra("vendorId", vendorId);
                        intent.putExtra("fragment", 0);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        spinner_orderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ViewOrderActivity.this.order.setStatus(statusList.get(position).toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RefreshOrderData();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RefreshOrderData() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetOrderByOrderId(orderId, new Service.OrderListener() {
            @Override
            public void onResponse(Order order) {
                ViewOrderActivity.this.order = order;
                txt_orderCustomerName.setText(order.getCustomerName());
                txt_orderContactNumber.setText(order.getContactNumber());
                txt_orderLocation.setText(order.getLocation());
                txt_orderDateMade.setText(("Created On: " + dateTimeFormatter.format(order.getDateMade())));
                txt_orderDateExpected.setText(("Required On: " + dateTimeFormatter.format(order.getDateExpected())));
                txtDateExpected = dateTimeFormatter.format(order.getDateExpected());
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
                                    total += orderedProduct.getQuantity() * product.getSellingPrice();
                                    txt_orderTotal.setText(("R " + decimalFormat.format(total)));
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
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ViewOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ViewOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewOrderActivity.this, MainActivity.class);
        intent.putExtra("vendorId", vendorId);
        intent.putExtra("fragment", 0);
        startActivity(intent);
        finish();
    }

    private boolean ValidateField(EditText editText) {
        if(editText.getText().toString().length() < 3) {
            editText.setBackgroundResource(R.drawable.error_border);
            SetOnEdit(editText);
            return false;
        }
        return true;
    }

    private void SetOnEdit(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    editText.setBackgroundResource(R.drawable.primary_boarder);
                }
            }
        });
    }

}