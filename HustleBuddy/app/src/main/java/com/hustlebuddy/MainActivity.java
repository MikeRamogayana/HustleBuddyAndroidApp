package com.hustlebuddy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hustlebuddy.adapter.FragmentTabAdapter;
import com.hustlebuddy.controller.Service;

public class MainActivity extends AppCompatActivity {

    static FragmentTabAdapter adapter;

    SaleFragment saleFragment;
    OrderFragment orderFragment;
    ExpenseFragment expenseFragment;

    TabLayout tabLayout;
    ViewPager viewPager;
    TextView textTitle;

    Intent intent;

    int vendorId;
    int count = 0;

    Service service;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();
        vendorId = intent.getIntExtra("vendorId", 0);

        service = new Service(this);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        textTitle = findViewById(R.id.title);

        setupViewPager();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupViewPager() {
        adapter = new FragmentTabAdapter(getSupportFragmentManager());

        saleFragment = new SaleFragment(this, vendorId);
        orderFragment = new OrderFragment(this, vendorId);
        expenseFragment = new ExpenseFragment(this, vendorId);

        adapter.addFragment(orderFragment, "Orders");
        adapter.addFragment(saleFragment, "Sales");
        adapter.addFragment(expenseFragment, "Expenses");

        viewPager.setAdapter(adapter);
        if(intent.getIntExtra("fragment", -1) == 0) {
            viewPager.setCurrentItem(0, true);
        } else {
            viewPager.setCurrentItem(1, true);
        }
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_point_of_sale_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_access_alarm_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_attach_money_24);

    }

    @Override
    public void onBackPressed() {
        if(count == 0) {
            Toast.makeText(this, "Double click to exit app...", Toast.LENGTH_SHORT).show();
        }
        count++;
        if(count > 1) {
            super.onBackPressed();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                count = 0;
            }
        }, 1500);
    }
}