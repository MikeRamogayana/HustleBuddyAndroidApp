package com.hustlebuddy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderedProduct {

    private int orderId;
    private String productCode;
    private int quantity;

    public OrderedProduct(int orderId, String productCode, int quantity) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public OrderedProduct(JSONObject jsonObject) throws JSONException {
        setProductCode(jsonObject.getString("productCode"));
        setOrderId(jsonObject.getInt("orderId"));
        setQuantity(jsonObject.getInt("quantity"));
    }

    public OrderedProduct() {
    }

    public static JSONObject toJSONObject(OrderedProduct orderedProduct) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productCode", orderedProduct.getProductCode());
        jsonObject.put("orderId", orderedProduct.getOrderId());
        jsonObject.put("quantity", orderedProduct.getQuantity());
        return jsonObject;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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
}
