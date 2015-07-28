package com.yvan.alarmclock.db;

import com.yvan.alarmclock.bean.AlarmClockItem;

import java.util.List;

/**
 * Created by Yvan on 2015/7/6.
 */
public interface DBDao <T>{
    void insert(T t);
    void delete(int id);
    List<T> queryAll();
    List<T> query(boolean is_on);
    void update(T t);
    void update(int alarm_id,boolean isOn);

    List<T> queryBySQL(String sql,String[] selectionArgs);
}
