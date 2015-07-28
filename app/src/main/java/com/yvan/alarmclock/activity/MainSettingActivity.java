package com.yvan.alarmclock.activity;

import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yvan.alarmclock.R;

public class MainSettingActivity extends AppCompatActivity {
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        initActionBar();
        fragment = new SettingFragment();
        getFragmentManager().beginTransaction().add(R.id.fl_main_setting, fragment).commit();
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setHideOffset(0);
        actionbar.setCustomView(R.layout.top_actionbar);
        ImageButton iv_back= (ImageButton) actionbar.getCustomView()
                .findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        ((TextView) actionbar.getCustomView()
                .findViewById(R.id.text_actionbar_title))
                .setText("设置");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainSettingActivity.this.finish();
            }
        });
    }

}
