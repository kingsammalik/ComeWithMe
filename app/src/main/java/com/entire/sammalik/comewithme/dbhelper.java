package com.entire.sammalik.comewithme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Deepak on 16-03-2016.
 */
public class dbhelper extends SQLiteOpenHelper {

    public dbhelper(Context context, String name) {
        super(context, name, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists distance( dist varchar(20), lat varchar(20), lon varchar(20) ) ");
        initialze(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getdistance(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM distance", null);
        c.moveToFirst();
       String temp=c.getString(0);
        c.close();
        db.close();
      return temp;
    }

    public void setdistance(String temp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM distance", null);
        c.moveToFirst();
        String temp_dis=c.getString(0);
        c.close();
         double distance= Double.parseDouble(temp)+ Double.parseDouble(temp_dis);
        db.execSQL("update distance set dist=" + String.valueOf(distance));
        db.close();
    }

    void initialze(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT * FROM distance", null);
        if(c.getCount()>0){

        }
        else {
            db.execSQL("insert into distance(dist,lat,lon) values (0,0,0)");
        }
        /*db.execSQL("if exists (select * from distance)\n" +
                "begin\n" +
                "insert into distance(dist) values (0)\")\n" +
                "end\n" +
                "else\n" +
                "begin\n" +
                "\n" +
                "end");*/
        c.close();
        //db.close();
    }

    public void initialzedistance() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update distance set dist=0 ,lat=0,lon=0");
        db.close();
    }

    public void setLatLong(double lat,double lon){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update distance set lat="+ String.valueOf(lat)+", lon="+ String.valueOf(lon));
       // db.close();
    }

    public String[] getLatLong(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] a=new String[2];
        Cursor c = db.rawQuery("SELECT * FROM distance", null);
        c.moveToFirst();
        a[0]=c.getString(1);
        a[1]=c.getString(2);
        c.close();
       // db.close();
        return a;
    }
}
