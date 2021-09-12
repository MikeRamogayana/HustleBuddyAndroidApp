package com.hustlebuddy;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.hustlebuddy.adapter.SaleProductAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.CreditSale;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.Sale;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaleFragment extends Fragment {

    Service service;
    View view;

    ProgressBar progressBar;
    Button btnCreateSale;
    Spinner spinnerCashOrCredit;
    EditText txtSaleDate;
    ImageView imgSaleDateReset;
    ImageView imgSaleDate;
    EditText txtSaleSearch;
    ImageView imgSaleSearch;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SaleProductAdapter saleProductAdapter;
    FloatingActionButton btnSaleRefresh;
    TextView txtSaleCash;
    TextView txtSaleCredit;

    ArrayAdapter<String> cashOrCreditAdapter;

    List<Sale> saleList = new ArrayList<>();
    List<String> cashOrCreditList = Arrays.asList("All", "Cash", "Credit");

    int vendorId, dateDay, dateYear, dateMonth;
    boolean inflated = false;
    String strDate;
    Context context;
    int position = 0;

    public SaleFragment(Context context, int vendorId) {
        this.context = context;
        this.vendorId = vendorId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_sale, container, false);

        service = new Service(view.getContext());

        progressBar = view.findViewById(R.id.progress_sale);
        btnSaleRefresh = view.findViewById(R.id.btn_recentSaleRefresh);
        btnCreateSale = view.findViewById(R.id.btn_createSale);
        spinnerCashOrCredit = view.findViewById(R.id.spn_searchProductExpenseCode);
        txtSaleDate = view.findViewById(R.id.txt_viewSaleDate);
        imgSaleDateReset = view.findViewById(R.id.img_viewSaleDateReset);
        imgSaleDate = view.findViewById(R.id.img_viewSaleDate);
        recyclerView = view.findViewById(R.id.recycler_recentSales);
        txtSaleCash = view.findViewById(R.id.txt_viewSaleCash);
        txtSaleCredit = view.findViewById(R.id.txt_viewSaleCredit);

        btnSaleRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaleRefresh.setVisibility(View.GONE);
                RefreshSaleData();
            }
        });

        btnCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AddSaleActivity.class);
                intent.putExtra("vendorId", vendorId);
                startActivity(intent);
                getActivity().finish();
            }
        });

        cashOrCreditAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, cashOrCreditList);
        cashOrCreditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCashOrCredit.setAdapter(cashOrCreditAdapter);

        spinnerCashOrCredit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(cashOrCreditList.get(position).equalsIgnoreCase("cash")) {
                    txtSaleCash.setBackgroundColor(Color.parseColor("#9987FA00"));
                    txtSaleCredit.setBackgroundColor(0);
                } else if (cashOrCreditList.get(position).equalsIgnoreCase("credit")) {
                    txtSaleCash.setBackgroundColor(0);
                    txtSaleCredit.setBackgroundColor(Color.parseColor("#9973D7FF"));
                } else {
                    txtSaleCash.setBackgroundColor(Color.parseColor("#9987FA00"));
                    txtSaleCredit.setBackgroundColor(Color.parseColor("#9973D7FF"));
                }
                SaleFragment.this.position = position;
                DisplayByCashOrCredit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        imgSaleDateReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSaleDate.setText("");
                RefreshSaleData();
            }
        });

        imgSaleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                dateYear = calendar.get(Calendar.YEAR);
                dateMonth = calendar.get(Calendar.MONTH);
                dateDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
                                txtSaleDate.setText(strDate + " 00:00");
                                RefreshSaleDataByDate();
                            }
                        }, dateYear, dateMonth, dateDay);
                datePickerDialog.show();
            }
        });

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        saleProductAdapter = new SaleProductAdapter();
        recyclerView.setAdapter(saleProductAdapter);
        saleProductAdapter.setSaleFragment(this);

        if(!inflated) {
            RefreshSaleData();
            inflated = true;
        }

        return view;
    }

    private void DisplayByCashOrCredit() {
        saleProductAdapter.getProductList().clear();
        saleProductAdapter.getQuantityList().clear();
        saleProductAdapter.getDateList().clear();
        saleProductAdapter.getCreditSaleList().clear();

        if(saleList.size() == 0) {
            saleProductAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        for (Sale sale: saleList) {
            if(!cashOrCreditList.get(position).equalsIgnoreCase("all")) {
                if(!sale.getCashOrCredit().equalsIgnoreCase(cashOrCreditList.get(position))) {
                    saleProductAdapter.notifyDataSetChanged();
                    continue;
                }
            }
            service.GetProductByProductCode(sale.getProductCode(), new com.hustlebuddy.controller.Service.ProductListener() {
                @Override
                public void onResponse(Product product) {
                    if(sale.getCashOrCredit().equalsIgnoreCase("credit")) {
                        service.GetCreditSaleBySaleId(sale.getSaleId(), new Service.CreditSaleListener() {
                            @Override
                            public void onResponse(CreditSale creditSale) {
                                saleProductAdapter.getProductList().add(product);
                                saleProductAdapter.getQuantityList().add(sale.getQuantity());
                                saleProductAdapter.getDateList().add(sale.getDate());
                                saleProductAdapter.getCreditSaleList().add(creditSale);
                                saleProductAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(String message) {

                            }
                        });
                    } else {
                        saleProductAdapter.getProductList().add(product);
                        saleProductAdapter.getQuantityList().add(sale.getQuantity());
                        saleProductAdapter.getDateList().add(sale.getDate());
                        saleProductAdapter.getCreditSaleList().add(null);
                        saleProductAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(view.getContext(), "Could not load product sales!!!", Toast.LENGTH_SHORT).show();
                    btnSaleRefresh.setVisibility(View.VISIBLE);
                }
            });
        }
        progressBar.setVisibility(View.GONE);

    }

    public void RefreshSaleData() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetDailySales(vendorId, new Service.SalesListener() {
            @Override
            public void onResponse(ArrayList<Sale> sales) {

                saleProductAdapter.getProductList().clear();
                saleProductAdapter.getQuantityList().clear();
                saleProductAdapter.getDateList().clear();
                saleProductAdapter.getCreditSaleList().clear();
                saleList.clear();
                saleList.addAll(sales);
                saleList.sort(new Comparator<Sale>() {
                    @Override
                    public int compare(Sale o1, Sale o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });

                DisplayByCashOrCredit();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(view.getContext(), "Could not load recent sales!!!", Toast.LENGTH_SHORT).show();
                btnSaleRefresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void RefreshSaleDataByDate() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetSalesByDate(vendorId, LocalDateTime.parse(txtSaleDate.getText().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), new Service.SalesListener() {
            @Override
            public void onResponse(ArrayList<Sale> sales) {

                saleProductAdapter.getProductList().clear();
                saleProductAdapter.getQuantityList().clear();
                saleProductAdapter.getDateList().clear();
                saleProductAdapter.getCreditSaleList().clear();
                saleList.clear();
                saleList.addAll(sales);
                saleList.sort(new Comparator<Sale>() {
                    @Override
                    public int compare(Sale o1, Sale o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });

                DisplayByCashOrCredit();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(view.getContext(), "Could not load recent sales!!!", Toast.LENGTH_SHORT).show();
                btnSaleRefresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
