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
            "(alarm_id,alarm_time,alarm_days,alarm_voice,alarm_content,is_on,is_vibrated) " +
            " values(?,?,?,?,?,?,?)";
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
                        item.getVoicePath(), item.getAlarm_content(), boolToInt(item.isOn()), boolToInt(item.isVibrated())});
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
        String sql = "select * from alarm_clock";
        return queryBySQL(sql, null);
    }

    @Override
    public List<AlarmClockItem> query(boolean is_on) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        List<AlarmClockItem> items = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from alarm_clock where is_on=?", new String[]{boolToInt(is_on) + ""});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AlarmClockItem item = new AlarmClockItem();
                item.setAlarm_id(cursor.getInt(cursor.getColumnIndex("alarm_id")));
                item.setAlarm_time(cursor.getString(cursor.getColumnIndex("alarm_time")));
                item.setAlarm_day(cursor.getString(cursor.getColumnIndex("alarm_days")));
                item.setVoicePath(cursor.getString(cursor.getColumnIndex("alarm_voice")));
                item.setAlarm_content(cursor.getString(cursor.getColumnIndex("alarm_content")));
                item.setIsOn(is_on);
                item.setIsVibrated(intToBool(cursor.getInt(cursor.getColumnIndex("is_vibrated"))));
                items.add(item);
            }
            cursor.close();
        }
        return items;
    }

    @Override
    public void update(AlarmClockItem item) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("alarm_time", item.getAlarm_time());
        values.put("alarm_days", item.getAlarm_day());
        values.put("alarm_voice", item.getVoicePath());
        values.put("alarm_content", item.getAlarm_content());
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

    @Override
    public List<AlarmClockItem> queryBySQL(String s, String[] selectionArgs) {
        List<AlarmClockItem> items = new ArrayList<>();
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(s, selectionArgs);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AlarmClockItem item = new AlarmClockItem();
                item.setAlarm_id(cursor.getInt(cursor.getColumnIndex("alarm_id")));
                item.setAlarm_time(cursor.getString(cursor.getColumnIndex("alarm_time")));
                item.setAlarm_day(cursor.getString(cursor.getColumnIndex("alarm_days")));
                item.setVoicePath(cursor.getString(cursor.getColumnIndex("alarm_voice")));
                item.setAlarm_content(cursor.getString(cursor.getColumnIndex("alarm_content")));
                item.setIsOn(intToBool(cursor.getInt(cursor.getColumnIndex("is_on"))));
                item.setIsVibrated(intToBool(cursor.getInt(cursor.getColumnIndex("is_vibrated"))));
                items.add(item);
            }
            cursor.close();
        }
        return items;
    }

    private int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    private boolean intToBool(int i) {
        return i == 1;
    }

}
