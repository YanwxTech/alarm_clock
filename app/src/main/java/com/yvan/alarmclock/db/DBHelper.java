package com.yvan.alarmclock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yvan on 2015/7/6.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "alarm_clock.db";
    private static final int DB_VERSION = 2;
    public static final String TABLE_NAME = "alarm_clock";
    private static final String SQL_CREATE =
            "create table alarm_clock " +
                    "(_id integer primary key autoincrement,alarm_id integer," +
                    "alarm_time text,alarm_days text,alarm_voice text," +
                    "alarm_content text,is_on integer,is_vibrated integer)";
    private static final String SQL_UPDATE =
            "alter table " + TABLE_NAME + " add alarm_content text";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_UPDATE);
    }
}
