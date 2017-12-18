package io.zjw.rxdemo.errors;

import android.util.Log;

import io.reactivex.functions.Consumer;

/**
 * Created by mega on 2017/12/18.
 */

public class ErrorHandler implements Consumer<Throwable> {
    private static final ErrorHandler INSTANCE = new ErrorHandler();

    public static ErrorHandler get() {
        return INSTANCE;
    }
    private ErrorHandler() {
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        Log.e("APP", "Error on " + Thread.currentThread().getName() + ":", throwable);
    }
}
