package com.hustlebuddy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TradingStock {

    private String productCode;
    private int stockId;
    private int quantity;
    private int sold;

    public TradingStock(String productCode, int stockId, int quantity, int sold) {
        this.productCode = productCode;
        this.stockId = stockId;
        this.quantity = quantity;
        this.sold = sold;
    }

    public TradingStock(JSONObject jsonObject) throws JSONException {
        setProductCode(jsonObject.getString("productCode"));
        setStockId(jsonObject.getInt("stockId"));
        setQuantity(jsonObject.getInt("quantity"));
        setSold(jsonObject.getInt("sold"));
    }

    public TradingStock() {
    }

    public static JSONObject toJSONObject(TradingStock tradingStock) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productCode", tradingStock.getProductCode());
        jsonObject.put("stockId", tradingStock.getStockId());
        jsonObject.put("quantity", tradingStock.getQuantity());
        jsonObject.put("sold", tradingStock.getSold());
        return jsonObject;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }
}
