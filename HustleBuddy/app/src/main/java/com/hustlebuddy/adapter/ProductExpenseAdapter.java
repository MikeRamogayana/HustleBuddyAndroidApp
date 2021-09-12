package com.hustlebuddy.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hustlebuddy.R;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.ProductExpense;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProductExpenseAdapter extends RecyclerView.Adapter<ProductExpenseAdapter.ProductExpenseViewHolder> {

    Context context;
    List<ProductExpense> productExpenseList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DecimalFormat decimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.UK));

    boolean collapsed = true;

    public ProductExpenseAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProductExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_expense_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductExpenseViewHolder holder, int position) {
        holder.txtFor.setText(productList.get(position).getProductName());
        holder.txtName.setText(productExpenseList.get(position).getProductName());
        holder.txtDate.setText(("Date: " + dateTimeFormatter.format(productExpenseList.get(position).getDate())));
        holder.txtCost.setText(("R " + decimalFormat.format(productExpenseList.get(position).getCost())));
        holder.txtQuantity.setText(String.valueOf(productExpenseList.get(position).getQuantity()));
        holder.txtTotal.setText(("R " + decimalFormat.format(productExpenseList.get(position).getCost() * productExpenseList.get(position).getQuantity())));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Service(context).DeleteProductExpense(productExpenseList.get(holder.getAdapterPosition()).getProductId(), new Service.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Could not delete product!!!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Object response) {
                        Toast.makeText(context, "Product deleted successfully...", Toast.LENGTH_SHORT).show();
                        productList.remove(holder.getAdapterPosition());
                        productExpenseList.remove(holder.getAdapterPosition());
                        ProductExpenseAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productExpenseList.size();
    }

    public class ProductExpenseViewHolder extends RecyclerView.ViewHolder{
        
        TextView txtFor;
        TextView txtName;
        TextView txtDate;
        LinearLayout layoutDetails;
        TextView txtCost;
        TextView txtQuantity;
        TextView txtTotal;
        Button btnDelete;
        ImageView imgExpandCollapse;

        public ProductExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFor = itemView.findViewById(R.id.txt_viewProductExpenseCode);
            txtName = itemView.findViewById(R.id.txt_viewProductExpenseName);
            txtDate = itemView.findViewById(R.id.txt_viewProductExpenseDate);
            layoutDetails = itemView.findViewById(R.id.layout_viewProductExpenseDetails);
            txtCost = itemView.findViewById(R.id.txt_viewProductExpenseCost);
            txtQuantity = itemView.findViewById(R.id.txt_viewProductExpenseQuantity);
            txtTotal = itemView.findViewById(R.id.txt_viewProductExpenseTotal);
            btnDelete = itemView.findViewById(R.id.btn_viewProductExpenseDelete);
            imgExpandCollapse = itemView.findViewById(R.id.img_viewProductExpenseCollapseExpand);

            imgExpandCollapse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(collapsed){
                        layoutDetails.setVisibility(View.VISIBLE);
                        imgExpandCollapse.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                        collapsed = false;
                    } else {
                        layoutDetails.setVisibility(View.GONE);
                        imgExpandCollapse.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                        collapsed = true;
                    }
                }
            });
        }
    }

    public List<Product> getProductList() { return productList; }

    public List<ProductExpense> getProductExpenseList() {
        return productExpenseList;
    }
}
