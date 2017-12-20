package io.zjw.rxdemo.storio;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.get.DefaultGetResolver;

import java.math.BigDecimal;
import java.util.Date;

import io.zjw.rxdemo.models.StockUpdate;

/**
 * Created by mega on 2017/12/18.
 */

public class StockUpdateGetResolver extends DefaultGetResolver<StockUpdate> {
    @NonNull
    @Override
    public StockUpdate mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.ID));
        long dateLong = cursor.getLong(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.DATE));
        long priceLong = cursor.getLong(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.PRICE));
        String stockSymbol = cursor.getString(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.STOCK_SYMBOL));
        String twitterStatus = cursor.getString(cursor.getColumnIndexOrThrow(StockUpdateTable.Columns.TWITTER_STATUS));
        Date date = getDate(dateLong);
        BigDecimal price = getPrice(priceLong);

        final StockUpdate stockUpdate = new StockUpdate(stockSymbol, price, date, twitterStatus);
        stockUpdate.setId(id);
        return stockUpdate;
    }

    // 方法分离，不管此方法多简单，请写成独立的方法
    private Date getDate(long dateLong) {
        return new Date(dateLong);
    }

    private BigDecimal getPrice(long priceLong) {
        return new BigDecimal(priceLong).scaleByPowerOfTen(-4);
    }
}
