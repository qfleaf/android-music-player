package com.example.mymusicplayer.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mymusicplayer.data.DBHelper;

public class UserDAO {

    private SQLiteDatabase db;

    public UserDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // 用户注册
    public long registerUser(String username, String password, String gender) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USERNAME, username);
        values.put(DBHelper.COLUMN_PASSWORD, password);
        values.put(DBHelper.COLUMN_GENDER, gender);
        return db.insert(DBHelper.TABLE_USER, null, values);
    }

    // 验证用户登录
    @SuppressLint("Range")
    public int loginUser(String username, String password) {
        String selection = DBHelper.COLUMN_USERNAME + "=? AND " + DBHelper.COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                DBHelper.TABLE_USER,
                new String[]{DBHelper.COLUMN_ID}, // 查询用户ID
                selection,
                selectionArgs,
                null, null, null
        );

        int userId = 0; // -1 表示登录失败
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
            cursor.close();
        }

        return userId;
    }

    // 检查用户名是否已存在
    public boolean isUsernameExists(String username) {
        String selection = DBHelper.COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                DBHelper.TABLE_USER,
                new String[]{DBHelper.COLUMN_ID},
                selection,
                selectionArgs,
                null, null, null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    // 关闭数据库连接
    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}