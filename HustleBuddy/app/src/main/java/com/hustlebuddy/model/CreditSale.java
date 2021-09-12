package com.hustlebuddy.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CreditSale {

    private int creditId;
    private int saleId;
    private int vendorId;
    private String customerName;
    private String contactNumber;
    private double creditAmount;
    private double paidAmount;
    private LocalDateTime dueDate;
    private LocalDateTime payDate;
    private LocalDateTime date;
    private String status;

    public CreditSale(int creditId, int saleId, int vendorId, String customerName, String contactNumber, double creditAmount, double paidAmount, LocalDateTime dueDate, LocalDateTime payDate, LocalDateTime date, String status) {
        this.creditId = creditId;
        this.saleId = saleId;
        this.vendorId = vendorId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.creditAmount = creditAmount;
        this.paidAmount = paidAmount;
        this.dueDate = dueDate;
        this.payDate = payDate;
        this.date = date;
        this.status = status;
    }

    public CreditSale() {
    }

    public CreditSale(JSONObject jsonObject) throws JSONException {
        creditId = jsonObject.getInt("creditId");
        saleId = jsonObject.getInt("saleId");
        vendorId = jsonObject.getInt("vendorId");
        customerName = jsonObject.getString("customerName");
        contactNumber = jsonObject.getString("contactNumber");
        creditAmount = jsonObject.getDouble("creditAmount");
        paidAmount = jsonObject.getDouble("paidAmount");
        dueDate = LocalDateTime.parse(jsonObject.getString("dueDate"));
        payDate = LocalDateTime.parse(jsonObject.getString("payDate"));
        date = LocalDateTime.parse(jsonObject.getString("date"));
        status = jsonObject.getString("status");
    }

    public static JSONObject toJSONObject(CreditSale creditSale) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("creditId", creditSale.getCreditId());
        jsonObject.put("saleId", creditSale.getSaleId());
        jsonObject.put("vendorId", creditSale.getVendorId());
        jsonObject.put("customerName", creditSale.getCustomerName());
        jsonObject.put("contactNumber", creditSale.getContactNumber());
        jsonObject.put("creditAmount", creditSale.getCreditAmount());
        jsonObject.put("paidAmount", creditSale.getPaidAmount());
        jsonObject.put("dueDate", creditSale.getDueDate());
        jsonObject.put("payDate", creditSale.getPayDate());
        jsonObject.put("date", creditSale.getDate());
        jsonObject.put("status", creditSale.getStatus());
        return jsonObject;
    }

    public int getCreditId() {
        return creditId;
    }

    public void setCreditId(int creditId) {
        this.creditId = creditId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDateTime payDate) {
        this.payDate = payDate;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
