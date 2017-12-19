package io.zjw.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MockActivity extends AppCompatActivity {
    private String[] bigArray = new String[10000000];
    private static final List<MockActivity> INSTANCES = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);

        bigArray[0] = "test";
        INSTANCES.add(this);
        Log.i("APP", "Activity Created");
    }

    @Override
    protected void onDestroy() {
        Log.i("APP", "Activity Destroyed");
        super.onDestroy();
    }
}
