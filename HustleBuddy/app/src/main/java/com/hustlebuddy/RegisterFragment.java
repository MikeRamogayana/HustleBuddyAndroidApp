package com.hustlebuddy;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.Vendor;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RegisterFragment extends Fragment {

    Service service;

    ProgressBar progressBar;
    TextView txtFirstName;
    TextView txtLastName;
    TextView txtCompanyName;
    TextView txtAddress;
    TextView txtContact;
    TextView txtEmail;
    TextView txtPassword;
    TextView txtConfirm;
    Button btnRegister;

    Context context;
    ViewPager viewPager;

    public RegisterFragment(Context context, ViewPager viewPager) {
        this.context = context;
        this.viewPager = viewPager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication_register, container, false);

        service = new Service(context);

        progressBar = view.findViewById(R.id.progress_register);
        txtFirstName = view.findViewById(R.id.txt_registerFirstName);
        txtLastName = view.findViewById(R.id.txt_registerLastName);
        txtCompanyName = view.findViewById(R.id.txt_registerCompanyName);
        txtAddress = view.findViewById(R.id.txt_registerAddress);
        txtContact = view.findViewById(R.id.txt_registerContactNumber);
        txtEmail = view.findViewById(R.id.txt_registerEmail);
        txtPassword = view.findViewById(R.id.txt_registerPassword);
        txtConfirm = view.findViewById(R.id.txt_registerConfirm);
        btnRegister = view.findViewById(R.id.btn_registerRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IsValid()) {
                    Vendor vendor = new Vendor(0, txtFirstName.getText().toString(), txtLastName.getText().toString(),
                            txtEmail.getText().toString(), txtConfirm.getText().toString(),
                            txtContact.getText().toString(), txtAddress.getText().toString(), 
                            txtCompanyName.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    service.Register(vendor, new Service.VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(context, "Could not create account!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResponse(Object response) {
                            Toast.makeText(context, "Account created successfully...", Toast.LENGTH_SHORT).show();
                            viewPager.setCurrentItem(0, true);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        return view;
    }

    private boolean IsValid() {
        if(ValidateFields(txtFirstName) && ValidateFields(txtLastName) && ValidateFields(txtCompanyName) &&
                ValidateFields(txtAddress) && ValidateFields(txtPassword) && ValidateFields(txtConfirm) &&
                ValidateFields(txtContact) && ValidateFields(txtEmail)) {
            return true;
        }
        return false;
    }

    private boolean ValidateFields(TextView textView) {
        if(textView.getText().toString().length() < 3) {
            textView.setBackgroundColor(Color.parseColor("#22FF0000"));
            SetOnEdit(textView);
            return false;
        }
        if(textView == txtConfirm && !textView.getText().toString().equals(txtPassword.getText().toString())) {
            textView.setBackgroundColor(Color.parseColor("#22FF0000"));
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

    private void SetOnEdit(TextView textView) {
        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackgroundColor(0);
                }
            }
        });
    }
}
