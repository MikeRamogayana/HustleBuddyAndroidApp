package com.hustlebuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hustlebuddy.adapter.ExpenseAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.DailyExpense;
import com.hustlebuddy.model.DailyStock;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExpenseFragment extends Fragment {

    Service service;

    ProgressBar progressBar;
    FloatingActionButton btnRefresh;
    Button btnViewProductExpense;
    ConstraintLayout layout;
    EditText txtLunch;
    EditText txtTransport;
    EditText txtOther;
    Button btnUpdate;
    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;
    ExpenseAdapter expenseAdapter;

    int vendorId;
    Context context;
    DailyExpense dailyExpense;

    DecimalFormat decimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.UK));

    public ExpenseFragment(Context context, int vendorId) {
        this.context = context;
        this.vendorId = vendorId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_expense, container, false);
        service = new Service(view.getContext());

        progressBar = view.findViewById(R.id.progress_expense);
        btnRefresh = view.findViewById(R.id.btn_expenseRefresh);
        btnViewProductExpense = view.findViewById(R.id.btn_viewProductExpenses);
        layout = view.findViewById(R.id.expense_fragment);
        txtLunch = view.findViewById(R.id.txt_expenseLunch);
        txtTransport = view.findViewById(R.id.txt_expenseTransport);
        txtOther = view.findViewById(R.id.txt_expenseOther);
        btnUpdate = view.findViewById(R.id.btn_expenseUpdate);
        recyclerView = view.findViewById(R.id.recycler_Expense);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData();
                UpdateRecyclerView();
                btnRefresh.setVisibility(View.GONE);
            }
        });

        btnViewProductExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ProductExpenseActivity.class);
                intent.putExtra("vendorId", vendorId);
                startActivity(intent);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(ValidateField(txtLunch) && ValidateField(txtTransport) && ValidateField(txtOther))) {
                    Toast.makeText(context, "Check field highlighted in red!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dailyExpense.setLunch(Double.parseDouble(txtLunch.getText().toString()));
                dailyExpense.setTransport(Double.parseDouble(txtTransport.getText().toString()));
                dailyExpense.setOther(Double.parseDouble(txtOther.getText().toString()));
                progressBar.setVisibility(View.VISIBLE);
                service.UpdateDailyExpense(dailyExpense, new Service.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Could not update expenses!!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Object response) {
                        Toast.makeText(context, "Expenses updated successfully...", Toast.LENGTH_SHORT).show();
                        UpdateRecyclerView();
                    }
                });
            }
        });

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        expenseAdapter = new ExpenseAdapter();
        recyclerView.setAdapter(expenseAdapter);

        UpdateRecyclerView();
        GetData();

        return view;
    }

    private void GetData() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetDailyStockByVendorId(vendorId, new Service.DailyStockListener() {
            @Override
            public void onResponse(DailyStock dailyStock) {
                service.GetDailyExpenseByStockId(dailyStock.getStockId(), new Service.DailyExpenseListener() {
                    @Override
                    public void onResponse(DailyExpense dailyExpense) {
                        if(dailyExpense != null) {
                            ExpenseFragment.this.dailyExpense = dailyExpense;
                            txtLunch.setText(decimalFormat.format(dailyExpense.getLunch()));
                            txtTransport.setText(decimalFormat.format(dailyExpense.getTransport()));
                            txtOther.setText(decimalFormat.format(dailyExpense.getOther()));
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Could not load daily expense", Toast.LENGTH_SHORT).show();
                        btnRefresh.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context, "Could not daily stock", Toast.LENGTH_SHORT).show();
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void UpdateRecyclerView() {
        progressBar.setVisibility(View.VISIBLE);
        service.GetMonthlyStocks(vendorId, new Service.DailyStocksListener() {
            @Override
            public void onResponse(ArrayList<DailyStock> dailyStocks) {
                dailyStocks.sort(new Comparator<DailyStock>() {
                    @Override
                    public int compare(DailyStock o1, DailyStock o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });

                expenseAdapter.getDailyExpenseList().clear();
                for(DailyStock dailyStock: dailyStocks) {
                    service.GetDailyExpenseByStockId(dailyStock.getStockId(), new Service.DailyExpenseListener() {
                        @Override
                        public void onResponse(DailyExpense dailyExpense) {
                            expenseAdapter.getDailyExpenseList().add(dailyExpense);
                            expenseAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(context, "Could not load monthly expenses!!!", Toast.LENGTH_SHORT).show();
                            btnRefresh.setVisibility(View.VISIBLE);
                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context, "Could not load monthly stocks!!!", Toast.LENGTH_SHORT).show();
                btnRefresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean ValidateField(EditText editText) {
        if(editText.getText().toString().equals("")) {
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