package com.hustlebuddy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {

    private String productCode;
    private int vendorId;
    private String productName;
    private String description;
    private double costPrice;
    private double sellingPrice;
    private double discountPrice;

    public Product(String productCode, int vendorId, String productName, String description, double costPrice, double sellingPrice, double discountPrice) {
        this.productCode = productCode;
        this.vendorId = vendorId;
        this.productName = productName;
        this.description = description;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.discountPrice = discountPrice;
    }

    public Product(JSONObject jsonObject) throws JSONException {
        this.setProductCode(jsonObject.getString("productCode"));
        this.setVendorId(jsonObject.getInt("vendorId"));
        this.setProductName(jsonObject.getString("productName"));
        this.setDescription(jsonObject.getString("description"));
        this.setCostPrice(jsonObject.getDouble("costPrice"));
        this.setSellingPrice(jsonObject.getDouble("sellingPrice"));
        this.setDiscountPrice(jsonObject.getDouble("discountPrice"));
    }

    public Product() {
    }

    public static JSONObject toJSONObject(Product product) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productCode", product.getProductCode());
        jsonObject.put("vendorId", product.getVendorId());
        jsonObject.put("productName", product.getProductName());
        jsonObject.put("description", product.getDescription());
        jsonObject.put("costPrice", product.getCostPrice());
        jsonObject.put("sellingPrice", product.getSellingPrice());
        jsonObject.put("discountPrice", product.getDiscountPrice());
        return jsonObject;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }
}
