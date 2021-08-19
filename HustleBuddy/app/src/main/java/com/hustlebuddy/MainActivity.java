package com.hustlebuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hustlebuddy.adapter.FragmentTabAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.DailyStock;
import com.hustlebuddy.model.Product;
import com.hustlebuddy.model.Sale;
import com.hustlebuddy.model.TradingStock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static FragmentTabAdapter adapter;

    SaleFragment saleFragment;
    OrderFragment orderFragment;
    ExpenseFragment expenseFragment;

    TabLayout tabLayout;
    ViewPager viewPager;
    TextView textTitle;

    int vendorId = 1;

    Service service;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        service = new Service(this);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        textTitle = findViewById(R.id.title);

        if(intent.getIntExtra("fragment", -1) == 2) {
            viewPager.setCurrentItem(2, true);
        }

        setupViewPager();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupViewPager() {
        adapter = new FragmentTabAdapter(getSupportFragmentManager());

        saleFragment = new SaleFragment(this, vendorId);
        orderFragment = new OrderFragment(this, vendorId, saleFragment);
        expenseFragment = new ExpenseFragment(this, vendorId);

        adapter.addFragment(saleFragment, "Sales");
        adapter.addFragment(orderFragment, "Orders");
        adapter.addFragment(expenseFragment, "Expenses");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_point_of_sale_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_access_alarm_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_attach_money_24);
    }
}