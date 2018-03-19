package com.example.leejinseong.nomadhackathone.ui.calendar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.model.Money

import java.text.SimpleDateFormat
import java.util.Date

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_calendar.*
import android.widget.CalendarView.OnDateChangeListener
import com.example.leejinseong.nomadhackathone.ui.main.MainAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by leejinseong on 2018. 3. 18..
 */

class CalendarActivity : AppCompatActivity() {

    internal val realm by lazy { Realm.getDefaultInstance() }

    internal val adapter by lazy { CalendarAdapter() }

    internal val datas by lazy {

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val nowTime = sdf.format(date)

        realm.where(Money::class.java).equalTo("date1", nowTime).findAllSortedAsync("date2")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initView()
        initButton()

    }

    override fun onDestroy() {
        super.onDestroy()

        datas.removeAllChangeListeners()
        realm.close()

    }

    private fun initButton() {
        rlActivityCalendar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {

        with(rvActivityCalendar) {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = this@CalendarActivity.adapter
        }

        val array: ArrayList<Money> = ArrayList()

        for(item in datas) {
            array.add(item)
        }

        adapter.setData(array)

        cvActivityCalendar.setOnDateChangeListener(OnDateChangeListener {
            arg0, year, month, date ->

            var nowTime : String?

            val month = month + 1

            if(month < 10) {

                nowTime = "$year/0$month/$date"

            } else {

                nowTime = "$year/$month/$date"

            }

            //Dlog.d("nowTime : $nowTime")

            val calendarDatas = realm.where(Money::class.java).equalTo("date1", nowTime).findAllSortedAsync("date2")
            //Dlog.d("calendarDatas : $datas")

            val array: ArrayList<Money> = ArrayList()

            for(item in calendarDatas) {
                array.add(item)
            }

            adapter.setData(array)

        })

    }
}
