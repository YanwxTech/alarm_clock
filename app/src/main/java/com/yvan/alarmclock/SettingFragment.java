package com.yvan.alarmclock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.widget.SeekBar;

import com.yvan.alarmclock.utils.UriUtil;

/**
 * Created by Yvan on 2015/7/26.
 */
public class SettingFragment extends PreferenceFragment {
    private Preference setVolume;
    private ListPreference setInterval, setAllTime, setAlarmKeyDown;
    ;
    private RingtonePreference setDefaultRingtone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_setting);
        initPreference();
        initEvent();
    }


    private void initPreference() {
        setVolume = findPreference("alarm_volume");
        setInterval = (ListPreference) findPreference("alarm_interval");
        setAllTime = (ListPreference) findPreference("alarm_all_time");
        setDefaultRingtone = (RingtonePreference) findPreference("default_ringtone");
        setAlarmKeyDown = (ListPreference) findPreference("alarm_key_down");
        Uri uri = Uri.parse(setDefaultRingtone.getSharedPreferences().getString(setDefaultRingtone.getKey(), ""));
        String title = UriUtil.uriToName(getActivity(), uri);
        setDefaultRingtone.setSummary(title);
        setVolume.setSummary(setVolume.getSharedPreferences().getInt(setVolume.getKey(), 0) + "");
        setInterval.setSummary(setInterval.getEntry());
        setAllTime.setSummary(setAllTime.getEntry());
        setAlarmKeyDown.setSummary(setAlarmKeyDown.getEntry());

    }

    private void initEvent() {
        setVolume.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showSeekBarDialog();
                return true;
            }
        });
        setInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });
        setAllTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });

        setAlarmKeyDown.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });

        setDefaultRingtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String title = UriUtil.uriToName(getActivity(), Uri.parse((String) newValue));
                preference.setSummary(title);
                return true;
            }
        });
    }

    private void showSeekBarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("闹钟音量设置");
        final SeekBar seekBar = new SeekBar(getActivity());
        seekBar.setMax(100);
        seekBar.setProgress((int) (setVolume.getSharedPreferences().getInt(setVolume.getKey(), 0) / 7f * 100));
        builder.setView(seekBar);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int volume = (int) (seekBar.getProgress() * 7 / 100f);
                setVolume.setSummary(volume + "");
                setVolume.getEditor().putInt(setVolume.getKey(), volume).commit();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

}
