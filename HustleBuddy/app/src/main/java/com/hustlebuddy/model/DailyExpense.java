package com.hustlebuddy.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class DailyExpense {

    private int expenseId;
    private int stockId;
    private LocalDateTime date;
    private double transport;
    private double lunch;
    private double other;

    public DailyExpense(int expenseId, int stockId, LocalDateTime date, double transport, double lunch, double other) {
        this.expenseId = expenseId;
        this.stockId = stockId;
        this.date = date;
        this.transport = transport;
        this.lunch = lunch;
        this.other = other;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DailyExpense(JSONObject jsonObject) throws JSONException {
        this.setExpenseId(jsonObject.getInt("expenseId"));
        this.setStockId(jsonObject.getInt("stockId"));
        this.setDate(LocalDateTime.parse(jsonObject.getString("date")));
        this.setLunch(jsonObject.getDouble("lunch"));
        this.setTransport(jsonObject.getDouble("transport"));
        this.setOther(jsonObject.getDouble("other"));
    }

    public DailyExpense() {
    }

    public static JSONObject toJSONObject(DailyExpense dailyExpense) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("expenseId",dailyExpense.getExpenseId());
        jsonObject.put("stockId", dailyExpense.getStockId());
        jsonObject.put("date", dailyExpense.getDate());
        jsonObject.put("transport",dailyExpense.getTransport());
        jsonObject.put("lunch",dailyExpense.getLunch());
        jsonObject.put("other",dailyExpense.getOther());
        return  jsonObject;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getTransport() {
        return transport;
    }

    public void setTransport(double transport) {
        this.transport = transport;
    }

    public double getLunch() {
        return lunch;
    }

    public void setLunch(double lunch) {
        this.lunch = lunch;
    }

    public double getOther() {
        return other;
    }

    public void setOther(double other) {
        this.other = other;
    }
}
