package io.zjw.rxdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.zjw.rxdemo.errors.ErrorHandler;
import io.zjw.rxdemo.models.StockUpdate;
import io.zjw.rxdemo.retrofit.RandomUserService;
import io.zjw.rxdemo.retrofit.RetrofitRandomUserFactory;
import io.zjw.rxdemo.storio.StockUpdateTable;
import io.zjw.rxdemo.storio.StorIOFactory;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Observable;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    @BindView(R.id.hello_world_greet)
    TextView helloText;

    @BindView(R.id.stock_updates_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.no_data_available)
    TextView noDataAvailableView;

    @OnClick(R.id.start_another_activity_button)
    public void onStartAnotherActivityButtonClick(Button button) {
        startActivity(new Intent(this, MockActivity.class));
    }

    private LinearLayoutManager layoutManager;
    private StockDataAdapter stockDataAdapter;

    static <T> Observable<T> v2(rx.Observable<T> source) {
        return toV2Observable(source);
    }

    final Configuration configuration = new ConfigurationBuilder()
            .setDebugEnabled(true)
            .setOAuthConsumerKey("aQLuORYqjwg8m9gUjugSIVjDH")
            .setOAuthConsumerSecret("WlAEQtTsa8jKU0cgT1Lrl4fmY5xsf1UlZNZGtDhstCBMtwyFww")
            .setOAuthAccessToken("970274502-ph0HKIxBjJQWNCxyWUTKW5CfVAFYsS9C8wZwNRTl")
            .setOAuthAccessTokenSecret("cy7q5Ye27BDoaZNYHMsKu71NAHPT6uymaX1c4K43QdYZD")
            .build();
    FilterQuery filterQuery = new FilterQuery().track("Yahoo", "Google", "Microsoft").language("en");

//    TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
//
//    StatusListener twitterListenner = new StatusListener() {
//        @Override
//        public void onStatus(Status status) {
//            System.out.println(status.getUser().getName() + " : " + status.getText());
//
//        }
//
//        @Override
//        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//
//        }
//
//        @Override
//        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//
//        }
//
//        @Override
//        public void onScrubGeo(long userId, long upToStatusId) {
//
//        }
//
//        @Override
//        public void onStallWarning(StallWarning warning) {
//
//        }
//
//        @Override
//        public void onException(Exception ex) {
//            ex.printStackTrace();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxJavaPlugins.setErrorHandler(ErrorHandler.get());
        lifecycleSubject.onNext(ActivityEvent.CREATE);

        ButterKnife.bind(this);

        Observable.just("Hello! Please use this app responsibly!")
            .subscribe(s -> helloText.setText(s));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stockDataAdapter = new StockDataAdapter();
        recyclerView.setAdapter(stockDataAdapter);

//        startQuery();

//        prepareTwitter(); // no rx way
//        prepareTwitterAdObservable();

        startFetch();
    }

    private void startFetch() {
        Observable.merge(
                Observable.interval(30, 5, TimeUnit.SECONDS)
                        .doOnNext(i -> stockDataAdapter.clear())
                        .flatMap(i -> RetrofitRandomUserFactory.getInstance().fetch(3, "female").toObservable())
                        .map(r -> r.results)
                        .flatMap(Observable::fromIterable)
                        .map(StockUpdate::create),
                observeTwitterStream(configuration, filterQuery).map(StockUpdate::create)
        )
                .compose(RxLifecycle.bindUntilEvent(lifecycleSubject, ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(this::saveStockUpdate)
                .onExceptionResumeNext(v2(StorIOFactory.get(this)
                        .get()
                        .listOfObjects(StockUpdate.class)
                        .withQuery(Query.builder()
                                .table(StockUpdateTable.TABLE)
                                .orderBy("date DESC")
                                .limit(3)
                                .build())
                        .prepare()
                        .asRxObservable())
                        .take(1)
                        .flatMap(Observable::fromIterable))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> {
                    log("doOnError", error);
                    Toast.makeText(this, "We couldn't reach internet - falling back to local data", Toast.LENGTH_SHORT).show();
                })
                .subscribe(data -> {
                    log("subscribe: " + data.getStockSymbol());
                    noDataAvailableView.setVisibility(View.GONE);
                    stockDataAdapter.add(data);
                }, e -> {
                    if (stockDataAdapter.getItemCount() == 0) {
                        noDataAvailableView.setVisibility(View.VISIBLE);
                    }
                });
    }

    Observable<Status> observeTwitterStream(Configuration configuration, FilterQuery filterQuery) {
        return Observable.create(emitter -> {
            TwitterStream ts = new TwitterStreamFactory(configuration).getInstance();
            emitter.setCancellable(ts::shutdown);

            StatusListener statusListener = new StatusListener() {
                @Override
                public void onException(Exception ex) {
                    emitter.onError(ex);
                }

                @Override
                public void onStatus(Status status) {
                    emitter.onNext(status);
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                }

                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

                }

                @Override
                public void onScrubGeo(long userId, long upToStatusId) {

                }

                @Override
                public void onStallWarning(StallWarning warning) {

                }
            };
            ts.addListener(statusListener);
            ts.filter(filterQuery);
        });
    }

//    private void prepareTwitter() {
//        twitterStream.addListener(twitterListenner);
//        twitterStream.filter(new FilterQuery().track("Yahoo", "Google", "Microsoft").language("en"));
//    }
    
    

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
    }

    private void startQuery() {
//        YahooService yahooService = new RetrofitYahooServiceFactory().create();
//        String query = "select * from yahoo.finance.quote where symbol in ('YHOO','AAPL','GOOG','MSFT')";
//        String env = "store://datatables.org/alltableswithkeys";
//
//        yahooService.yqlQuery(query, env)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(data -> {
//                    log(data.query.results.quote.get(0).symbol);
//                }, this::log);

        RandomUserService randomUserService = RetrofitRandomUserFactory.getInstance();
//        randomUserService.fetch(8, "female")
//                .subscribeOn(Schedulers.io())
//                .toObservable()
//                .map(r -> r.results)
//                .flatMap(r -> Observable.fromIterable(r))
//                .map(r -> StockUpdate.create(r))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(data -> {
//                    log("subscribe: " + data);
//                    stockDataAdapter.add(data);
//                });
        Observable.interval(0, 5, TimeUnit.SECONDS)
                .compose(RxLifecycle.bindUntilEvent(lifecycleSubject, ActivityEvent.DESTROY))
                .doOnNext(i -> stockDataAdapter.clear())
                .flatMap(i -> randomUserService.fetch(3, "female").toObservable())
//                .flatMap(
//                        i -> Observable.<RandomUserResults>error(new RuntimeException("Oops"))
//                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> {
                    log("doOnError", error);
                    Toast.makeText(this, "We couldn't reach internet - falling back to local data", Toast.LENGTH_SHORT).show();
                })
                .observeOn(Schedulers.io())
                .map(r -> r.results)
                .flatMap(Observable::fromIterable)
                .map(StockUpdate::create)
                .doOnNext(this::saveStockUpdate)
                .onExceptionResumeNext(v2(StorIOFactory.get(this)
                        .get()
                        .listOfObjects(StockUpdate.class)
                        .withQuery(Query.builder()
                                .table(StockUpdateTable.TABLE)
                                .orderBy("date DESC")
                                .limit(3)
                                .build())
                        .prepare()
                        .asRxObservable())
                        .take(1)
                        .flatMap(Observable::fromIterable))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    log("subscribe: " + data.getStockSymbol());
                    noDataAvailableView.setVisibility(View.GONE);
                    stockDataAdapter.add(data);
                }, e -> {
                    if (stockDataAdapter.getItemCount() == 0) {
                        noDataAvailableView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    class StockDataAdapter extends RecyclerView.Adapter<StockUpdateViewHolder> {
        private final List<StockUpdate> list = new ArrayList<>();

        @Override
        public StockUpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_update_item, parent, false);
            StockUpdateViewHolder vh = new StockUpdateViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(StockUpdateViewHolder holder, int position) {
            StockUpdate stockUpdate = list.get(position);
            holder.bindView(stockUpdate);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void add(StockUpdate stockUpdate) {
            list.add(stockUpdate);
            notifyDataSetChanged();
//            notifyItemInserted(list.size() - 1);
        }

        public void clear() {
            list.clear();
        }
    }

     class StockUpdateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stock_item_symbol) TextView stockSymbol;
        @BindView(R.id.stock_item_date) TextView date;
        @BindView(R.id.stock_item_price) TextView price;
        @BindView(R.id.stock_item_twitter_status) TextView twitterStatus;

        private final NumberFormat PRICE_FORMAT = new DecimalFormat("#0.00");

        StockUpdateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(StockUpdate stockUpdate) {
            stockSymbol.setText(stockUpdate.getStockSymbol());
            date.setText(DateFormat.format("yyyy-MM-dd hh:mm", stockUpdate.getDate()));
            price.setText(PRICE_FORMAT.format(stockUpdate.getPrice().floatValue()));
            twitterStatus.setText(stockUpdate.getTwitterStatus());
            setIsStatusUpdate(stockUpdate.isTwitterStatusUpdate());
        }

         private void setIsStatusUpdate(boolean twitterStatusUpdate) {
             if (twitterStatusUpdate) {
                 this.twitterStatus.setVisibility(View.VISIBLE);
                 this.price.setVisibility(View.GONE);
                 this.stockSymbol.setVisibility(View.GONE);
             } else {
                 this.twitterStatus.setVisibility(View.GONE);
                 this.price.setVisibility(View.VISIBLE);
                 this.stockSymbol.setVisibility(View.VISIBLE);
             } }

    }

    // save sqlite
    public void saveStockUpdate(StockUpdate stockUpdate) {
        log("saveStockUpdate", stockUpdate.getStockSymbol());
        StorIOFactory.get(this)
                .put()
                .object(stockUpdate)
                .prepare()
                .asRxSingle()
                .subscribe();
    }


    private void getStockUpdate(Observer<? super StockUpdate> observer) {
        v2(StorIOFactory.get(this)
                .get()
                .listOfObjects(StockUpdate.class)
                .withQuery(Query.builder()
                        .table(StockUpdateTable.TABLE)
                        .orderBy("date DESC")
                        .limit(50)
                        .build())
                .prepare()
                .asRxObservable())
                .take(1)
                .flatMap(Observable::fromIterable);
    }

    // log
    private void log(String stage, String item) {
        Log.d(TAG, stage + ":" + Thread.currentThread().getName() + ":" + item);
    }

    private void log(String stage, Object o) {
        Log.d(TAG, stage + ":" + Thread.currentThread().getName() + ":" + o);
    }

    private void log(String stage) {
        Log.d(TAG, stage + ":" + Thread.currentThread().getName());
    }
    private void log(Throwable throwable) {
        Log.e(TAG, "Error on " + Thread.currentThread().getName() + ":", throwable);
    }

}
