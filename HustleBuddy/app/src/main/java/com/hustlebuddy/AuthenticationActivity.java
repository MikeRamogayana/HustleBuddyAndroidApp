package com.hustlebuddy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hustlebuddy.adapter.FragmentTabAdapter;
import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Vendor;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AuthenticationActivity extends AppCompatActivity {

    static FragmentTabAdapter adapter;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        tabLayout = findViewById(R.id.auth_tabs);
        viewPager = findViewById(R.id.auth_view_pager);

        SetupViewPager();
    }

    private void SetupViewPager() {
        adapter = new FragmentTabAdapter(getSupportFragmentManager());

        loginFragment = new LoginFragment(this);
        registerFragment = new RegisterFragment(this, viewPager);

        adapter.addFragment(loginFragment, "Login");
        adapter.addFragment(registerFragment, "Register");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_login_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_create_24);
    }
}