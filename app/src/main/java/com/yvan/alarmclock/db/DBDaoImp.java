package com.yvan.alarmclock.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yvan.alarmclock.bean.AlarmClockItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yvan on 2015/7/6.
 */
public class DBDaoImp implements DBDao<AlarmClockItem> {
    private static final String INSERT_SQL = "insert into alarm_clock" +
            "(alarm_id,alarm_time,alarm_days,alarm_voice,is_on,is_vibrated) " +
            " values(?,?,?,?,?,?)";
    private Context context;

    public DBDaoImp(Context context) {
        this.context = context;
    }

    @Override
    public void insert(AlarmClockItem item) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(INSERT_SQL, new Object[]
                {item.getAlarm_id(), item.getAlarm_time(), item.getAlarm_day(),
                        item.getVoicePath(), boolToInt(item.isOn()), boolToInt(item.isVibrated())});
        db.close();
    }

    @Override
    public void delete(int alarm_id) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("alarm_clock", "alarm_id=?", new String[]{alarm_id + ""});
        db.close();
    }

    @Override
    public List<AlarmClockItem> queryAll() {
        List<AlarmClockItem> items = new ArrayList<>();
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from alarm_clock", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AlarmClockItem item = new AlarmClockItem();
                item.setAlarm_id(cursor.getInt(cursor.getColumnIndex("alarm_id")));
                item.setAlarm_time(cursor.getString(cursor.getColumnIndex("alarm_time")));
                item.setAlarm_day(cursor.getString(cursor.getColumnIndex("alarm_days")));
                item.setVoicePath(cursor.getString(cursor.getColumnIndex("alarm_voice")));
                item.setIsOn(intToBool(cursor.getInt(cursor.getColumnIndex("is_on"))));
                item.setIsVibrated(intToBool(cursor.getInt(cursor.getColumnIndex("is_vibrated"))));
                items.add(item);
            }
            cursor.close();
        }
        return items;
    }

    @Override
    public AlarmClockItem query(int alarm_id) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        AlarmClockItem item = new AlarmClockItem();
        item.setAlarm_id(alarm_id);
        Cursor cursor=db.rawQuery("selet * from alarm_clock where alarm_id=?", new String[]{alarm_id + ""});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                item.setAlarm_time(cursor.getString(cursor.getColumnIndex("alarm_time")));
                item.setAlarm_day(cursor.getString(cursor.getColumnIndex("alarm_days")));
                item.setVoicePath(cursor.getString(cursor.getColumnIndex("alarm_voice")));
                item.setIsOn(intToBool(cursor.getInt(cursor.getColumnIndex("is_on"))));
                item.setIsVibrated(intToBool(cursor.getInt(cursor.getColumnIndex("is_vibrated"))));
            }
            cursor.close();
        }
        return item;
    }

    @Override
    public void update(AlarmClockItem item) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("alarm_time", item.getAlarm_time());
        values.put("alarm_days", item.getAlarm_day());
        values.put("alarm_voice", item.getVoicePath());
        values.put("is_on", boolToInt(item.isOn()));
        values.put("is_vibrated", boolToInt(item.isVibrated()));
        db.update("alarm_clock", values, "alarm_id=?", new String[]{item.getAlarm_id() + ""});
        db.close();
    }

    @Override
    public void update(int alarm_id, boolean isOn) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_on", boolToInt(isOn));
        db.update("alarm_clock", values, "alarm_id=?", new String[]{alarm_id + ""});
        db.close();
    }

    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    private boolean intToBool(int i) {
        return i == 1;
    }

}
