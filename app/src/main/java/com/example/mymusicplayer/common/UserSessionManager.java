package com.example.mymusicplayer.common;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    public UserSessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // 保存用户登录状态
    public void setLoginStatus(int userId, String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // 获取用户登录状态
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 获取用户 ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, 0); // 默认值为 0，表示无效 ID
    }

    // 获取用户名
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // 清除登录状态
    public void logout() {
        editor.clear();
        editor.apply();
    }
}