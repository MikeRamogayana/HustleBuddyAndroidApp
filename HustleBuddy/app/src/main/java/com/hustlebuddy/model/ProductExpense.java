package com.hustlebuddy.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProductExpense {

    private int productId;
    private int vendorId;
    private String productCode;
    private String productName;
    private int quantity;
    private double cost;
    private LocalDateTime date;

    public ProductExpense(int productId, int vendorId, String productCode,
                          String productName, int quantity, double cost, LocalDateTime date)
    {
        this.productId = productId;
        this.vendorId = vendorId;
        this.productCode = productCode;
        this.productName = productName;
        this.quantity = quantity;
        this.cost = cost;
        this.date = date;
    }

    public ProductExpense(){ }

    public ProductExpense(JSONObject jsonObject) throws JSONException{
        productId = jsonObject.getInt("productId");
        vendorId = jsonObject.getInt("vendorId");
        productCode = jsonObject.getString("productCode");
        productName = jsonObject.getString("productName");
        quantity = jsonObject.getInt("quantity");
        cost = jsonObject.getDouble("cost");
        date = LocalDateTime.parse(jsonObject.getString("date"));

    }

    public static JSONObject toJSONObject(ProductExpense productExpense) throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productId",productExpense.getProductId());
        jsonObject.put("vendorId",productExpense.getVendorId());
        jsonObject.put("productCode", productExpense.getProductCode());
        jsonObject.put("productName", productExpense.getProductName());
        jsonObject.put("quantity",productExpense.getQuantity());
        jsonObject.put("cost", productExpense.getCost());
        jsonObject.put("date",productExpense.getDate());
        return jsonObject;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
