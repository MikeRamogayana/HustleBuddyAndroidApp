package com.hustlebuddy.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Order {

    private int orderId;
    private int vendorId;
    private String customerName;
    private String contactNumber;
    private String location;
    private String status;
    private LocalDateTime dateMade;
    private LocalDateTime dateExpected;
    private String description;

    public Order(int orderId, int vendorId, String customerName, String contactNumber,
                 String location, String status, LocalDateTime dateMade, LocalDateTime dateExpected,
                 String description) {
        this.orderId = orderId;
        this.vendorId = vendorId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.location = location;
        this.status = status;
        this.dateMade = dateMade;
        this.dateExpected = dateExpected;
        this.description = description;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Order(JSONObject jsonObject) throws JSONException {
        this.setOrderId(jsonObject.getInt("orderId"));
        this.setVendorId(jsonObject.getInt("vendorId"));
        this.setCustomerName(jsonObject.getString("customerName"));
        this.setContactNumber(jsonObject.getString("contactNumber"));
        this.setLocation(jsonObject.getString("location"));
        this.setStatus(jsonObject.getString("status"));
        this.setDateMade(LocalDateTime.parse(jsonObject.getString("dateMade")));
        this.setDateExpected(LocalDateTime.parse(jsonObject.getString("dateExpected")));
        this.setDescription(jsonObject.getString("description"));
    }

    public Order() {
    }

    public static JSONObject toJSONObject(Order order) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId",order.getOrderId());
        jsonObject.put("vendorId",order.getVendorId());
        jsonObject.put("customerName",order.getCustomerName());
        jsonObject.put("contactNumber",order.getContactNumber());
        jsonObject.put("location",order.getLocation());
        jsonObject.put("status",order.getStatus());
        jsonObject.put("dateMade", order.getDateMade());
        jsonObject.put("dateExpected", order.getDateExpected());
        jsonObject.put("description",order.getDescription());
        return jsonObject;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDateMade() {
        return dateMade;
    }

    public void setDateMade(LocalDateTime dateMade) {
        this.dateMade = dateMade;
    }

    public LocalDateTime getDateExpected() {
        return dateExpected;
    }

    public void setDateExpected(LocalDateTime dateExpected) {
        this.dateExpected = dateExpected;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
