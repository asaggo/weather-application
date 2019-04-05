package com.application.jessiejay.weatherapplicationv101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    public SQLiteDatabase db;

    public CityListDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE if not exists " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY, " +
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

    public boolean insertData(int id,String city, double latitude, double longitude,
                              String country, String admin){

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,city);
        contentValues.put(COL_3,latitude);
        contentValues.put(COL_4,longitude);
        contentValues.put(COL_5,country);
        contentValues.put(COL_6,admin);
        long result = db.insertWithOnConflict(TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return result == -1? false: true;
    }

    public Cursor searchData(String input){
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ COL_2 + " like '%"+input+"%'",null);
        return cursor;
    }

    public Cursor findCoordinates(String city, String admin, String country){
        Cursor cursor = db.rawQuery(
                "SELECT "+ COL_3 + "," + COL_4 +
                        " FROM " + TABLE_NAME +
                        " WHERE " + COL_2 + " = '" + city +
                        "' AND " + COL_6 + " = '" + admin +
                        "' AND " + COL_5 + " = '" + country + "'", null);
        return cursor;
    }
}
