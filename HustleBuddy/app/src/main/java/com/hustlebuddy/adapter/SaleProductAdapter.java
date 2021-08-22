package com.hustlebuddy.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.hustlebuddy.R;
import com.hustlebuddy.model.Product;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaleProductAdapter extends RecyclerView.Adapter<SaleProductAdapter.SaleProductHolder> {

    List<Product> productList = new ArrayList<>();
    List<Integer> quantityList = new ArrayList<>();
    List<LocalDateTime> dateList = new ArrayList<>();

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public SaleProductAdapter() {
    }

    @NonNull
    @Override
    public SaleProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SaleProductHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sale_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SaleProductHolder holder, int position) {
        if(!dateList.isEmpty()) {
            holder.txtDate.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(dateList.get(position)));
            holder.tableDate.setVisibility(View.VISIBLE);
        } else {
            holder.tableDate.setVisibility(View.GONE);
        }
        holder.txtProductName.setText(productList.get(position).getProductName());
        holder.txtQuantity.setText(String.valueOf(quantityList.get(position)));
        holder.txtSubTotal.setText(("R " + decimalFormat.format(productList.get(position).getSellingPrice() * quantityList.get(position))));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class SaleProductHolder extends RecyclerView.ViewHolder {

        TextView txtProductName;
        TextView txtQuantity;
        TextView txtSubTotal;
        TableRow tableDate;
        TextView txtDate;

        public SaleProductHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txt_saleCardProductName);
            txtQuantity = itemView.findViewById(R.id.txt_saleCardQuantity);
            txtSubTotal = itemView.findViewById(R.id.txt_saleCardSubTotal);
            tableDate = itemView.findViewById(R.id.table_saleCardDate);
            txtDate = itemView.findViewById(R.id.txt_saleCardDate);
        }
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<Integer> getQuantityList() {
        return quantityList;
    }

    public List<LocalDateTime> getDateList() {
        return dateList;
    }
}
