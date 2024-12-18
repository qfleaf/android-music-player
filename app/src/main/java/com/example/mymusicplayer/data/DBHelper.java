package com.example.mymusicplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // 数据库名称与版本号
    private static final String DATABASE_NAME = "music_player.db";
    private static final int DATABASE_VERSION = 4; // 数据库版本更新

    // 通用字段-id
    public static final String COLUMN_ID = "id";

    // 1、用户表
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_GENDER = "gender";

    // 2、播放列表表
    public static final String TABLE_PLAYLIST = "playlist";
    public static final String COLUMN_SONG_TITLE = "song_title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_RANK = "rank";
    public static final String COLUMN_RESOURCE_ID = "resource_id";
    public static final String COLUMN_COVER_ID = "cover_id";
    public static final String COLUMN_USER_ID = "user_id"; // 新增用户ID

    // 创建用户表的 SQL 语句
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, "
            + COLUMN_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_GENDER + " TEXT NOT NULL)";

    // 创建播放列表表的 SQL 语句
    private static final String CREATE_TABLE_PLAYLIST = "CREATE TABLE " + TABLE_PLAYLIST + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_SONG_TITLE + " TEXT, "
            + COLUMN_ARTIST + " TEXT, "
            + COLUMN_RANK + " INTEGER, "
            + COLUMN_RESOURCE_ID + " INTEGER, "
            + COLUMN_COVER_ID + " INTEGER, "
            + COLUMN_USER_ID + " INTEGER DEFAULT 0, " // 外键关联用户ID，默认为0（公共播放列表）
            + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "))";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER); // 创建用户表
        db.execSQL(CREATE_TABLE_PLAYLIST); // 创建播放列表表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级时保留数据
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_PLAYLIST + " ADD COLUMN " + COLUMN_USER_ID + " INTEGER DEFAULT 0");
            db.execSQL(CREATE_TABLE_USER); // 确保用户表被创建
        }
    }
}