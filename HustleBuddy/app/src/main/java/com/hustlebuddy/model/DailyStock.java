package com.hustlebuddy.model;

import android.app.job.JobScheduler;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class DailyStock {

    private int stockId;
    private int vendorId;
    private LocalDateTime date;
    private String location;
    private double cost;
    private double expectedReturn;

    public DailyStock(int stockId, int vendorId, LocalDateTime date, String location, double cost, double expectedReturn) {
        this.stockId = stockId;
        this.vendorId = vendorId;
        this.date = date;
        this.location = location;
        this.cost = cost;
        this.expectedReturn = expectedReturn;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DailyStock(JSONObject jsonObject) throws JSONException {
        this.setStockId(jsonObject.getInt("stockId"));
        this.setVendorId(jsonObject.getInt("vendorId"));
        this.setLocation(jsonObject.getString("location"));
        this.setDate(LocalDateTime.parse(jsonObject.getString("date")));
        this.setCost(jsonObject.getDouble("cost"));
        this.setExpectedReturn(jsonObject.getDouble("expectedReturn"));
    }

    public DailyStock() {
    }

    public static JSONObject toJSONObject(DailyStock dailyStock) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stockId", dailyStock.stockId);
        jsonObject.put("vendorId", dailyStock.vendorId);
        jsonObject.put("location", dailyStock.location);
        jsonObject.put("date", dailyStock.date);
        jsonObject.put("cost", dailyStock.cost);
        jsonObject.put("expectedReturn", dailyStock.expectedReturn);
        return jsonObject;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(double expectedReturn) {
        this.expectedReturn = expectedReturn;
    }
}
