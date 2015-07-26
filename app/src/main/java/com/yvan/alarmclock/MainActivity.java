package com.yvan.alarmclock;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.yvan.alarmclock.bean.AlarmClockItem;
import com.yvan.alarmclock.db.DBDao;
import com.yvan.alarmclock.db.DBDaoImp;
import com.yvan.alarmclock.utils.MyAdapter;
import com.yvan.alarmclock.utils.UriUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView listView;
    private Button btn_add_ac;
    private Button btn_main_setting;
    private MyAdapter adapter;
    private List<AlarmClockItem> list;

    //保存要设置或删除的闹铃的位置
    private static int mPosition = 0;
    //数据库操作Dao
    private DBDao<AlarmClockItem> mDao;

    //intent的requestCode
    private static final int SETTING_REQUEST_CODE = 0x101;
    private static final int ADD_REQUEST_CODE = 0x102;


    private static final int UPDATE_AFTER_TIME = 0x111;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_AFTER_TIME) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
        initView();
        initEvent();
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        //actionbar.setDefaultDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        //actionbar.setWindowTitle("adsg");
        actionbar.setCustomView(R.layout.top_actionbar);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_alarm_clock);
        btn_add_ac = (Button) findViewById(R.id.btn_add_ac);
        btn_main_setting = (Button) findViewById(R.id.btn_main_setting);
    }

    private void initEvent() {
        mDao = new DBDaoImp(this);
        list = mDao.queryAll();
        if (list == null || list.size() == 0) {
            initDB();
        }
        sortList();
        adapter = new MyAdapter(this, list, R.layout.alarm_clock_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        registerForContextMenu(listView);
        btn_add_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("add_or_set", 1);
                intent.putExtra("list", bundle);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });
        btn_main_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isRunning = false;

    class MyThread implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    Thread.sleep(1000 * 60);
                    mHandler.sendEmptyMessage(UPDATE_AFTER_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        new Thread(new MyThread()).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    private void initDB() {
       //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        AlarmClockItem item = new AlarmClockItem();
        item.setAlarm_id(101);
        item.setAlarm_time("08:00");
        item.setAlarm_day("周一至周五");
        item.setIsOn(false);
        item.setIsVibrated(false);
        //item.setVoicePath(UriUtil.uriToName(this,uri));
        item.setVoicePath("默认铃声");
        mDao.insert(item);
        list.add(item);

        item = new AlarmClockItem();
        item.setAlarm_id(102);
        item.setAlarm_time("09:00");
        item.setAlarm_day("周六周日");
        item.setIsOn(false);
        item.setIsVibrated(false);
        //item.setVoicePath(UriUtil.uriToName(this,uri));
        item.setVoicePath("默认铃声");
        mDao.insert(item);
        list.add(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1, 1, 1, "删除闹铃");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            mDao.delete(list.get(mPosition).getAlarm_id());
            list.remove(mPosition);
            adapter.notifyDataSetChanged();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPosition = position;
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("add_or_set", 2);
        bundle.putSerializable("list_item", list.get(position));
        intent.putExtra("list", bundle);
        startActivityForResult(intent, SETTING_REQUEST_CODE);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("isLongClick", "true");
        mPosition = position;
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            AlarmClockItem item = (AlarmClockItem) bundle.getSerializable("list_item");
            if (item != null) {
                mDao.update(item);
                list.set(mPosition, item);
                sortList();
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == ADD_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            AlarmClockItem item = (AlarmClockItem) bundle.getSerializable("list_item");
            if (item != null) {
                mDao.insert(item);
                list.add(item);
                sortList();
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void sortList() {
        Collections.sort(list, new Comparator<AlarmClockItem>() {
            @Override
            public int compare(AlarmClockItem lhs, AlarmClockItem rhs) {
                return lhs.getAlarm_time().compareTo(rhs.getAlarm_time());
            }
        });
    }
}
