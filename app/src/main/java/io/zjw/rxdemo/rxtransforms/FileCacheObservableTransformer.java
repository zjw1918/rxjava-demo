package io.zjw.rxdemo.rxtransforms;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * Created by mega on 2017/12/22.
 */

public class FileCacheObservableTransformer<R> implements ObservableTransformer<R, R> {
    private final String filename;
    private final Context context;

    public FileCacheObservableTransformer(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    @Override
    public ObservableSource<R> apply(Observable<R> upstream) {
        return readFromFile()
                .onExceptionResumeNext(
                        upstream
                                .take(1)
                                .doOnNext(this::saveToFile)
                );
    }

    public static <R> FileCacheObservableTransformer<R> cacheToLocalFileNamed(String filename, Context context) {
        return new FileCacheObservableTransformer<R>(filename, context);
    }

    private Observable<R> readFromFile() {
        return Observable.create(emitter -> {
            ObjectInputStream input = null;

            try {
                FileInputStream fileInputStream = new FileInputStream(getFilename());
                input = new ObjectInputStream(fileInputStream);
                R foundObj = (R) input.readObject();
                emitter.onNext(foundObj);
            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                if (input != null) {
                    input.close();
                }
                emitter.onComplete();
            }
        });
    }

    private void saveToFile(R r) throws IOException {
        ObjectOutputStream output = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getFilename());
            output = new ObjectOutputStream(fileOutputStream);
            output.writeObject(r);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private String getFilename() {
        return context.getExternalFilesDir("log").getAbsolutePath() + File.separator + filename;
    }

}
