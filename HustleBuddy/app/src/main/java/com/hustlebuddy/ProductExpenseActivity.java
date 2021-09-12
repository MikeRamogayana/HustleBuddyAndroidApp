package com.hustlebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hustlebuddy.adapter.ProductExpenseAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.ProductExpense;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProductExpenseActivity extends AppCompatActivity {

    Service service;

    FloatingActionButton btnRefresh;
    ProgressBar progressBar;
    Button btnCreateProductExpense;
    ConstraintLayout layoutAddProductExpense;
    Spinner spnProductName;
    EditText txtProductExpenseName;
    Spinner spnProductExpenseName;
    EditText txtProductExpenseCost;
    EditText txtProductExpenseQuantity;
    TextView txtProductExpenseSubTotal;
    Button btnProductExpenseAdd;
    Button btnProductExpenseClose;
    Spinner spnSearchProductExpense;
    EditText txtSearchProductExpenseDate;
    ImageView imgSearchProductExpenseDate;
    ImageView imgSearchProductExpenseResetDate;
    RecyclerView recyclerView;

    ArrayAdapter<String> productNameAdapter;
    List<Product> productList = new ArrayList<>();
    List<String> productNameList = new ArrayList<>();
    int productListPos = 0;

    ArrayAdapter<String> productExpenseNameAdapter;
    List<String> productExpenseNameList = new ArrayList<>();
    int productExpenseListPos = 0;

    ArrayAdapter<String> searchAdapter;
    List<Product> searchList = new ArrayList<>();
    List<String> searchNameList = new ArrayList<>();
    int searchPos = 0;

    ProductExpenseAdapter productExpenseAdapter;
    LinearLayoutManager linearLayoutManager;
    List<ProductExpense> productExpenseList = new ArrayList<>();

    DecimalFormat decimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.UK));
    DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    boolean byDate = false;
    String strDate;
    int vendorId, dateYear, dateMonth, dateDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_expense);

        Intent intent = getIntent();
        vendorId = intent.getIntExtra("vendorId", 0);

        service = new Service(this);

        searchNameList.add("All");
        searchList.add(null);
        searchPos = 0;

        btnRefresh = findViewById(R.id.btn_prodExpRefresh);
        progressBar = findViewById(R.id.progress_prodExpense);
        btnCreateProductExpense = findViewById(R.id.btn_createProductExpense);
        layoutAddProductExpense = findViewById(R.id.layout_addProductExpense);
        spnProductName = findViewById(R.id.spn_addProductExpenseProductCode);
        txtProductExpenseName = findViewById(R.id.txt_addProductExpenseName);
        spnProductExpenseName = findViewById(R.id.spn_addProductExpenseNameList);
        txtProductExpenseCost = findViewById(R.id.txt_addProductExpenseCost);
        txtProductExpenseQuantity = findViewById(R.id.txt_addProductExpenseQuantity);
        txtProductExpenseSubTotal = findViewById(R.id.txt_addProductExpenseSubTotal);
        btnProductExpenseAdd = findViewById(R.id.btn_addProductExpense);
        btnProductExpenseClose = findViewById(R.id.btn_addProductExpenseClose);
        spnSearchProductExpense = findViewById(R.id.spn_searchProductExpenseCode);
        txtSearchProductExpenseDate = findViewById(R.id.txt_searchProductExpenseDate);
        imgSearchProductExpenseDate = findViewById(R.id.img_searchProductExpenseDate);
        imgSearchProductExpenseResetDate = findViewById(R.id.img_searchProductExpenseResetDate);
        recyclerView = findViewById(R.id.recycler_ProductExpenses);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshData();
                btnRefresh.setVisibility(View.GONE);
            }
        });

        btnCreateProductExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddProductExpense.setVisibility(View.VISIBLE);
            }
        });

        productNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNameList);
        productNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProductName.setAdapter(productNameAdapter);
        spnProductName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productListPos = position;
                productExpenseNameList.clear();
                for(ProductExpense productExpense: productExpenseList) {
                    if(productExpense.getProductCode().equalsIgnoreCase(productList.get(position).getProductCode())){
                        productExpenseNameList.add(productExpense.getProductName());
                    }
                }
                productExpenseNameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productExpenseNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productExpenseNameList);
        productExpenseNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProductExpenseName.setAdapter(productExpenseNameAdapter);
        spnProductExpenseName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productExpenseListPos = position;
                txtProductExpenseName.setText(productExpenseNameList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtProductExpenseCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!txtProductExpenseQuantity.getText().toString().equals("") && count > 0) {
                    txtProductExpenseSubTotal.setText(("Sub Total: R " + decimalFormat.format(Integer.parseInt(txtProductExpenseQuantity.getText().toString()) * Double.parseDouble(txtProductExpenseCost.getText().toString()))));
                } else {
                    txtProductExpenseSubTotal.setText(("Sub Total: R 0.00"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        txtProductExpenseQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!txtProductExpenseCost.getText().toString().equals("") && count > 0) {
                    txtProductExpenseSubTotal.setText(("Sub Total: R " + decimalFormat.format(Integer.parseInt(txtProductExpenseQuantity.getText().toString()) * Double.parseDouble(txtProductExpenseCost.getText().toString()))));
                } else {
                    txtProductExpenseSubTotal.setText(("Sub Total: R 0.00"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnProductExpenseAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidateField(txtProductExpenseName) && ValidateField(txtProductExpenseCost) && ValidateField(txtProductExpenseQuantity)){
                    ProductExpense productExpense = new ProductExpense(0, vendorId, productList.get(productListPos).getProductCode(), txtProductExpenseName.getText().toString(),
                            Integer.parseInt(txtProductExpenseQuantity.getText().toString()), Double.parseDouble(txtProductExpenseCost.getText().toString()),
                            LocalDateTime.now());
                    progressBar.setVisibility(View.VISIBLE);
                    service.CreateProductExpense(productExpense, new Service.VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(ProductExpenseActivity.this, "Could not add product!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResponse(Object response) {
                            Toast.makeText(ProductExpenseActivity.this, "Product added successfully...", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            layoutAddProductExpense.setVisibility(View.GONE);
                            RefreshData();
                        }
                    });
                }
            }
        });

        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchNameList);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSearchProductExpense.setAdapter(searchAdapter);
        spnSearchProductExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchPos = position;
                if(searchList.get(searchPos) == null) {
                    RefreshDataByDate();
                    return;
                }
                productExpenseAdapter.getProductList().clear();
                productExpenseAdapter.getProductExpenseList().clear();
                for (ProductExpense productExpense: productExpenseList) {
                    if(productExpense.getProductCode().equals(searchList.get(searchPos).getProductCode())) {
                        service.GetProductByProductCode(productExpense.getProductCode(), new Service.ProductListener() {
                            @Override
                            public void onResponse(Product product) {
                                productExpenseAdapter.getProductList().add(product);
                                productExpenseAdapter.getProductExpenseList().add(productExpense);
                                productExpenseAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(String message) {
                            }
                        });
                    } else {
                        productExpenseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        imgSearchProductExpenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                dateYear = calendar.get(Calendar.YEAR);
                dateMonth = calendar.get(Calendar.MONTH);
                dateDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ProductExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                if(month < 10 && dayOfMonth < 10) {
                                    strDate = year + "-0" + (month + 1) + "-0" + dayOfMonth;
                                } else if(month < 10) {
                                    strDate = year + "-0" + (month + 1) + "-" + dayOfMonth;
                                } else if(dayOfMonth < 10) {
                                    strDate = year + "-" + (month + 1) + "-0" + dayOfMonth;
                                } else {
                                    strDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                                }
                                txtSearchProductExpenseDate.setText(strDate + " 00:00");
                                RefreshDataByDate();
                            }
                        }, dateYear, dateMonth, dateDay);
                datePickerDialog.show();
            }
        });

        imgSearchProductExpenseResetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearchProductExpenseDate.setText("");
                RefreshData();
            }
        });

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        productExpenseAdapter = new ProductExpenseAdapter(this);
        recyclerView.setAdapter(productExpenseAdapter);

        btnProductExpenseClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddProductExpense.setVisibility(View.GONE);
            }
        });

        RefreshProducts();
        RefreshData();
    }

    private void RefreshProducts() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetProductsByVendorId(vendorId, new Service.ProductsListener() {
            @Override
            public void onResponse(ArrayList<Product> products) {
                productList.clear();
                productList.addAll(products);
                productNameList.clear();
                searchList.clear();
                searchNameList.clear();
                searchList.add(null);
                searchNameList.add("All");
                for (Product product: productList) {
                    productNameList.add(product.getProductName());
                    searchNameList.add(product.getProductName());
                    searchList.add(product);
                }
                productNameAdapter.notifyDataSetChanged();
                searchAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProductExpenseActivity.this, "Could not load products!!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void RefreshData() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetProductExpensesByVendorID(vendorId, new Service.ProductExpensesListener() {
            @Override
            public void onResponse(ArrayList<ProductExpense> productExpenses) {
                productExpenseList.clear();
                productExpenseList.addAll(productExpenses);
                productExpenseNameList.clear();
                productExpenseAdapter.getProductList().clear();
                productExpenseAdapter.getProductExpenseList().clear();
                for (ProductExpense productExpense: productExpenseList) {
                    productExpenseNameList.add(productExpense.getProductName());
                    if(!searchNameList.get(searchPos).equalsIgnoreCase("all")) {
                        if(!searchList.get(searchPos).getProductCode().equals(productExpense.getProductCode())) {
                            continue;
                        }
                    }
                    service.GetProductByProductCode(productExpense.getProductCode(), new Service.ProductListener() {
                        @Override
                        public void onResponse(Product product) {
                            productExpenseAdapter.getProductList().add(product);
                            productExpenseAdapter.getProductExpenseList().add(productExpense);
                            productExpenseAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(ProductExpenseActivity.this, "Could not load product!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnRefresh.setVisibility(View.VISIBLE);
                        }
                    });
                }
                productExpenseNameAdapter.notifyDataSetChanged();
                productExpenseAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProductExpenseActivity.this, "Could not load product expenses!!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void RefreshDataByDate() {
        progressBar.setVisibility(View.VISIBLE);
        if(txtSearchProductExpenseDate.getText().toString().equals("")) {
            RefreshData();
            return;
        }
        service.GetProductExpensesByDate(vendorId, LocalDateTime.parse(txtSearchProductExpenseDate.getText().toString(), dateTimeFormater), new Service.ProductExpensesListener() {
            @Override
            public void onResponse(ArrayList<ProductExpense> productExpenses) {
                productExpenseList.clear();
                productExpenseList.addAll(productExpenses);
                productExpenseNameList.clear();
                productExpenseAdapter.getProductList().clear();
                productExpenseAdapter.getProductExpenseList().clear();
                for (ProductExpense productExpense: productExpenseList) {
                    productExpenseNameList.add(productExpense.getProductName());
                    service.GetProductByProductCode(productExpense.getProductCode(), new Service.ProductListener() {
                        @Override
                        public void onResponse(Product product) {
                            productExpenseAdapter.getProductList().add(product);
                            productExpenseAdapter.getProductExpenseList().add(productExpense);
                            productExpenseAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(ProductExpenseActivity.this, "Could not load product!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnRefresh.setVisibility(View.VISIBLE);
                        }
                    });
                }
                productExpenseNameAdapter.notifyDataSetChanged();
                productExpenseAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProductExpenseActivity.this, "Could not load product expenses!!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean ValidateField(EditText editText) {
        if(!editText.equals(txtProductExpenseName)) {
            if(editText.getText().toString().equals("")) {
                Toast.makeText(this, "Value cannot be empty!!!", Toast.LENGTH_SHORT).show();
                editText.setBackgroundResource(R.drawable.error_border);
                SetOnEdit(editText);
                return false;
            } else if (Double.parseDouble(editText.getText().toString()) == 0) {
                Toast.makeText(this, "Value cannot be less than 1!!!", Toast.LENGTH_SHORT).show();
                editText.setBackgroundResource(R.drawable.error_border);
                SetOnEdit(editText);
                return false;
            }
        } else {
            if(editText.getText().toString().length() < 2) {
                Toast.makeText(this, "Product name cannot be less than 3 characters!!!", Toast.LENGTH_SHORT).show();
                editText.setBackgroundResource(R.drawable.error_border);
                SetOnEdit(editText);
                return false;
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}