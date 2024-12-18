package com.example.mymusicplayer.activity;

import android.os.Bundle;

import com.example.mymusicplayer.R;
import com.example.mymusicplayer.data.dao.UserDAO;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    //    private static final ApiClient apiClient = new ApiClient();
    private UserDAO userDAO;

    private EditText etUsername, etPassword, etConfirmPassword;
    private RadioGroup rgGender;
    private CheckBox cbTerms;
    private Button btnRegister;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 获取传递的用户名
        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.confirm_register_password);
        rgGender = findViewById(R.id.rg_gender);
        cbTerms = findViewById(R.id.cb_terms);
        btnRegister = findViewById(R.id.btn_register);
        ivBack = findViewById(R.id.iv_back);
        userDAO = new UserDAO(this);

        initView();

    }

    private void initView() {
        // 返回登陆页
        ivBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String passedUsername = intent.getStringExtra("username");
        if (passedUsername != null) {
            etUsername.setText(passedUsername);
        }

        // 注册按钮监听
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            int genderId = rgGender.getCheckedRadioButtonId();
            boolean termsAccepted = cbTerms.isChecked();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!termsAccepted) {
                Toast.makeText(RegisterActivity.this, "请同意用户协议", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!confirmPassword.equals(password)) {
                Toast.makeText(RegisterActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = (genderId == R.id.rb_male) ? "男" : "女";

            if (userDAO.isUsernameExists(username)) {
                Toast.makeText(RegisterActivity.this, "用户名已被使用", Toast.LENGTH_SHORT).show();
            } else {
                userDAO.registerUser(username, password, gender);
                // 返回数据到登录页
                Intent resultIntent = new Intent();
                resultIntent.putExtra("username", username);
                resultIntent.putExtra("password", password);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}