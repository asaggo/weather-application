package com.application.jessiejay.weatherapplicationv101;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


//delete this class after finsihing CityListDatabaseHelper.java class
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "weather.db";
    public static final String TABLE_NAME = "user_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "username";
    public static final String COL_3 = "password";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
