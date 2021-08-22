package com.hustlebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hustlebuddy.adapter.SaleProductAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.DailyStock;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.Sale;
import com.hustlebuddy.model.TradingStock;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddSaleActivity extends AppCompatActivity {

    Service service;

    ProgressBar progressBar;
    Button btnCreateProduct;
    TableLayout tableAddProduct;
    Button btnAddProduct;
    Button btnCancel;
    Spinner spinnerCashOrCredit;
    Spinner spinnerProductNames;
    TextView txtRemaining;
    EditText txtQuantity;
    TextView txtSubTotal;
    RecyclerView recyclerView;
    TextView txtTotal;
    Button btnAddSale;

    ArrayAdapter<String> cashOrCreditAdapter;
    ArrayAdapter<String> productNamesAdapter;
    SaleProductAdapter saleProductAdapter;
    LinearLayoutManager linearLayoutManager;

    List<Product> productList = new ArrayList<>();
    List<TradingStock> tradingStockList = new ArrayList<>();
    List<String> productNamesList = new ArrayList<>();
    List<String> cashOrCreditList = Arrays.asList("Cash", "Credit");
    String cashOrCredit = "Cash";
    DailyStock stock;
    int position;
    int vendorId;
    int saleCount = 0;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sale);

        Intent intent = getIntent();
        vendorId = intent.getIntExtra("vendorId",0);
        service = new Service(this);

        progressBar = findViewById(R.id.progress_add_sale);
        btnCreateProduct = findViewById(R.id.btn_addSaleCreateProduct);
        tableAddProduct = findViewById(R.id.table_addSaleAddProduct);
        btnAddProduct = findViewById(R.id.btn_addSaleAddProduct);
        btnCancel = findViewById(R.id.btn_addSaleCancelProduct);
        spinnerCashOrCredit = findViewById(R.id.spinner_addSaleCashOrCredit);
        spinnerProductNames = findViewById(R.id.spinner_addSaleProductNames);
        txtRemaining = findViewById(R.id.txt_addSaleRemaining);
        txtQuantity = findViewById(R.id.txt_addSaleQuantity);
        txtSubTotal = findViewById(R.id.txt_addSaleSubTotal);
        recyclerView = findViewById(R.id.recycler_addSale);
        txtTotal = findViewById(R.id.txt_addSaleTotal);
        btnAddSale = findViewById(R.id.btn_addSale);

        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableAddProduct.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableAddProduct.setVisibility(View.GONE);
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(Integer.parseInt(txtQuantity.getText().toString()) > 0) {
                        saleProductAdapter.getQuantityList().add(Integer.parseInt(txtQuantity.getText().toString()));
                        saleProductAdapter.getProductList().add(productList.get(position));
                        saleProductAdapter.notifyDataSetChanged();
                        double total = 0;
                        for(int i = 0; i < saleProductAdapter.getItemCount(); i++) {
                            total += saleProductAdapter.getProductList().get(i).getSellingPrice() * saleProductAdapter.getQuantityList().get(i);
                        }
                        txtTotal.setText(("Total: R " + decimalFormat.format(total)));
                        saleCount++;
                        tableAddProduct.setVisibility(View.GONE);
                        Toast.makeText(AddSaleActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddSaleActivity.this, "Quantity can not be less than 1!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddSaleActivity.this, "Quantity can not be empty!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cashOrCreditAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cashOrCreditList);
        cashOrCreditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCashOrCredit.setAdapter(cashOrCreditAdapter);
        spinnerCashOrCredit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cashOrCredit = cashOrCreditList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        productNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNamesList);
        productNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductNames.setAdapter(productNamesAdapter);
        spinnerProductNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtRemaining.setText(("Remaining Items: " + (tradingStockList.get(position).getQuantity() - tradingStockList.get(position).getSold())));
                AddSaleActivity.this.position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0) {
                    txtSubTotal.setText(("Sub Total: R " + decimalFormat.format(Integer.parseInt(txtQuantity.getText().toString()) * productList.get(position).getSellingPrice())));
                } else {
                    txtSubTotal.setText(("Sub Total: R 0.00"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        saleProductAdapter = new SaleProductAdapter();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(saleProductAdapter);
        RefreshSaleData();

        btnAddSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saleProductAdapter.getItemCount() == 0) {
                    Toast.makeText(AddSaleActivity.this, "Not products selected!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i = 0; i < saleProductAdapter.getItemCount(); i++) {
                    Sale sale = new Sale(0, saleProductAdapter.getProductList().get(i).getProductCode(),
                            saleProductAdapter.getQuantityList().get(i), stock.getLocation(), LocalDateTime.now(), vendorId);
                    progressBar.setVisibility(View.VISIBLE);
                    service.CreateSale(sale, stock.getStockId(), new Service.VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(AddSaleActivity.this, "Could not create sale!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResponse(Object response) {
                            saleCount--;
                            if(saleCount == 0) {
                                Toast.makeText(AddSaleActivity.this, "Sale added successfully...", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AddSaleActivity.this, MainActivity.class);
                                intent.putExtra("vendorId", vendorId);
                                progressBar.setVisibility(View.GONE);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddSaleActivity.this, MainActivity.class);
        intent.putExtra("vendorId", vendorId);
        startActivity(intent);
        finish();
    }

    private void RefreshSaleData() {
        service.GetDailyStockByVendorId(vendorId, new Service.DailyStockListener() {
            @Override
            public void onResponse(DailyStock dailyStock) {
                stock = dailyStock;
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AddSaleActivity.this, "Could not load daily stock!!!", Toast.LENGTH_SHORT).show();
            }
        });
        service.GetTradingStocksByVendorId(vendorId, new Service.TradingStocksListener() {
            @Override
            public void onResponse(ArrayList<TradingStock> tradingStocks) {
                productList.clear();
                productNamesList.clear();
                tradingStockList.clear();
                for (TradingStock tradingStock: tradingStocks) {
                    service.GetProductByProductCode(tradingStock.getProductCode(), new Service.ProductListener() {
                        @Override
                        public void onResponse(Product product) {
                            productList.add(product);
                            productNamesList.add(product.getProductName());
                            tradingStockList.add(tradingStock);
                            productNamesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(AddSaleActivity.this, "Could not load product!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AddSaleActivity.this, "Could not load today stocks!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}