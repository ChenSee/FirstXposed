package com.chen.firstxposed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chen.firstxposed.util.AutoCollectUtils;
import com.chen.firstxposed.util.Config2;
import com.chen.firstxposed.util.RecordUtil;

public class MainActivity extends Activity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        Config2.init();
        AutoCollectUtils.startAlipay(this);
        set();
        handler.postDelayed(runnable, 2000);
    }

    @SuppressLint("SdCardPath")
    protected void set() {
        ListView listView = (ListView) findViewById(R.id.list);
        String[] array = RecordUtil.show().toArray(new String[0]);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.item, array));
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            set();
            handler.postDelayed(this, 60000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        set();
    }
}