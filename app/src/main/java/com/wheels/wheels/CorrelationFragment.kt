package com.wheels.wheels

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.wheels.wheels.correlations.Shadowing
import com.wheels.wheels.stocknamegenerator.StockSymbolGenerator
import kotlinx.android.synthetic.main.fragment_correlation.*
import yahoofinance.YahooFinance
import java.math.BigDecimal
import java.util.*


/**
 * CorrelationFragment is [Fragment] subclass.
 * Gives the user a mWatchListView of ways to correlate their stock with other factors.
 */
class CorrelationFragment : Fragment() {
    var _currentStock = "MSFT"
    private val SHADOW_DAY_RANGE = 25
    private val MIN_THRESHHOLD = 5;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_correlation, container, false)
    }

    override fun onResume() {
        super.onResume()
        val spinner: Spinner = activity.findViewById(R.id.correlation_spinner)
        val adapter = ArrayAdapter.createFromResource(context,
                R.array.correlation_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val seekBar: SeekBar = activity.findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener)
    }

    var seekBarChangeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // updated continuously as the user slides the thumb
            daySlider.setText("Progress: " + progress)
            daySlider.setText("Stock = " + _currentStock);
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            // called when the user first touches the SeekBar
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // called after the user finishes moving the SeekBar
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        CorrelateUpDown().execute()
    }


    private fun stockShadowing(orig : String, toCompare : String): Int {
        val shadowTest = Shadowing(orig, toCompare, SHADOW_DAY_RANGE)
        return shadowTest.exactShadowDates
    }

    @SuppressLint("StaticFieldLeak")
    private inner class CorrelateUpDown : AsyncTask<Void, Void, String>() {
        val stockShadowed = ArrayList<StockHolder>()

        inner class StockHolder(name : String, numDatesShadowed : Integer) {
            var _name : String = name
            var _numDayShadowed = numDatesShadowed;

        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                val stock = YahooFinance.get(_currentStock)
                var textString = "Stock Examining = " + _currentStock
                val change = stock.quote.change
                textString += " change " + change
                val up = BigDecimal("0")
                if (change > up) {
                    textString += " value UP!"
                } else {
                    textString += " value DOWN!"
                }

                //
                // Who else is up today.
                //
                val stockSymbolGenerator = StockSymbolGenerator(2)
                for (name in stockSymbolGenerator.stockNames) {
                    val directShadow = stockShadowing(_currentStock, name)
                    if (directShadow > MIN_THRESHHOLD) {
                        stockShadowed.add(StockHolder(name, Integer(directShadow)))
                        publishProgress()
                    }
                }

                textString += " stock names generated = " + stockSymbolGenerator.stockNames.size
                return textString.toString()
            } catch (e: Exception) {
                // handler
                println("Exception = " + e.stackTrace);
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            // ...
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)

            Collections.sort(stockShadowed) { lhs, rhs ->
                // -1 - less than, 1 - greater than, 0 - equal, all inverse for descending
                if (lhs._numDayShadowed > rhs._numDayShadowed.toInt()) -1 else if (lhs._numDayShadowed < rhs._numDayShadowed.toInt()) 1 else 0
            }

            val textView: TextView = activity.findViewById(R.id.correlation_text_view)
            var resultsString = "Comparing:$_currentStock with \n"
            for (shadow: StockHolder in stockShadowed) {
                resultsString += "Stock:" + shadow._name + " numDays " + shadow._numDayShadowed + "\n"
            }

            textView.setText(resultsString)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

        }
    }

    override fun onDetach() {
        super.onDetach()
    }


}// Required empty public constructor
