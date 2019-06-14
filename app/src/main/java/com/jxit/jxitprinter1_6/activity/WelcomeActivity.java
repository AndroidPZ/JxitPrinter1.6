package com.jxit.jxitprinter1_6.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jxit.jxitprinter1_6.R;

/**
 * WelcomeActivity
 *
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final int GO_MAIN = 1001;
    private static final long SPLASH_DELAY_MILLIS = 500;

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
    }

    /**
     * init
     */
    private void init() {handler.sendEmptyMessageDelayed(GO_MAIN, SPLASH_DELAY_MILLIS);}

    /**
     * Handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_MAIN:
                    goMain();
                    break;
            }
        }
    };

    /**
     * goMain
     */
    private void goMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.startActivity(intent);
        WelcomeActivity.this.finish();
    }


}
