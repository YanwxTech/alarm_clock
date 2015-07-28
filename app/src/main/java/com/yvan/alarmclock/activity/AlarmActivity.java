package com.yvan.alarmclock.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.yvan.alarmclock.R;
import com.yvan.alarmclock.service.AlarmService;
import com.yvan.alarmclock.utils.AlarmPlay;

public class AlarmActivity extends Activity {
    private static final int IS_SHOULD_END_RINGTONE = 0x101;
    private long startTime;
    private AlarmPlay ap;
    private AlarmService.MyBinder mBinder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (AlarmService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Vibrator vibrator;
    private String ring_time;
    private String keyDownOperation;
    private String alarm_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        findViewById(R.id.tv_alarm_after).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmAfter();
            }
        });

        Intent intent = new Intent(this, AlarmService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        Intent i = getIntent();
        String url = i.getStringExtra("ringtone_uri");
        if (url != null) {
            Uri uri = Uri.parse(url);
            ap = new AlarmPlay(this, uri);
            ap.startAlarm();
            startTime = System.currentTimeMillis();
            // new Thread(new TimeEndThread()).start();
        }

        animForTV();
        boolean isVibrated = i.getBooleanExtra("is_vibrated", false);
        if (isVibrated) {
            startVibrate();
        }

        alarm_content = i.getStringExtra("alarm_content");
        if (alarm_content != null) {
            ((TextView) findViewById(R.id.tv_show_content)).setText(alarm_content);
        }
        ring_time = i.getStringExtra("ring_time");
        keyDownOperation = i.getStringExtra("alarm_key_down");
    }


    private void startVibrate() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(
                new long[]{10, 50, 80, 300, 100, 400}, 0);
    }

    private void animForTV() {
        TextView tv = (TextView) findViewById(R.id.tv_up_slide_to_shut_down);
        AnimationSet anim = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1f);
        alphaAnimation.setDuration(1500);
        alphaAnimation.setFillAfter(false);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        Animation tranAnimation = new TranslateAnimation(0, 0, 0, -50f);
        tranAnimation.setDuration(1500);
        tranAnimation.setFillAfter(false);
        tranAnimation.setRepeatCount(Animation.INFINITE);
        tranAnimation.setRepeatMode(Animation.REVERSE);
        anim.addAnimation(alphaAnimation);
        anim.addAnimation(tranAnimation);
        anim.setDuration(1500);
        anim.setFillAfter(false);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        tv.setAnimation(anim);
        tv.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private float startY;
    private float space;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                space = startY - event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (space > 200) {
                    cancelAlarm();
                    space = 0;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void alarmAfter() {
        vibrator.cancel();
        if (ap != null) {
            ap.stopAlarm();
        }
        mBinder.alarmAfter();
        this.finish();
    }

    private void cancelAlarm() {
        vibrator.cancel();
        if (ap != null) {
            ap.stopAlarm();
        }
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_POWER:
            case KeyEvent.KEYCODE_PLUS:
            case KeyEvent.KEYCODE_MINUS:
                return handleKeyEvent();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean handleKeyEvent() {
        if (keyDownOperation == null) {
            keyDownOperation = "稍后再响";
        }
        if ("稍后再响".equals(keyDownOperation)) {
            alarmAfter();

        } else if ("关闭闹钟".equals(keyDownOperation)) {
            cancelAlarm();

        } else {
            return false;
        }
        return true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IS_SHOULD_END_RINGTONE:
                    cancelAlarm();
                    break;
            }
        }
    };

    class TimeEndThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (ring_time == null)
                    ring_time = "20分钟";
                int time = stringToInt(ring_time);
                if (time == -1) {
                    return;
                } else if (System.currentTimeMillis() - startTime > time) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    mHandler.sendEmptyMessage(IS_SHOULD_END_RINGTONE);
                }
            }
        }

        private int stringToInt(String ring_time) {
            if (ring_time.equals("永不停止")) {
                return -1;
            } else {
                int minutes = Integer.parseInt(ring_time.substring(0, ring_time.indexOf("分")));
                return minutes * 60 * 1000;
            }
        }
    }
}
