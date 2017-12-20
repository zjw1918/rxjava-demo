package io.zjw.rxdemo.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.zjw.rxdemo.gson.RandomUserResults;
import twitter4j.Status;

/**
 * Created by mega on 2017/12/13.
 */

public class StockUpdate implements Serializable {
    private Integer id;
    private final String stockSymbol;
    private final BigDecimal price;
    private final Date date;
    private final String twitterStatus;

    public static StockUpdate create(RandomUserResults.UserInfo userInfo) {
        return new StockUpdate(userInfo.getFullName(), BigDecimal.valueOf(userInfo.location.postcode), new Date(), "");
    }

    public static StockUpdate create(Status status) {
        return new StockUpdate("", BigDecimal.ZERO, status.getCreatedAt(), status.getText());
    }

    public StockUpdate(String stockSymbol, BigDecimal price, Date date, String twitterStatus) {
        if (stockSymbol == null) {
            stockSymbol = "";
        }
        if (twitterStatus == null) {
            twitterStatus = "";
        }
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.date = date;
        this.twitterStatus = twitterStatus;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTwitterStatus() {
        return twitterStatus;
    }

    public boolean isTwitterStatusUpdate() {
        return !twitterStatus.isEmpty();
    }
}
