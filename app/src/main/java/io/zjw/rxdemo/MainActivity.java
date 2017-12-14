package io.zjw.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.zjw.rxdemo.models.StockUpdate;
import io.zjw.rxdemo.retrofit.RandomUserService;
import io.zjw.rxdemo.retrofit.RetrofitRandomUserFactory;
import io.zjw.rxdemo.retrofit.RetrofitYahooServiceFactory;
import io.zjw.rxdemo.retrofit.YahooService;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @BindView(R.id.hello_world_greet)
    TextView helloText;

    @BindView(R.id.stock_updates_recycler_view)
    RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private StockDataAdapter stockDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Observable.just("Hello! Please use this app responsibly!")
            .subscribe(s -> helloText.setText(s));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stockDataAdapter = new StockDataAdapter();
        recyclerView.setAdapter(stockDataAdapter);

        Observable.just(
                new StockUpdate("GOOGLE", BigDecimal.valueOf(12.43), new Date()),
                new StockUpdate("APPL", BigDecimal.valueOf(645.1), new Date()),
                new StockUpdate("TWTR", BigDecimal.valueOf(1.43), new Date())
        )
//                .subscribeOn(Schedulers.io())
                .doOnNext(e -> Log.d(TAG, Thread.currentThread().getName() + ", doOnNext:" + e))
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                    Log.d(TAG, Thread.currentThread().getName() + ", subscribe:" + e);
                    stockDataAdapter.add(e);
                });

        startQuery();

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


        RandomUserService randomUserService = new RetrofitRandomUserFactory().create();
        randomUserService.fetch(3, "female")
                .subscribeOn(Schedulers.io())
//                .map(r -> r.results)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    log("subscribe", data.results.get(0));
                });

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
            notifyItemInserted(list.size() - 1);
        }
    }

     class StockUpdateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stock_item_symbol) TextView stockSymbol;
        @BindView(R.id.stock_item_date) TextView date;
        @BindView(R.id.stock_item_price) TextView price;

        private final NumberFormat PRICE_FORMAT = new DecimalFormat("#0.00");

        StockUpdateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(StockUpdate stockUpdate) {
            stockSymbol.setText(stockUpdate.getStockSymbol());
            date.setText(DateFormat.format("yyyy-MM-dd hh:mm", stockUpdate.getDate()));
            price.setText(PRICE_FORMAT.format(stockUpdate.getPrice().floatValue()));
        }
    }

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
        Log.e(TAG, "Error", throwable);
    }

}
