package com.jxit.jxitprinter1_6.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jxit.jxitprinter1_6.R;

public class SettingActivity extends AppCompatActivity {
    private TextView shuaxin,gengxin;
    private Switch lixian;
    private Toolbar settingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    /**
     * init
     */
    private void init(){
        shuaxin = (TextView) findViewById(R.id.shuaxin);
        shuaxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this,"刷新待开发",Toast.LENGTH_SHORT).show();
            }
        });
        gengxin = (TextView) findViewById(R.id.gengxin);
        gengxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this,"更新待开发",Toast.LENGTH_SHORT).show();
            }
        });
        lixian = (Switch) findViewById(R.id.lixian);
        lixian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        settingbar = (Toolbar) findViewById(R.id.settingbar);
        settingbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
