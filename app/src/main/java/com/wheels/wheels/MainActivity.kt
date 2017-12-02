package com.wheels.wheels

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val _fragWatchListName = "TagFragWatchList"
    private val _fragCorrelationName = "TagFragCorrelation"

    /**
     * Maintains a mWatchListView of Fragments for [BottomNavigationView]
     */
    private val _fragments = HashMap<String, Fragment>(3)

    /**
     * Listener for [BottomNavigationView] to help switch to a different fragment.
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(_fragWatchListName);
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                switchFragment(_fragCorrelationName);
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }


    private fun switchFragment(tag: String) {
        val fragment: Fragment = _fragments[tag] ?: WatchlistFragment() as Fragment;
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }

    private fun buildFragmentsList() {
        _fragments.put(_fragWatchListName, WatchlistFragment() as Fragment)
        _fragments.put(_fragCorrelationName, CorrelationFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildFragmentsList()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        switchFragment(_fragWatchListName)
    }
}

