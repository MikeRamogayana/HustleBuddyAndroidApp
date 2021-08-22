package com.hustlebuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.hustlebuddy.controller.Service;
import com.hustlebuddy.model.EmailMessage;
import com.hustlebuddy.model.Vendor;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LoginFragment extends Fragment {

    Service service;

    ProgressBar progressBar;
    TableLayout tableLogin;
    EditText txtEmail;
    EditText txtPassword;
    Button btnLogin;
    Button btnReset;

    TableLayout tableResetSendCode;
    EditText txtResetEmail;
    Button btnSendCode;

    TableLayout tableResetPassword;
    EditText txtResetCode;
    EditText txtNewPassword;
    EditText txtConfirmPassword;
    Button btnResetPassword;

    Button btnCancel;

    String email, password;
    Vendor vendor = new Vendor();
    Context context;

    int code;

    public LoginFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication_login, container, false);
        service = new Service(context);

        progressBar = view.findViewById(R.id.progress_login);
        tableLogin = view.findViewById(R.id.table_login);
        txtEmail = view.findViewById(R.id.txt_loginEmail);
        txtPassword = view.findViewById(R.id.txt_loginPassword);
        btnLogin = view.findViewById(R.id.btn_loginLogin);
        btnReset = view.findViewById(R.id.btn_loginReset);

        tableResetSendCode = view.findViewById(R.id.table_resetSendCode);
        tableResetSendCode.setVisibility(View.INVISIBLE);
        txtResetEmail = view.findViewById(R.id.txt_resetEmail);
        btnSendCode = view.findViewById(R.id.btn_loginSendCode);

        tableResetPassword = view.findViewById(R.id.table_resetPassword);
        txtResetCode = view.findViewById(R.id.txt_resetCode);
        txtNewPassword = view.findViewById(R.id.txt_resetPassword);
        txtConfirmPassword = view.findViewById(R.id.txt_resetConfirmPassword);
        btnResetPassword = view.findViewById(R.id.btn_resetPassword);

        btnCancel = view.findViewById(R.id.btn_loginCancel);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email = txtEmail.getText().toString();
                    password = txtPassword.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    service.Login(email, password, new Service.VendorResponseListener() {
                        @Override
                        public void onResponse(Vendor vendor) {
                            LoginFragment.this.vendor = vendor;
                            Toast.makeText(context, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            intent.putExtra("vendorId", vendor.getVendorId());
                            intent.putExtra("title", vendor.getCompanyName());
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(context, "Login failed check your fields!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(context, "Login form invalid, check your fields!!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLogin.setVisibility(View.GONE);
                tableResetSendCode.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }
        });

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code = GenerateCode();
                    if(txtResetEmail.getText().toString().contains("@")) {
                        email = txtResetEmail.getText().toString();
                        EmailMessage emailMessage = new EmailMessage(email, "Password Reset Code", String.valueOf(code));
                        progressBar.setVisibility(View.VISIBLE);
                        service.SendEmail(emailMessage, new Service.VolleyResponseListener() {
                            @Override
                            public void onError(String message) {
                                Toast.makeText(context, "Could not sent email!!!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResponse(Object response) {
                                Toast.makeText(context, "Email sent successfully...", Toast.LENGTH_SHORT).show();
                                tableResetSendCode.setVisibility(View.GONE);
                                tableResetPassword.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        Toast.makeText(context, "Please enter valid email address!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Please enter email address!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ResetFieldsValid()) {
                    progressBar.setVisibility(View.VISIBLE);
                    service.GetVendorByEmail(email, new Service.VendorResponseListener() {
                        @Override
                        public void onResponse(Vendor vendor) {
                            service.ResetPassword(vendor.getVendorId(), vendor.getEmail(), txtConfirmPassword.getText().toString(),
                                    new Service.VolleyResponseListener() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(context, "Could not reset password!!!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onResponse(Object response) {
                                            Toast.makeText(context, "Password reset successfully...", Toast.LENGTH_SHORT).show();
                                            btnCancel.setVisibility(View.GONE);
                                            tableResetPassword.setVisibility(View.GONE);
                                            tableLogin.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(context, "Account not found!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setVisibility(View.GONE);
                tableResetPassword.setVisibility(View.GONE);
                tableResetSendCode.setVisibility(View.GONE);
                tableLogin.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private boolean ResetFieldsValid() {
        if(ValidateField(txtResetCode) && ValidateField(txtNewPassword) && ValidateField(txtConfirmPassword)) {
            return true;
        }
        return false;
    }

    private boolean ValidateField(TextView textView) {
        if(textView.getText().toString().length() < 5) {
            textView.setBackgroundColor(Color.parseColor("#22FF0000"));
            SetOnEdit(textView);
            return false;
        }
        if(textView == txtConfirmPassword && !textView.getText().toString().equals(txtNewPassword.getText().toString())) {
            textView.setBackgroundColor(Color.parseColor("#22FF0000"));
            SetOnEdit(textView);
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(textView == txtResetCode && Integer.parseInt(textView.getText().toString()) != code) {
            textView.setBackgroundColor(Color.parseColor("#22FF0000"));
            SetOnEdit(textView);
            Toast.makeText(context, "Invalid reset code entered!!!", Toast.LENGTH_SHORT).show();
            return false;
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

    private int GenerateCode() {
        int min = 10000;
        int max = 99999;
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

}
