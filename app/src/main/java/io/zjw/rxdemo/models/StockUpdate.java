package io.zjw.rxdemo.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.zjw.rxdemo.gson.RandomUserResults;

/**
 * Created by mega on 2017/12/13.
 */

public class StockUpdate implements Serializable {
    private final String stockSymbol;
    private final BigDecimal price;
    private final Date date;

    public static StockUpdate create(RandomUserResults.UserInfo userInfo) {
        return new StockUpdate(userInfo.getFullName(), BigDecimal.valueOf(userInfo.location.postcode), new Date());
    }

    public StockUpdate(String stockSymbol, BigDecimal price, Date date) {
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.date = date;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }
}
