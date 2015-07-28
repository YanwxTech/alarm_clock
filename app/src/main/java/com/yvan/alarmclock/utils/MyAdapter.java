package com.yvan.alarmclock.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yvan.alarmclock.R;
import com.yvan.alarmclock.bean.AlarmClockItem;
import com.yvan.alarmclock.db.DBDao;
import com.yvan.alarmclock.db.DBDaoImp;
import com.yvan.alarmclock.service.AlarmService;

import java.util.List;

/**
 * Created by Yvan on 2015/7/6.
 */
public class MyAdapter extends CommonAdapter<AlarmClockItem> {
    private DBDao<AlarmClockItem> dao;
    private Context context;

    public MyAdapter(Context context, List<AlarmClockItem> mData, int dataLayout) {
        super(context, mData, dataLayout);
        dao = new DBDaoImp(context);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder holder, final AlarmClockItem item) {

        final ToggleButton tb = holder.getView(R.id.tb_on_off);
        tb.setChecked(item.isOn());
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.update(item.getAlarm_id(), tb.isChecked());
                AlarmService.sortListByNearTime();
                item.setIsOn(tb.isChecked());
                notifyDataSetChanged();
                if (tb.isChecked()) {
                    Toast.makeText(context, TimeUtil.getAfterString(item.getAlarm_time(), item.getAlarm_day()), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.setText(R.id.tv_alarm_time, item.getAlarm_time())
                .setText(R.id.tv_days, item.getAlarm_day())
                .setText(R.id.tv_after_time, tb.isChecked() ? TimeUtil.getAfterString(item.getAlarm_time(), item.getAlarm_day()) : "未启用");

    }

}
