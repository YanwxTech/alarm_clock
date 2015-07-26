package com.yvan.alarmclock;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yvan.alarmclock.bean.AlarmClockItem;
import com.yvan.alarmclock.utils.TimeUtil;
import com.yvan.alarmclock.utils.UriUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private AlarmClockItem alarmClockItem;

    private RelativeLayout rl_choice_days;
    private TextView tv_choice_days;
    private RelativeLayout rl_choice_voice;
    private TextView tv_choice_voice;
    private RelativeLayout rl_set_vibrated;
    private ToggleButton tb_isVibrated;
    private TimePicker tp_alarm_picker;
    private int add_or_set;
    private static final int CODE_PICK_RINGTONE = 0X111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("list");
        add_or_set = bundle.getInt("add_or_set");
        if (add_or_set == 1) {
            alarmClockItem = new AlarmClockItem();
        } else if (add_or_set == 2) {
            alarmClockItem = (AlarmClockItem) bundle.getSerializable("list_item");
        }
        initActionBar();
        initView();
        initEvent();
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setCustomView(R.layout.top_actionbar);
        ((TextView) actionbar.getCustomView()
                .findViewById(R.id.text_actionbar_title))
                .setText("闹钟设置");
    }

    private void initView() {
        rl_choice_days = (RelativeLayout) findViewById(R.id.rl_choice_days);
        rl_choice_voice = (RelativeLayout) findViewById(R.id.rl_choice_voice);
        rl_set_vibrated = (RelativeLayout) findViewById(R.id.rl_choice_vib);
        tv_choice_days = (TextView) findViewById(R.id.tv_show_choice_days);
        tv_choice_voice = (TextView) findViewById(R.id.tv_show_choice_voice);
        tb_isVibrated = (ToggleButton) findViewById(R.id.tb_is_vibrated);
        tp_alarm_picker = (TimePicker) findViewById(R.id.tp_alarm_time);
    }

    private void initEvent() {
        rl_choice_days.setOnClickListener(this);
        rl_choice_voice.setOnClickListener(this);
        rl_set_vibrated.setOnClickListener(this);
        tp_alarm_picker.setIs24HourView(true);

        String choice_days = alarmClockItem.getAlarm_day();
        if (choice_days != null) {
            tv_choice_days.setText(choice_days);
        }

        String choice_voice = alarmClockItem.getVoicePath();
        if (choice_voice != null) {
            String title = UriUtil.uriToName(this, Uri.parse(choice_voice));
            if (title != null)
                tv_choice_voice.setText(title);
        }
//        else {
//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            ringtoneUri = uri;
//            tv_choice_voice.setText(UriUtil.uriToName(this, uri));
//        }

        boolean isVibrated = alarmClockItem.isVibrated();
        tb_isVibrated.setChecked(isVibrated);

        String alarm_time = alarmClockItem.getAlarm_time();
        if (alarm_time != null) {
            String[] time = alarm_time.split(":");
            if (time.length == 2) {
                tp_alarm_picker.setCurrentHour(Integer.parseInt(time[0]));
                tp_alarm_picker.setCurrentMinute(Integer.parseInt(time[1]));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                exitSure();
                break;
            case R.id.action_sure:
                alarmItemSetting();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("闹铃重复操作");
        menu.add(1, 1, 1, TimeUtil.ONLY_ONCE);
        menu.add(1, 2, 1, TimeUtil.EVERYDAY);
        menu.add(1, 3, 1, TimeUtil.WEEKDAY);
        menu.add(1, 4, 1, "自定义");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                tv_choice_days.setText(TimeUtil.ONLY_ONCE);
                break;
            case 2:
                tv_choice_days.setText(TimeUtil.EVERYDAY);
                break;
            case 3:
                tv_choice_days.setText(TimeUtil.WEEKDAY);
                break;
            case 4:
                showDialog();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private String[] weekdays = new String[]{"星期一", "星期二", "星期三",
            "星期四", "星期五", "星期六", "星期日"};
    List<Integer> checkedDays = new ArrayList<>();

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("重复");
        builder.setMultiChoiceItems(
                weekdays,
                new boolean[]{
                        checkedDays.contains(0), checkedDays.contains(1), checkedDays.contains(2),
                        checkedDays.contains(3), checkedDays.contains(4), checkedDays.contains(5),
                        checkedDays.contains(6)}, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            checkedDays.add(which);
                        } else {
                            checkedDays.remove((Integer) which);
                        }
                    }
                });
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showChoiceDays(checkedDays);
            }
        });
        builder.show();
    }

    private void showChoiceDays(List<Integer> checkedDays) {
        Collections.sort(checkedDays);
        String text = "";
        if (checkedDays.size() == 0) {
            return;
        } else if (checkedDays.size() == 7) {
            text = TimeUtil.EVERYDAY;
        } else if (checkedDays.size() == 5 && !checkedDays.contains(5) && !checkedDays.contains(6)) {
            text = TimeUtil.WEEKDAY;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Integer item : checkedDays) {
                sb.append(TimeUtil.intToWeekday(item) + " ");
            }
            text = sb.toString();
        }
        tv_choice_days.setText(text);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) exitSure();
        return super.onKeyDown(keyCode, event);
    }

    private void exitSure() {
        if (add_or_set == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    SettingActivity.this);
            builder.setMessage("舍弃更改？");
            builder.setPositiveButton("保存",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alarmItemSetting();
                        }
                    });
            builder.setNegativeButton("舍弃",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SettingActivity.this.finish();
                        }
                    });
            builder.show();
        } else {
            this.finish();
        }
    }

    private void alarmItemSetting() {
        int hour = tp_alarm_picker.getCurrentHour();
        int minute = tp_alarm_picker.getCurrentMinute();
        String time = String.format("%1$02d:%2$02d", hour, minute);
        alarmClockItem.setAlarm_time(time);
        boolean isVibrated = tb_isVibrated.isChecked();
        alarmClockItem.setIsVibrated(isVibrated);
        if (ringtoneUri != null) {
            alarmClockItem.setVoicePath(ringtoneUri + "");
        }
        alarmClockItem.setAlarm_day(tv_choice_days.getText().toString());
        if (add_or_set == 1) {
            int id = (int) (System.currentTimeMillis() % 100000);
            alarmClockItem.setAlarm_id(id);
            alarmClockItem.setIsOn(true);
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list_item", alarmClockItem);
        intent.putExtras(bundle);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_choice_days:
                registerForContextMenu(v);
                openContextMenu(v);
                unregisterForContextMenu(v);
                break;
            case R.id.rl_choice_voice:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*;application/ogg");
                Intent wrapIntent = Intent.createChooser(intent, "选择铃声");
                startActivityForResult(wrapIntent, CODE_PICK_RINGTONE);
                break;
            case R.id.rl_choice_vib:
                tb_isVibrated.setChecked(!tb_isVibrated.isChecked());
                break;
        }
    }

    private Uri ringtoneUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CODE_PICK_RINGTONE) {
            Uri uri = data.getData();
            if (uri != null) {
                ringtoneUri = uri;
                String title = UriUtil.uriToName(this, uri);
                if (title != null && !title.equals(""))
                    tv_choice_voice.setText(title);
                else {
                    Toast.makeText(this, "所选铃声无效，保持原有设置", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "所选铃声无效，保持原有设置", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
