package com.hustlebuddy.controller;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hustlebuddy.model.DailyExpense;
import com.hustlebuddy.model.DailyStock;
import com.hustlebuddy.model.Order;
import com.hustlebuddy.model.OrderedProduct;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.Sale;
import com.hustlebuddy.model.TradingStock;
import com.hustlebuddy.model.Vendor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Service {

    public static final String baseUrl = "https://hustlebuddy.azurewebsites.net/api/";
    public static final String vendorUrl = "https://hustlebuddy.azurewebsites.net/api/vendor/";
    public static final String expenseUrl = "https://hustlebuddy.azurewebsites.net/api/expense/";
    public static final String orderUrl = "https://hustlebuddy.azurewebsites.net/api/order/";
    public static final String saleUrl = "https://hustlebuddy.azurewebsites.net/api/sale/";
    public static final String productUrl = "https://hustlebuddy.azurewebsites.net/api/product/";

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    Context context;

    public Service(Context context){
        this.context = context;
    }

    //Asynchronous Object CallBack
    public interface VolleyResponseListener{
        void onError(String message);
        void onResponse(Object response);
    }
    // For Creating
    private void StringPostRequest(String url, JSONObject jsonObject, VolleyResponseListener volleyListener){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                volleyListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyListener.onError(error.getMessage());
            }
        }) {
            @Override
            public  String getBodyContentType(){
                return "application/json; charset=utf-8";
            }
            @Override
            public  byte[] getBody() throws AuthFailureError {
                try {
                    return jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        //adding request to queue
        DataComSingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
    // For Updating
    private void StringPutRequest(String url, JSONObject jsonObject, VolleyResponseListener volleyListener){

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                volleyListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyListener.onError(error.getMessage());
            }
        }) {
            @Override
            public  String getBodyContentType(){
                return "application/json; charset=utf-8";
            }
            @Override
            public  byte[] getBody() throws AuthFailureError {
                try {
                    return jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        //adding request to queue
        DataComSingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
    // For Deleting
    private void StringDeleteRequest(String url, VolleyResponseListener volleyListener){

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                volleyListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyListener.onError(error.getMessage());
            }
        });
        //adding request to queue
        DataComSingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    //------Vendor Account Management-------//
    // Logging In
    public void Login(String email,String password,final VolleyResponseListener volleyListener) {

        String url = vendorUrl + "login/" + email + "/" + password;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int vendorId = response.getInt("vendorId");
                            volleyListener.onResponse(vendorId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyListener.onError("Could not login, error occurred!");
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }
    // Register Account
    public void Register(Vendor vendor, final VolleyResponseListener volleyListener) {

        String url = vendorUrl + "create";

        //creating request body
        try {
            StringPostRequest(url, Vendor.toJSONObject(vendor), volleyListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get Vendor By VendorId
    public void GetVendor(int vendorId, final VolleyResponseListener volleyListener) {

        String url = vendorUrl + "/get/vendorId/" + vendorId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    int id = 0;
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            id = response.getInt("vendorId");
                            volleyListener.onResponse(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        volleyListener.onError("An error occurred!");
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);

    }

    //------Stock Management------//
    public interface DailyStockListener{
        void onResponse(DailyStock dailyStock);
        void onError(String message);
    }

    public interface TradingStockListener{
        void onResponse(TradingStock tradingStock);
        void onError(String message);
    }

    public interface TradingStocksListener{
        void onResponse(ArrayList<TradingStock> tradingStocks);
        void onError(String message);
    }

    public interface DailyExpenseListener{
        void onResponse(DailyExpense dailyExpense);
        void onError(String message);
    }

    // Get Daily Stock
    public void GetDailyStockByVendorId(int vendorId, DailyStockListener dailyStockListener){
        String url =  baseUrl + "daily/get/stock/vendorId/" + vendorId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DailyStock dailyStock = null;
                        try {
                            dailyStock = new DailyStock(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dailyStockListener.onResponse(dailyStock);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dailyStockListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Get TradingStocks By Stock Id
    public void GetTradingStocksByStockId(int stockId, TradingStocksListener tradingStocksListener){
        String url = baseUrl + "trading/get/stockId/" + stockId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<TradingStock> tradingStocks = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                tradingStocks.add(new TradingStock(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        tradingStocksListener.onResponse(tradingStocks);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tradingStocksListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Get TradingStocks By Stock Id
    public void GetTradingStocksByVendorId(int vendorId, TradingStocksListener tradingStocksListener){
        String url = baseUrl + "trading/get/vendorId/" + vendorId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<TradingStock> tradingStocks = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                tradingStocks.add(new TradingStock(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        tradingStocksListener.onResponse(tradingStocks);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tradingStocksListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Get DailyStock By StockId
    public void GetStockById(int stockId, DailyStockListener dailyStockListener){
        String url = baseUrl + "daily/get/stockId/" + stockId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DailyStock dailyStock = null;
                        try {
                            dailyStock = new DailyStock(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dailyStockListener.onResponse(dailyStock);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dailyStockListener.onError("An error occurred!");
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Get DailyExpense By StockId
    public void GetDailyExpenseByStockId(int stockId, DailyExpenseListener expenseListener){
        String url = baseUrl + "expense/get/stockId/" + stockId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        DailyExpense dailyExpense = null;
                        try {
                            dailyExpense = new DailyExpense(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        expenseListener.onResponse(dailyExpense);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        expenseListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Update DailyExpense
    public void UpdateDailyExpense(DailyExpense expense, VolleyResponseListener volleyListener){
        String url = expenseUrl + "update";
        try {
            StringPutRequest(url, DailyExpense.toJSONObject(expense), volleyListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //------Order Management------//
    public interface OrdersListener{
        void onResponse(ArrayList<Order> orders);
        void onError(String message);
    }
    public interface OrderListener{
        void onResponse(Order order);
        void onError(String message);
    }
    public interface OrderedProductsListener {
        void onResponse(ArrayList<OrderedProduct> orderedProducts);
        void onError(String message);
    }

    // Get Orders By VendorId
    public void GetOrdersByVendorId(int vendorId, final OrdersListener orderListener){
        String url = orderUrl + "get/vendorId/" + vendorId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Order> orders = new ArrayList<>();
                        for(int i = 0; i < response.length(); i++){
                            try {
                                orders.add(new Order(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        orderListener.onResponse(orders);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        orderListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Get Order By OrderId
    public void GetOrderByOrderId(int orderId, OrderListener orderListener){
        String url = orderUrl + "get/orderId/" + orderId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        Order order = null;
                        try {
                            order = new Order(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        orderListener.onResponse(order);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        orderListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    //Get Recent Order
    public void GetRecentOrder(int vendorId, String name, String contact, OrderListener orderListener){
        String url = orderUrl + "/get/recent/" + vendorId + "/" + name + "/" + contact;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        Order order = null;
                        try {
                            order = new Order(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        orderListener.onResponse(order);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        orderListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Create Order
    public void CreateOrder(Order order,final VolleyResponseListener volleyListener){
        String url = orderUrl + "create";
        try {
            StringPostRequest(url, Order.toJSONObject(order), volleyListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Create Ordered Product
    public void CreateOrderedProduct(OrderedProduct orderedProduct, VolleyResponseListener volleyListener){
        String url = baseUrl + "orderedproduct/create";
        try {
            StringPostRequest(url, OrderedProduct.toJSONObject(orderedProduct), volleyListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update Order
    public void UpdateOrder(Order order,final VolleyResponseListener volleyListener){
        String url = orderUrl + "/update";
        try {
            StringPutRequest(url, Order.toJSONObject(order), volleyListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get Ordered Products By orderId
    public void GetOrderedProductsByOrderId(int orderId, OrderedProductsListener orderedProductsListener) {
        String url = baseUrl + "orderedproduct/get/" + orderId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<OrderedProduct> orderedProducts = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                orderedProducts.add(new OrderedProduct(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        orderedProductsListener.onResponse(orderedProducts);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        orderedProductsListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    //------Sale Management------//
    public interface SalesListener{
        void onResponse(ArrayList<Sale> sales);
        void onError(String message);
    }
    // Create Sale
    public void CreateSale(Sale sale, int stockId, VolleyResponseListener volleyListener){
        String url = saleUrl + "create/" + stockId;
        try {
            StringPostRequest(url, Sale.toJSONObject(sale), volleyListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Get Sales For Today
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void GetDailySales(int vendorId, SalesListener salesListener){
        String url = saleUrl + "get/date/" + vendorId + "/" + LocalDateTime.now();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Sale> sales = new ArrayList<>();
                        for(int i = 0; i < response.length(); i++){
                            try {
                                sales.add(new Sale(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        salesListener.onResponse(sales);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        salesListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    //------Product Management------//
    public interface ProductsListener{
        void onResponse(ArrayList<Product> products);
        void onError(String message);
    }
    public interface ProductListener{
        void onResponse(Product product);
        void onError(String message);
    }
    // Get Products By VendorId
    public void GetProductsByVendorId(int vendorId, ProductsListener productsListener){
        String url = productUrl + "get/vendorId/" + vendorId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Product> products = new ArrayList<>();
                        try {
                            for(int i = 0; i < response.length(); i++){
                                products.add(new Product(response.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        productsListener.onResponse(products);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        productsListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

    // Get Products By ProductCode
    public void GetProductByProductCode(String productCode, ProductListener productListener){
        String url = productUrl + "get/productCode/" + productCode;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Product product = null;
                        try {
                            product = new Product(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        productListener.onResponse(product);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        productListener.onError(error.getMessage());
                    }
                });
        DataComSingleton.getInstance(context).addToRequestQueue(request);
    }

}
