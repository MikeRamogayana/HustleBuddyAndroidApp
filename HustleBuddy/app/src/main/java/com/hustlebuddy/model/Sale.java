package com.hustlebuddy.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Sale implements Comparable<Sale> {

    private int saleId;
    private String productCode;
    private int quantity;
    private String location;
    private LocalDateTime date;
    private int vendorId;
    private String cashOrCredit;

    public Sale(int saleId, String productCode, int quantity, String location, LocalDateTime date, int vendorId, String cashOrCredit) {
        this.saleId = saleId;
        this.productCode = productCode;
        this.quantity = quantity;
        this.location = location;
        this.date = date;
        this.vendorId = vendorId;
        this.cashOrCredit = cashOrCredit;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Sale(JSONObject jsonObject) throws JSONException {
        this.setSaleId(jsonObject.getInt("saleId"));
        this.setProductCode(jsonObject.getString("productCode"));
        this.setQuantity(jsonObject.getInt("quantity"));
        this.setLocation(jsonObject.getString("location"));
        this.setDate(LocalDateTime.parse(jsonObject.getString("date")));
        this.setVendorId(jsonObject.getInt("vendorId"));
        this.setCashOrCredit(jsonObject.getString("cashOrCredit"));
    }

    public Sale() {
    }

    public static JSONObject toJSONObject(Sale sale) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("saleId", 0);
        jsonObject.put("productCode", sale.getProductCode());
        jsonObject.put("quantity", sale.getQuantity());
        jsonObject.put("location", sale.getLocation());
        jsonObject.put("date", sale.getDate());
        jsonObject.put("vendorId",sale.getVendorId());
        jsonObject.put("cashOrCredit", sale.getCashOrCredit());
        return jsonObject;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getCashOrCredit() {
        return cashOrCredit;
    }

    public  void setCashOrCredit(String cashOrCredit) {
        this.cashOrCredit = cashOrCredit;
    }

    @Override
    public int compareTo(Sale o) {
        return productCode.compareTo(o.productCode);
    }
}
