package com.hustlebuddy.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.hustlebuddy.R;
import com.hustlebuddy.model.DailyExpense;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder> {

    List<DailyExpense> dailyExpenseList = new ArrayList<>();

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ExpenseAdapter() {
    }

    @NonNull
    @Override
    public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_card, null));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ExpenseHolder holder, int position) {
        holder.txtExpenseLunch.setText("R " + decimalFormat.format(dailyExpenseList.get(position).getLunch()));
        holder.txtExpenseTransport.setText("R " + decimalFormat.format(dailyExpenseList.get(position).getTransport()));
        holder.txtExpenseOther.setText("R " + decimalFormat.format(dailyExpenseList.get(position).getOther()));
        holder.txtExpenseDate.setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dailyExpenseList.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return dailyExpenseList.size();
    }

    public class ExpenseHolder extends RecyclerView.ViewHolder {

        TextView txtExpenseLunch;
        TextView txtExpenseTransport;
        TextView txtExpenseOther;
        TextView txtExpenseDate;

        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            txtExpenseLunch = itemView.findViewById(R.id.txt_viewExpenseLunch);
            txtExpenseTransport = itemView.findViewById(R.id.txt_viewExpenseTransport);
            txtExpenseOther = itemView.findViewById(R.id.txt_viewExpenseOther);
            txtExpenseDate = itemView.findViewById(R.id.txt_expenseDate);
        }
    }

    public List<DailyExpense> getDailyExpenseList() {
        return dailyExpenseList;
    }
}
