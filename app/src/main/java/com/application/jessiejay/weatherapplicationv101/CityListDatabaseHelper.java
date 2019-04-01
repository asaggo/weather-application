package com.application.jessiejay.weatherapplicationv101;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CityListDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "locations.db";
    public static final String TABLE_NAME = "locations_table";
    public static final String COL_1 = "id";
    public static final String COL_2 = "city";
    public static final String COL_3 = "latitude";
    public static final String COL_4 = "longitude";
    public static final String COL_5 = "country";
    public static final String COL_6 = "admin_name";

    public CityListDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " REAL, " +
                COL_4 + " REAL, " +
                COL_5 + " TEXT, " +
                COL_6 + " TEXT)");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
