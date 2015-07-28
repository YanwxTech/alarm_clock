package com.yvan.alarmclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yvan.alarmclock.service.AlarmService;

public class AlarmClockReceiver extends BroadcastReceiver {
    public AlarmClockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "android.intent.action.BOOT_COMPLETED":
            case AlarmService.START_ACTION:
                Intent i = new Intent(context, AlarmService.class);
                context.startService(i);
                break;
        }
    }
}
