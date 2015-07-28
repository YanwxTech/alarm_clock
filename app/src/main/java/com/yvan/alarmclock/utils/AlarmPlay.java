package com.yvan.alarmclock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Yvan on 2015/7/27.
 */
public class AlarmPlay {
    private MediaPlayer mp = new MediaPlayer();
    private Uri mUri;
    private Context mContext;
    private SharedPreferences spf;

    public AlarmPlay(Context context, Uri uri) {
        mUri = uri;
        Log.i("mUri", mUri + "");
        mContext = context;
        spf = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void startAlarm() {
        if (mUri == null || mUri.equals("") || mUri.toString().equals("默认铃声")) {
            mUri = getDefaultRingtoneUri();
        }
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(mContext, mUri);
            mp.setLooping(true);
            int volume = spf.getInt("alarm_volume", 5);
            boolean alarm_increasing = spf.getBoolean("alarm_increasing", false);
            mp.prepare();
            if (alarm_increasing) {
                volumeMinToMax(volume);
            } else {
                mp.setVolume(volume / 7f, volume / 7f);
            }
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void volumeMinToMax(final float max) {
        new Thread() {
            @Override
            public void run() {
                float i = 0;
                if (mp != null) {
                    boolean isPlaying = false;
                    try {
                        isPlaying = mp.isPlaying();
                    } catch (Exception e) {
                        isPlaying = true;
                    }
                    while (isPlaying) {
                        mp.setVolume(i, i);
                        i += 0.05;
                        if (i > max) {
                            mp.setVolume(max, max);
                            break;
                        }
                        try {
                            Thread.currentThread().sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }

    private Uri getDefaultRingtoneUri() {
        String uri = spf.getString("default_ringtone", "");
        Log.i("default_ringtone", uri);
        if (uri == null || uri.equals("") || uri.equals("系统默认铃声")) {
            return RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_ALARM);
        } else {
            return Uri.parse(uri);
        }
    }

    public void stopAlarm() {
        boolean isPlaying = false;
        if (mp != null) {
            try {
                isPlaying = mp.isPlaying();
            } catch (Exception e) {
                isPlaying = true;
            }
            if (isPlaying) {
                mp.stop();
                mp.release();
                mp = null;
            }
        }
    }

    public void pauseAlarm() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }
}
