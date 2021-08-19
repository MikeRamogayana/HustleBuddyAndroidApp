package com.hustlebuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hustlebuddy.adapter.SaleProductAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.Sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SaleFragment extends Fragment {

    Service service;
    View view;

    Button btnCreateSale;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SaleProductAdapter saleProductAdapter;
    FloatingActionButton btnSaleRefresh;

    List<Sale> saleList = new ArrayList<>();

    ColumnChartView barGraph;
    ColumnChartData columnChartData;
    int default_data = 0;
    int subcolumns_data = 1;
    int stacked_data = 2;
    int negative_sbcolumns_data = 3;
    int negative_stacked_data = 4;
    
    boolean hasaxis = true;
    boolean hasaxisnames = true;
    boolean haslabels = false;
    boolean isHaslabelsforselected = false;
    int dataType = default_data;

    int vendorId;
    boolean inflated = false;
    Context context;

    public SaleFragment(Context context, int vendorId) {
        this.context = context;
        this.vendorId = vendorId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_sale, container, false);

        service = new Service(view.getContext());

        btnSaleRefresh = view.findViewById(R.id.btn_recentSaleRefresh);
        btnCreateSale = view.findViewById(R.id.btn_createSale);
        barGraph = view.findViewById(R.id.chart_saleBarGraph);
        recyclerView = view.findViewById(R.id.recycler_recentSales);

        btnSaleRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshSaleData();
            }
        });

        btnCreateSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barGraph.setVisibility(View.GONE);
                Intent intent = new Intent(view.getContext(), AddSaleActivity.class);
                intent.putExtra("vendorId", vendorId);
                startActivity(intent);
            }
        });

        barGraph.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {

            }

            @Override
            public void onValueDeselected() {

            }
        });

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        saleProductAdapter = new SaleProductAdapter();
        recyclerView.setAdapter(saleProductAdapter);

        if(!inflated) {
            RefreshSaleData();
            inflated = true;
        }

        return view;
    }

    private void RefreshSaleData() {
        service.GetDailySales(vendorId, new Service.SalesListener() {
            @Override
            public void onResponse(ArrayList<Sale> sales) {
                saleProductAdapter.getProductList().clear();
                saleProductAdapter.getQuantityList().clear();
                saleProductAdapter.notifyDataSetChanged();
                saleList.clear();
                saleList.addAll(sales);
                int i;
                for (Sale sale: sales) {
                    service.GetProductByProductCode(sale.getProductCode(), new com.hustlebuddy.controller.Service.ProductListener() {
                        @Override
                        public void onResponse(Product product) {
                            saleProductAdapter.getProductList().add(product);
                            saleProductAdapter.getQuantityList().add(sale.getQuantity());
                            saleProductAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(view.getContext(), "Could not load product sales!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                DrawBarChart(saleList);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(view.getContext(), "Could not load recent sales!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DrawBarChart(List<Sale> sales) {

        if(!sales.isEmpty()) {

            sales.sort(new Comparator<Sale>() {
                @Override
                public int compare(Sale o1, Sale o2) {
                    return o1.getProductCode().compareTo(o2.getProductCode());
                }
            });

            List<String> xAxisData = new ArrayList<>();
            List<Integer> yAxisData = new ArrayList<>();

            int total = 0;
            int i;
            for(i = 0; i < sales.size() - 1; i++) {
                if(sales.get(i).getProductCode().equals(sales.get(i + 1).getProductCode())) {
                    total += sales.get(i).getQuantity();
                } else {
                    xAxisData.add(sales.get(i).getProductCode());
                    yAxisData.add(total + sales.get(i).getQuantity());
                    total = 0;
                }
            }
            total += sales.get(i).getQuantity();
            if(xAxisData.contains(sales.get(i).getProductCode())) {
                yAxisData.set(yAxisData.size() - 1, total);
            } else {
                xAxisData.add(sales.get(i).getProductCode());
                yAxisData.add(total);
            }
            
            List<Column> columns = new ArrayList<>();
            List<SubcolumnValue> values = new ArrayList<>();
            List<AxisValue> axisValues = new ArrayList<>();

            for(i = 0; i < xAxisData.size(); i++) {
                axisValues.add(new AxisValue(i).setLabel(xAxisData.get(i)));

                SubcolumnValue subcolumnValue = new SubcolumnValue(yAxisData.get(i), ChartUtils.pickColor());
                subcolumnValue.setLabel(xAxisData.get(i));
                values.add(subcolumnValue);

                Column column = new Column(Arrays.asList(subcolumnValue));
                column.setHasLabelsOnlyForSelected(true);
                columns.add(column);
            }

            columnChartData = new ColumnChartData(columns);
            Axis xAxis = new Axis();
            xAxis.setValues(axisValues);
            Axis yAxis = new Axis().setHasLines(true);

            xAxis.setName("Products");
            yAxis.setName("Sales");
            columnChartData.setAxisXBottom(xAxis);
            columnChartData.setAxisYLeft(yAxis);

            barGraph.setVisibility(View.VISIBLE);
            barGraph.setColumnChartData(columnChartData);
            columnChartData.finish();
            
        }
    }
}
