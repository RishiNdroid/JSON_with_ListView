package com.example.rndroid.json_with_listview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rndroid on 23/1/17.
 */

public class MyDatabase  {
    public MyHelper myHelper;
    public SQLiteDatabase sqLiteDatabase;
    private final String ID = " _id integer primary key ";
    private final String TABLE_NAME = " contacts ";
    private final String NAME_COLOUMN = "name text ";
    private final String EMAIL_COLOUMN = "email text ";
    private final String PHONE_COLOUMN = "phone text ";

    public MyDatabase(Context context) {
        myHelper = new MyHelper(context, "contacts.db", null, 1);
    }

    public void openDatabase(){
        sqLiteDatabase = myHelper.getWritableDatabase();
    }

    public void insertContact(String name, String email, String phone){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    public Cursor getContacts(){
        Cursor cursor = null;
        cursor = sqLiteDatabase.query(TABLE_NAME,null,null,null,null,null,null);
        return cursor;
    }

    public void close() {
        sqLiteDatabase.close();
    }

    public class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+ " ( "+ID+"," + NAME_COLOUMN+"," + EMAIL_COLOUMN+"," + PHONE_COLOUMN +" )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
