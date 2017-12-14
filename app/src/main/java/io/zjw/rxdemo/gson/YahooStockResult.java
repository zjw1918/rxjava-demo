package io.zjw.rxdemo.gson;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mega on 2017/12/14.
 */



public class YahooStockResult {
    public YahooStockQuery query;

    public class YahooStockQuery {
        public int count;
        public Date created;
        public YahooStockResults results;

        public class YahooStockResults {
            public List<YahooStockQuote> quote;

            public class YahooStockQuote {
                public String symbol;
                @SerializedName("Name")
                public String name;
                @SerializedName("LastTradePriceOnly")
                public BigDecimal lastTradePriceOnly;
                @SerializedName("DaysLow")
                public BigDecimal daysLow;
                @SerializedName("DaysHigh")
                public BigDecimal daysHigh;
                @SerializedName("Volume")
                public String volume;
            }
        }
    }


}
