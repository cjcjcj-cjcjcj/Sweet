package com.example.bigwork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword, etAddress;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListeners();
        setupTextWatcher();
        setDefaultValues();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etAddress = findViewById(R.id.et_address);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void setDefaultValues() {
        // 设置默认用户名和密码
        etUsername.setText("hh");
        etPassword.setText("123456");
        etAddress.setText("北京市朝阳区xxx街道xxx号");
        // 自动启用登录按钮
        checkLoginButtonState();
    }

    private void setListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (login(username, password, address)) {
                // 保存用户信息
                SharedPreferences preferences = getSharedPreferences("user_info", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", username);
                editor.putString("address", address);
                editor.putBoolean("is_logged_in", true);
                editor.apply();

                Toast.makeText(LoginActivity.this, "登录成功！欢迎 " + username, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etAddress.addTextChangedListener(textWatcher);
    }

    private void checkLoginButtonState() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        boolean isEnabled = !username.isEmpty() && !password.isEmpty() && !address.isEmpty();
        btnLogin.setEnabled(isEnabled);
        if (isEnabled) {
            btnLogin.setBackgroundResource(R.drawable.btn_login_enabled);
        } else {
            btnLogin.setBackgroundResource(R.drawable.btn_login_disabled);
        }
    }

    private boolean login(String username, String password, String address) {
        return !username.isEmpty() && !password.isEmpty() && !address.isEmpty();
    }
}