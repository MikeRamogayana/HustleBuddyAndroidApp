package com.hustlebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hustlebuddy.adapter.SaleProductAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.CreditSale;
import com.hustlebuddy.model.DailyStock;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.Sale;
import com.hustlebuddy.model.TradingStock;

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

    // Popup
    View view;
    PopupWindow popupWindow;
    EditText txtCustomerName;
    EditText txtContactNumber;
    EditText txtCreditAmount;
    EditText txtDeposit;
    EditText txtDueDate;
    ImageView imgDueDate;
    Button btnAddCredit;

    ArrayAdapter<String> cashOrCreditAdapter;
    ArrayAdapter<String> productNamesAdapter;
    SaleProductAdapter saleProductAdapter;
    LinearLayoutManager linearLayoutManager;

    List<Product> productList = new ArrayList<>();
    List<TradingStock> tradingStockList = new ArrayList<>();
    List<String> productNamesList = new ArrayList<>();
    List<String> cashOrCreditList = Arrays.asList("Cash", "Credit");
    String cashOrCredit = "cash";
    DailyStock stock;
    int position;
    int vendorId;
    //int saleCount = 0;
    int dateYear, dateMonth, dateDay;

    DecimalFormat decimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.UK));

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

        // Popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_add_credit_details, null);
        txtCustomerName = view.findViewById(R.id.txt_addCreditCustomerName);
        txtContactNumber = view.findViewById(R.id.txt_addCreditContactNumber);
        txtCreditAmount = view.findViewById(R.id.txt_addCreditAmount);
        txtDeposit = view.findViewById(R.id.txt_addCreditDeposit);
        txtDueDate = view.findViewById(R.id.txt_addCreditDueDate);
        imgDueDate = view.findViewById(R.id.img_addCreditDueDate);
        btnAddCredit = view.findViewById(R.id.btn_addCreditCreate);

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
                        tableAddProduct.setVisibility(View.GONE);
                        Toast.makeText(AddSaleActivity.this, "Product added...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddSaleActivity.this, "Quantity can not be less than 1!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(AddSaleActivity.this, "Quantity can not be empty!!!", Toast.LENGTH_SHORT).show();
                    txtQuantity.setBackgroundResource(R.drawable.error_border);
                    txtQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus){
                                v.setBackgroundResource(R.drawable.primary_boarder);
                            }
                        }
                    });
                }
            }
        });

        cashOrCreditAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cashOrCreditList);
        cashOrCreditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCashOrCredit.setAdapter(cashOrCreditAdapter);
        spinnerCashOrCredit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cashOrCredit = cashOrCreditList.get(position).toLowerCase();
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

        saleProductAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                double total = 0;
                for(int i = 0; i < saleProductAdapter.getItemCount(); i++) {
                    total += saleProductAdapter.getProductList().get(i).getSellingPrice() * saleProductAdapter.getQuantityList().get(i);
                }
                txtTotal.setText(("Total: R " + decimalFormat.format(total)));
            }
        });

        btnAddSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saleProductAdapter.getItemCount() == 0) {
                    Toast.makeText(AddSaleActivity.this, "Not products selected!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cashOrCredit.equalsIgnoreCase("credit")) {
                    showPopupWindow(v);
                    return;
                }
                createSale("cash");
            }
        });
    }

    private void createSale(String mode) {
        int[] saleCount = {saleProductAdapter.getItemCount()};
        for(int i = 0; i < saleProductAdapter.getItemCount(); i++) {
            Sale sale = new Sale(0, saleProductAdapter.getProductList().get(i).getProductCode(),
                    saleProductAdapter.getQuantityList().get(i), stock.getLocation(), LocalDateTime.now(), vendorId, mode);
            progressBar.setVisibility(View.VISIBLE);
            service.CreateSale(sale, stock.getStockId(), new Service.VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Toast.makeText(AddSaleActivity.this, "Could not create sale!!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(Object response) {
                    if(mode.equalsIgnoreCase("credit")) {
                        service.GetRecentSale(vendorId, new Service.SaleListener() {
                            @Override
                            public void onResponse(Sale sale) {
                                CreditSale creditSale = new CreditSale(0, sale.getSaleId(), vendorId, txtCustomerName.getText().toString(),
                                        txtContactNumber.getText().toString(), Double.parseDouble(txtCreditAmount.getText().toString()),
                                        Double.parseDouble(txtDeposit.getText().toString()), LocalDateTime.parse(txtDueDate.getText().toString() + " 00:00",
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), LocalDateTime.now(), LocalDateTime.now(), "unpaid");
                                service.CreateCreditSale(creditSale, new Service.VolleyResponseListener() {
                                    @Override
                                    public void onError(String message) {
                                        Toast.makeText(AddSaleActivity.this, "Could not create credit sale!!!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onResponse(Object response) {
                                    }
                                });
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(AddSaleActivity.this, "Could not get recent sale!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    saleCount[0]--;
                    if(saleCount[0] == 0) {
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

    private void showPopupWindow(View v) {

        imgDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                dateYear = calendar.get(Calendar.YEAR);
                dateMonth = calendar.get(Calendar.MONTH);
                dateDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddSaleActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                if(month < 10 && dayOfMonth < 10) {
                                    txtDueDate.setText(year + "-0" + (month + 1) + "-0" + dayOfMonth);
                                } else if(month < 10) {
                                    txtDueDate.setText(year + "-0" + (month + 1) + "-" + dayOfMonth);
                                } else if(dayOfMonth < 10) {
                                    txtDueDate.setText(year + "-" + (month + 1) + "-0" + dayOfMonth);
                                } else {
                                    txtDueDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                                }
                            }
                        }, dateYear, dateMonth, dateDay);
                datePickerDialog.show();
            }
        });

        btnAddCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSale("credit");
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(view, v.getWidth() - 80, 530, true);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
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