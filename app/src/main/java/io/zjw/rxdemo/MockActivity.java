package io.zjw.rxdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class MockActivity extends AppCompatActivity {
    BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock);


        Observable.interval(1, TimeUnit.SECONDS)
                .compose(RxLifecycleAndroid.bindActivity(lifecycleSubject))
                .doOnDispose(() -> Log.i("APP", "Disposed"))
                .doOnTerminate(() -> Log.i("APP", "Terminate"))
                .subscribe(i -> Log.i("APP", "sub..."));

        Log.i("APP", "Activity Created");
        lifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.i("APP", "Activity Destroyed");
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }
}
