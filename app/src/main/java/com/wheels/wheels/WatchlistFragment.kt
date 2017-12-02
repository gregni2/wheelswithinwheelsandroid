/**
 * Created by gregorynield on 12/2/17.
 */
package com.wheels.wheels

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*


/**
 * Watchlist subclass of subclass.
 * Shows the stocks that we are watching.
 */
class WatchlistFragment : Fragment(), AdapterView.OnItemClickListener {
    private var mWatchListView: ListView? = null
    private var mStockAdapter: ArrayAdapter<*>? = null
    private val mStocksArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //
        // Expand the view.
        //
        val view = inflater!!.inflate(R.layout.fragment_watchlist, container, false)

        //
        // Setup the list.
        //
        createWatchListView(view)

        //
        // Add the new stock button.
        //
        addNewStockButton(view)
        return view
    }

    private fun addNewStockButton(inflate: View) {
        val addStockButton = inflate.findViewById<Button>(R.id.add_stock_to_watchlist_button)
        addStockButton.setOnClickListener { showAddStockDialog() }
    }

    fun showAddStockDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_stock_custom_dialog, null)
        dialogBuilder.setView(dialogView)


        val edt = dialogView.findViewById<EditText>(R.id.stockNameEditText)
        dialogBuilder.setTitle("Add Stock to your watch list")
        dialogBuilder.setMessage("Enter Stock Name")
        dialogBuilder.setPositiveButton("Done") { dialog, whichButton ->
            mStocksArrayList.add(edt.text.toString())
            mStockAdapter!!.notifyDataSetChanged()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, whichButton ->
            //pass
        }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun createWatchListView(inflate: View) {
        mStockAdapter = StockWatchListAdapter(context, R.id.stockNameButton, mStocksArrayList)
        mWatchListView = inflate.findViewById(R.id.watch_list)
        mWatchListView!!.adapter = mStockAdapter
    }


    inner class StockWatchListAdapter(context: Context, resource: Int, private val mItems: MutableList<String>) : ArrayAdapter<String>(context, resource, mItems) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var stockListView = convertView
            if (stockListView == null) {
                val vi: LayoutInflater
                vi = LayoutInflater.from(context)
                stockListView = vi.inflate(R.layout.stock_watch_list_item, null)
            }

            val stockName = getItem(position)
            if (stockName != null) {
                val stockNameButton = stockListView!!.findViewById<Button>(R.id.stockNameButton)
                stockNameButton.text = stockName
                val delButton = stockListView.findViewById<Button>(R.id.deleteButton)
                delButton.setOnClickListener {
                    mItems.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(context, "Deleting Pos " + position, Toast.LENGTH_LONG).show()
                }

                val stockButton = stockListView.findViewById<Button>(R.id.stockNameButton)
                stockButton.setOnClickListener {
                    Toast.makeText(context, "GO TO STOCK " + position, Toast.LENGTH_LONG).show()

                    val fragment = CorrelationFragment()
                    fragment._currentStock = mStocksArrayList[position]
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, fragment)
                            .commit()
                }
            }

            return stockListView
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
}
