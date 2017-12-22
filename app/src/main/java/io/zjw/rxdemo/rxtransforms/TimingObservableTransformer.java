package io.zjw.rxdemo.rxtransforms;

import android.util.Pair;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;

/**
 * Created by mega on 2017/12/22.
 */

public class TimingObservableTransformer<R> implements ObservableTransformer<R, R> {
    private final Consumer<Long> timerAction;

    public TimingObservableTransformer(Consumer<Long> timerAction) {
        this.timerAction = timerAction;
    }

    @Override
    public ObservableSource<R> apply(Observable<R> upstream) {
        return Observable.combineLatest(
                Observable.just(new Date()),
                upstream,
                Pair::create
        )
                .doOnNext(pair -> {
                    Date currentDate = new Date();
                    long diff = currentDate.getTime() - pair.first.getTime();
                    long diffSeconds = diff / 1000;
                    timerAction.accept(diffSeconds);
                })
                .map(pair -> pair.second);
    }

    public static <R> TimingObservableTransformer<R> timeItems(Consumer<Long> timerAction) {
        return new TimingObservableTransformer<>(timerAction);
    }
}
