package com.example.mymusicplayer.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.common.UserSessionManager;
import com.example.mymusicplayer.data.dao.UserDAO;
import com.example.mymusicplayer.service.MusicService;
import com.google.android.material.textfield.TextInputEditText;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final int REGISTER_REQUEST_CODE = 100;
    //    private static final ApiClient apiClient = new ApiClient();
    private UserDAO userDAO;
    private UserSessionManager sessionManager;

    private Button btnLogin;
    private TextView tvRegister;
    private ImageView ivBack;

    TextInputEditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化组件
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        ivBack = findViewById(R.id.iv_back);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        // 用户服务
        userDAO = new UserDAO(this);
        sessionManager = new UserSessionManager(getApplicationContext());

        // 返回
        ivBack.setOnClickListener(v -> {
            finish();
        });

        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            // 这里进行登录验证逻辑
            int id = userDAO.loginUser(username, password);
            if (id != 0) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                // 存储当前用户信息
                sessionManager.setLoginStatus(id, username);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 注册提示点击事件
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("username", etUsername.getText().toString());
            startActivityForResult(intent, REGISTER_REQUEST_CODE);
        });
    }

    // 处理注册页返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_REQUEST_CODE && resultCode == RESULT_OK) {
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            etUsername.setText(username);
            etPassword.setText(password);
        }
    }
}