package com.example.sony.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


    public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Reminder.db";
    public static final String TABLE_NAME = "reminderTb";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "REMINDER";
    public static final String COL_3 = "DATE";
    public static final String COL_4 = "TIME";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,REMINDER TEXT,DATE TEXT,TIME INTEGER)");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }





    public boolean insertData(String reminder,String date,String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,reminder);
        contentValues.put(COL_3,date);
        contentValues.put(COL_4,time);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }



    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }





    public boolean updateData(String id,String reminder,String date,String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,reminder);
        contentValues.put(COL_3,date);
        contentValues.put(COL_4,time);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }





    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}

