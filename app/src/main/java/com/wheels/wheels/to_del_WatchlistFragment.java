package com.wheels.wheels;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Watchlist subclass of subclass.
 * Shows the stocks that we are watching.
 */
public class to_del_WatchlistFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private ListView mWatchListView;
    private ArrayAdapter mStockAdapter;
    private ArrayList<String> mStocksArrayList = new ArrayList<>( );

    public to_del_WatchlistFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //
        // Expand the view.
        //
        final View view = inflater.inflate( R.layout.fragment_watchlist, container, false );

        //
        // Setup the list.
        //
        createWatchListView( view );

        //
        // Add the new stock button.
        //
        addNewStockButton( view );
        return view;
    }

    private void addNewStockButton(View inflate) {
        Button addStockButton = inflate.findViewById( R.id.add_stock_to_watchlist_button );
        addStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStockDialog();
            }
        } );
    }

    public void showAddStockDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_stock_custom_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText edt = dialogView.findViewById(R.id.stockNameEditText);
        dialogBuilder.setTitle("Add Stock to your watch list");
        dialogBuilder.setMessage("Enter Stock Name");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mStocksArrayList.add( edt.getText().toString() );
                mStockAdapter.notifyDataSetChanged();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void createWatchListView(View inflate) {
        mStockAdapter = new StockWatchListAdapter( getContext(), R.id.stockNameButton, mStocksArrayList );
        mWatchListView = inflate.findViewById(R.id.watch_list);
        mWatchListView.setAdapter( mStockAdapter );
    }


    public class StockWatchListAdapter extends ArrayAdapter<String> {
        private final List<String> mItems;

        public StockWatchListAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
            mItems = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View stockListView = convertView;
            if (stockListView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                stockListView = vi.inflate(R.layout.stock_watch_list_item, null);
            }

            String stockName = getItem(position);
            if (stockName != null) {
                Button stockNameButton = stockListView.findViewById(R.id.stockNameButton);
                stockNameButton.setText(  stockName );
                Button delButton = stockListView.findViewById( R.id.deleteButton );
                delButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItems.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText( getContext(), "Deleting Pos " + position , Toast.LENGTH_LONG ).show();
                    }
                } );

                Button stockButton = stockListView.findViewById( R.id.stockNameButton );
                stockButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText( getContext(), "GO TO STOCK " + position , Toast.LENGTH_LONG ).show();

                        CorrelationFragment fragment = new CorrelationFragment();
                        fragment.set_currentStock( mStocksArrayList.get( position ) );
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_layout, fragment)
                                .commit();
                    }
                } );
            }

            return stockListView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
    }

    @Override
    public void onDetach() {
        super.onDetach();
     }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
}
