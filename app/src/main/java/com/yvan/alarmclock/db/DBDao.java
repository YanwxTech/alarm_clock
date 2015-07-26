package com.yvan.alarmclock.db;

import java.util.List;

/**
 * Created by Yvan on 2015/7/6.
 */
public interface DBDao <T>{
    void insert(T t);
    void delete(int id);
    List<T> queryAll();
    T query(int id);
    void update(T t);
    void update(int alarm_id,boolean isOn);
}
