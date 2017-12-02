package com.wheels.wheels.correlations;

import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 * This class compares to stocks price histories. It allows the caller to see when they were shadowing.
 * In other words, when the stock prices (up and down) copy each other.
 */
public class Shadowing {
    private final String mOriginStock;
    private final String mCompareStock;
    private final int mDaysFrom;
    private final static String TAG = "Shadowing";

    public Shadowing(String originStock, String compareStock, int daysFrom) {
        mOriginStock = originStock;
        mCompareStock = compareStock;
        mDaysFrom = daysFrom;
    }

    /**
     * Count the days that originStock and compareStock follow each other.
     * @return number of days the stocks shadowed together.
     * @throws IOException
     */
    public int getExactShadowDates() throws IOException {
        //
        // Get the stock from yahoo.
        //
        Stock originalStock;
        Stock compareStock ;
        try {
            originalStock = YahooFinance.get( mOriginStock );
            compareStock = YahooFinance.get( mCompareStock );
        } catch (Exception e) {
            Log.d(TAG, "Exception = " + e);
            return 0;
        }

        //
        // Get the to and from dates minus one.
        //
        Calendar to = Calendar.getInstance();
        Calendar from = Calendar.getInstance();
        from.add( Calendar.DAY_OF_MONTH, mDaysFrom * -1 );
        to.add( Calendar.DAY_OF_MONTH, -1 );

        //
        // Count number of days the stock when up or down on that day.
        //
        int matchingDays = getNumberOfDaysShadowed( originalStock, compareStock, to, from );

        return matchingDays;
    }

    private int getNumberOfDaysShadowed(Stock originalStock, Stock compareStock, Calendar to, Calendar from) {

        int matchingDays = 0;
        try {

            //
            // Get the history given the interval.
            //
            final List<HistoricalQuote> origHistory = originalStock.getHistory( from, to, Interval.DAILY );
            final List<HistoricalQuote> compareHistory = compareStock.getHistory( from, to, Interval.DAILY );

            //
            // Look to see if it's been shadowing.
            //
            for (boolean shadowing = true; ((matchingDays < origHistory.size()) && shadowing); matchingDays++) {
                //
                // Get the opening and closing price from the past date or the original stock.
                //
                final HistoricalQuote historicalQuote = origHistory.get( matchingDays );
                BigDecimal open = historicalQuote.getOpen();
                BigDecimal close = historicalQuote.getClose();

                final BigDecimal subtract = open.subtract( close );
                boolean originalStockUp = subtract.compareTo( BigDecimal.ZERO) > 0;

                //
                // Get the open
                //
                final BigDecimal compareOpen = compareHistory.get( matchingDays ).getOpen();
                final BigDecimal compareClose = compareHistory.get( matchingDays ).getClose();
                boolean compareStockUp = (compareOpen.subtract( compareClose ).compareTo( BigDecimal.ZERO) > 0);

                if (originalStockUp != compareStockUp) {
                    shadowing = false;
                }
            }

        } catch (Exception e) {
        }
        return matchingDays;
    }
}
