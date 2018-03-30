package com.example.leejinseong.nomadhackathone.ui.calendar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.model.Money
import com.example.leejinseong.nomadhackathone.view.MyCalendarView

import java.text.SimpleDateFormat

import io.realm.Realm
import kotlinx.android.synthetic.main.activity_calendar.*
import java.util.*


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

        Dlog.d("Date : " + Date())

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

        val events = HashSet<Date>()
        events.add(Date())
        //Dlog.d("events : $events")

        with(cvActivityCalendar) {
            updateCalendar(events)

            setEventHandler(object : MyCalendarView.EventHandler {

                override fun onDayPress(date: Date?, view: View) {
                    // show returned day
                    val df = SimpleDateFormat("yyyy/MM/dd")

                    val nowTime = df.format(date);

                    val calendarDatas = realm.where(Money::class.java).equalTo("date1", nowTime).findAllSortedAsync("date2")

                    Dlog.w("nowTime : $nowTime")
                    Dlog.w("calendarDatas : $calendarDatas")

                    val array: ArrayList<Money> = ArrayList()

                    for(item in calendarDatas) {
                        array.add(item)
                    }

                    adapter.setData(array)

                }

                override fun onDayLongPress(date: Date) {
                    // show returned day
                    val df = SimpleDateFormat.getDateInstance()

                    Dlog.i("onDayLongPress : " + df.format(date))

                }
            })
        }

    }

}
