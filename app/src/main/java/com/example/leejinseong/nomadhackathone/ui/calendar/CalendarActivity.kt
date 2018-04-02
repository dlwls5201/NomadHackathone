package com.example.leejinseong.nomadhackathone.ui.calendar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.helper.PrefHelper
import com.example.leejinseong.nomadhackathone.model.Money
import com.example.leejinseong.nomadhackathone.view.MyCalendarView

import java.text.SimpleDateFormat

import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


/**
 * Created by leejinseong on 2018. 3. 18..
 */

class CalendarActivity : AppCompatActivity(), CalendarAdapter.ItemClickListener {

    internal val realm by lazy { Realm.getDefaultInstance() }

    internal val pref by lazy { PrefHelper.getInstanceOf(this) }

    internal val adapter by lazy { CalendarAdapter() }

    internal lateinit var datas : RealmResults<Money>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initView()
        initButton()

        Dlog.d("Date : " + Date())

    }

    override fun onDestroy() {
        super.onDestroy()

        datas?.removeAllChangeListeners()
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

        val perDay = pref.getValue(PrefHelper.PER_DAY, 1)

        val calendar = Calendar.getInstance();

        if(perDay != 1) {
            val date = calendar.get(Calendar.DATE) + (perDay-1)
            calendar.set(Calendar.DATE, date)
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DATE)

        Dlog.d("initView : $year/$month/$day")

        datas = realm.where(Money::class.java).equalTo("date1", "$year/$month/$day").findAllSortedAsync("date2")

        adapter.setData(datas)

        datas.removeAllChangeListeners()

        datas.addChangeListener({ money ->
            Dlog.w("size : " + datas.size + " , money : " + money)

            if(datas.size == 0) {


            } else {


            }

            adapter.notifyDataSetChanged() // UI를 갱신합니다.
        })


        val events = HashSet<Date>()
        events.add(Date())

        with(cvActivityCalendar) {

            updateCalendar(events)

            setEventHandler(object : MyCalendarView.EventHandler {

                override fun onDayPress(date: Date?, view: View) {
                    // show returned day
                    val year = SimpleDateFormat("yyyy").format(date).toInt()
                    val month = SimpleDateFormat("MM").format(date).toInt()
                    val day = SimpleDateFormat("dd").format(date).toInt()

                    val nowTime = "$year/$month/$day"

                    datas = realm.where(Money::class.java).equalTo("date1", nowTime).findAllSortedAsync("date2")

                    Dlog.w("nowTime : $nowTime")
                    Dlog.w("datas : $datas")

                    adapter.setData(datas)

                    datas.removeAllChangeListeners()

                    datas.addChangeListener({ money ->
                        Dlog.v("size : " + datas.size + " , money : " + money)

                        if(datas.size == 0) {


                        } else {


                        }

                        adapter.notifyDataSetChanged() // UI를 갱신합니다.
                    })

                }

                override fun onDayLongPress(date: Date) {
                    // show returned day
                    val df = SimpleDateFormat.getDateInstance()

                    Dlog.e("onDayLongPress ")

                }
            })
        }

    }

    override fun onItemClick() {
        Dlog.e("onItemClick")
    }


}
