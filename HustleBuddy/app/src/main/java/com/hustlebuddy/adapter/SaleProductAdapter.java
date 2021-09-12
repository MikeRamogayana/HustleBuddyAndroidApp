package com.hustlebuddy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hustlebuddy.R;
import com.hustlebuddy.SaleFragment;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.CreditSale;
import com.hustlebuddy.model.Product;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaleProductAdapter extends RecyclerView.Adapter<SaleProductAdapter.SaleProductHolder> {

    List<Product> productList = new ArrayList<>();
    List<Integer> quantityList = new ArrayList<>();
    List<LocalDateTime> dateList = new ArrayList<>();
    List<CreditSale> creditSaleList = new ArrayList<>();

    boolean isClosed = true;

    SaleFragment saleFragment = null;

    DecimalFormat decimalFormat = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.UK));
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
            holder.rowHeader.setBackgroundColor(Color.parseColor("#9987FA00"));
            holder.txtDate.setText(("Date: " + dateTimeFormatter.format(dateList.get(position))));
            holder.tableDate.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.GONE);
        } else {
            holder.tableDate.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productList.remove(holder.getAdapterPosition());
                    quantityList.remove(holder.getAdapterPosition());
                    SaleProductAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                }
            });
        }
        try {
            holder.txtCustomerName.setText(creditSaleList.get(holder.getAdapterPosition()).getCustomerName());
            holder.txtContactNumber.setText(creditSaleList.get(holder.getAdapterPosition()).getContactNumber());
            holder.txtDueDate.setText(dateTimeFormatter.format(creditSaleList.get(holder.getAdapterPosition()).getDueDate()));
            holder.txtLastPaid.setText(dateTimeFormatter.format(creditSaleList.get(holder.getAdapterPosition()).getPayDate()));
            holder.txtCreditAmount.setText(("R " + decimalFormat.format(creditSaleList.get(holder.getAdapterPosition()).getCreditAmount())));
            holder.txtPaidAmount.setText(("R " + decimalFormat.format(creditSaleList.get(holder.getAdapterPosition()).getPaidAmount())));
            holder.txtDueAmount.setText(("R " + decimalFormat.format(creditSaleList.get(holder.getAdapterPosition()).getCreditAmount() - creditSaleList.get(holder.getAdapterPosition()).getPaidAmount())));
            holder.btnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update Credit
                    if(Double.parseDouble(holder.txtPayAmount.getText().toString()) > 0) {
                        creditSaleList.get(holder.getAdapterPosition()).setPaidAmount(creditSaleList.get(holder.getAdapterPosition()).getPaidAmount() + Double.parseDouble(holder.txtPayAmount.getText().toString()));
                        if(creditSaleList.get(holder.getAdapterPosition()).getCreditAmount() - creditSaleList.get(holder.getAdapterPosition()).getPaidAmount() <= 0) {
                            creditSaleList.get(holder.getAdapterPosition()).setStatus("paid");
                        }
                        new Service(v.getContext()).UpdateCreditSale(creditSaleList.get(holder.getAdapterPosition()), new Service.VolleyResponseListener() {
                            @Override
                            public void onError(String message) {
                                Toast.makeText(v.getContext(), "Could not update credit!!!", Toast.LENGTH_SHORT);

                                holder.tableCreditDetails.setVisibility(View.GONE);
                                holder.imgShowCredit.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                                isClosed = true;
                            }

                            @Override
                            public void onResponse(Object response) {
                                Toast.makeText(v.getContext(), "Credit updated successfully...", Toast.LENGTH_SHORT);

                                holder.tableCreditDetails.setVisibility(View.GONE);
                                holder.imgShowCredit.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                                isClosed = true;
                                SaleProductAdapter.this.notifyItemChanged(holder.getAdapterPosition());
                            }

                        });
                    } else {
                        Toast.makeText(v.getContext(), "Amount cannot be 0", Toast.LENGTH_SHORT).show();
                        holder.txtPayAmount.setBackgroundResource(R.drawable.error_border);
                        holder.txtPayAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
            if(creditSaleList.get(position).getCreditAmount() - creditSaleList.get(position).getPaidAmount() <= 0) {
                holder.txtPayAmount.setEnabled(false);
                holder.btnPay.setEnabled(false);
            }
            holder.rowHeader.setBackgroundColor(Color.parseColor("#9973D7FF"));
            holder.layoutCredit.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            holder.layoutCredit.setVisibility(View.GONE);
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

        TableRow rowHeader;
        TextView txtProductName;
        TextView txtQuantity;
        TextView txtSubTotal;
        TableRow tableDate;
        TextView txtDate;
        ConstraintLayout layoutCredit;
        TextView txtCustomerName;
        TextView txtContactNumber;
        TableLayout tableCreditDetails;
        TextView txtDueDate;
        TextView txtLastPaid;
        TextView txtCreditAmount;
        TextView txtPaidAmount;
        TextView txtDueAmount;
        EditText txtPayAmount;
        Button btnPay;
        ImageView imgShowCredit;
        Button btnRemove;

        public SaleProductHolder(@NonNull View itemView) {
            super(itemView);
            rowHeader = itemView.findViewById(R.id.row_saleCardHeader);
            txtProductName = itemView.findViewById(R.id.txt_saleCardProductName);
            txtQuantity = itemView.findViewById(R.id.txt_saleCardQuantity);
            txtSubTotal = itemView.findViewById(R.id.txt_saleCardSubTotal);
            tableDate = itemView.findViewById(R.id.table_saleCardDate);
            txtDate = itemView.findViewById(R.id.txt_saleCardDate);
            layoutCredit = itemView.findViewById(R.id.layout_saleCardCredit);
            txtCustomerName = itemView.findViewById(R.id.txt_saleCardCustomerName);
            txtContactNumber = itemView.findViewById(R.id.txt_saleCardContactNumber);
            tableCreditDetails = itemView.findViewById(R.id.table_saleCardViewCredit);
            txtDueDate = itemView.findViewById(R.id.txt_saleCardDueDate);
            txtLastPaid = itemView.findViewById(R.id.txt_saleCardLastPaid);
            txtCreditAmount = itemView.findViewById(R.id.txt_saleCardCreditAmount);
            txtPaidAmount = itemView.findViewById(R.id.txt_saleCardPaidAmount);
            txtDueAmount = itemView.findViewById(R.id.txt_saleCardDueAmount);
            txtPayAmount = itemView.findViewById(R.id.txt_saleCardPayAmount);
            btnPay = itemView.findViewById(R.id.btn_saleCardPay);
            imgShowCredit = itemView.findViewById(R.id.img_saleCardShowCredit);
            btnRemove = itemView.findViewById(R.id.btn_productRemove);

            imgShowCredit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isClosed) {
                        tableCreditDetails.setVisibility(View.VISIBLE);
                        imgShowCredit.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                        isClosed = false;
                    } else {
                        tableCreditDetails.setVisibility(View.GONE);
                        imgShowCredit.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                        isClosed = true;
                    }
                }
            });
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

    public  List<CreditSale> getCreditSaleList() { return creditSaleList; }

    public void setSaleFragment(SaleFragment saleFragment) { this.saleFragment = saleFragment; }
}
