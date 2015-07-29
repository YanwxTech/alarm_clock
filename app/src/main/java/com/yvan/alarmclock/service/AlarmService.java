package com.yvan.alarmclock.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yvan.alarmclock.activity.AlarmActivity;
import com.yvan.alarmclock.bean.AlarmClockItem;
import com.yvan.alarmclock.db.DBDao;
import com.yvan.alarmclock.db.DBDaoImp;
import com.yvan.alarmclock.utils.TimeUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlarmService extends Service {
    public static final String START_ACTION =
            "com.yvan.alarmclock.service.action.RESTART_SERVICE";
    private static final int IS_SHOULD_ALARM = 0X100;
    private static final int AFTER_ALARM = 0x101;
    private static DBDao<AlarmClockItem> dbDao;
    private static List<AlarmClockItem> items;
    private static AlarmClockItem afterItem;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IS_SHOULD_ALARM:
                    afterItem = items.get(0);
                    startAlarmAty();
                    break;
                case AFTER_ALARM:
                    startAlarmAty();
                    break;
            }
        }
    };
    private SharedPreferences spf;

    private void startAlarmAty() {
        //仅响一次，关闭开启状态
        if (afterItem.getAlarm_day().equals(TimeUtil.ONLY_ONCE)) {
            dbDao.update(afterItem.getAlarm_id(), false);
        }
        Intent intent = new Intent(AlarmService.this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String ring_time = spf.getString("alarm_all_time", "20分钟");
        String keyDownOperation = spf.getString("alarm_key_down", "10分钟");
        boolean is_silence_ring = spf.getBoolean("alarm_at_silence", true);
        intent.putExtra("ringtone_uri", afterItem.getVoicePath());
        intent.putExtra("is_vibrated", afterItem.isVibrated());
        intent.putExtra("alarm_content", afterItem.getAlarm_content());
        intent.putExtra("ring_time", ring_time);
        intent.putExtra("alarm_key_down", keyDownOperation);
        intent.putExtra("is_silence_ring", is_silence_ring);
        startActivity(intent);
    }

    public static volatile int minutes = -1;
    public static int alarmAfterMinutes = 10;
    private static Thread alarmThread;

    public AlarmService() {
    }

    @Override
    public void onCreate() {
        spf = PreferenceManager.getDefaultSharedPreferences(AlarmService.this);
        dbDao = new DBDaoImp(AlarmService.this);
        sortListByNearTime();
        alarmThread = new Thread(new AlarmThread());
        alarmThread.start();
    }

//    private AlarmClockItem getNearItem() {
//        int size = items.size();
//        int[] nearTimes = new int[size];
//        for (int i = 0; i < size; i++) {
//            String time = items.get(i).getAlarm_time();
//            String days = items.get(i).getAlarm_day();
//            nearTimes[i] = TimeUtil.getAfterMinutes(time, days);
//
//        }
//
//        return null;
//    }

    public static void sortListByNearTime() {
        items = dbDao.query(true);
        Collections.sort(items, new Comparator<AlarmClockItem>() {
            @Override
            public int compare(AlarmClockItem lhs, AlarmClockItem rhs) {
                int lm = TimeUtil.getAfterMinutes(lhs.getAlarm_time(), lhs.getAlarm_day());
                int rm = TimeUtil.getAfterMinutes(rhs.getAlarm_time(), rhs.getAlarm_day());
                return lm - rm;
            }
        });
        if (items != null && items.size() > 0) {
            minutes = TimeUtil.getAfterMinutes(items.get(0).getAlarm_time(), items.get(0).getAlarm_day());
            Log.i("resetMinutes", minutes + "");
        } else {
            minutes = -1;
        }
        if (alarmThread != null) {
            alarmThread.interrupt();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(START_ACTION);
        sendBroadcast(intent);
    }

    public class MyBinder extends Binder {
        public void alarmAfter() {
            String interval = spf.getString("alarm_interval", "10分钟");
            try {
                alarmAfterMinutes = Integer.parseInt(interval.substring(0, interval.indexOf("分")));
            } catch (NumberFormatException e) {
                alarmAfterMinutes = 10;
            }
            new Thread(new AfterThread()).start();

        }
    }

    class AfterThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(alarmAfterMinutes * 60 * 1000);
                mHandler.sendEmptyMessage(AFTER_ALARM);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class AlarmThread implements Runnable {

        @Override
        public void run() {
            if (items != null && items.size() > 0) {
                minutes = TimeUtil.getAfterMinutes(items.get(0).getAlarm_time(), items.get(0).getAlarm_day());
                Log.i("minutes", minutes + "");
                while (true) {
                    if (minutes > 80) {
                        try {
                            Thread.sleep(60 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (items != null && items.size() > 0)
                            minutes = TimeUtil.getAfterMinutes(items.get(0).getAlarm_time(), items.get(0).getAlarm_day());
                    } else if (minutes > 30) {
                        try {
                            Thread.sleep(20 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (items != null && items.size() > 0)
                            minutes = TimeUtil.getAfterMinutes(items.get(0).getAlarm_time(), items.get(0).getAlarm_day());
                    } else if (minutes > 5) {
                        try {
                            Thread.sleep(5 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        minutes -= 5;
                    } else if (minutes > 0) {
                        try {
                            Thread.sleep(60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        minutes--;
                    } else if (minutes == 0) {
                        minutes = -1;
                        mHandler.sendEmptyMessage(IS_SHOULD_ALARM);
                        try {
                            Thread.sleep(60 * 1000);
                            sortListByNearTime();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }
}
