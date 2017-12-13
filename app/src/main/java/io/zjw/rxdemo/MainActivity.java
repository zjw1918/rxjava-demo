package io.zjw.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.zjw.rxdemo.models.StockUpdate;

public class MainActivity extends AppCompatActivity {
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
        StockDataAdapter stockDataAdapter = new StockDataAdapter();
        recyclerView.setAdapter(stockDataAdapter);

        Observable.just(
                new StockUpdate("GOOGLE", BigDecimal.valueOf(12.43), new Date()),
                new StockUpdate("APPL", BigDecimal.valueOf(645.1), new Date()),
                new StockUpdate("TWTR", BigDecimal.valueOf(1.43), new Date())
        ).subscribe(stockDataAdapter::add);

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
}
