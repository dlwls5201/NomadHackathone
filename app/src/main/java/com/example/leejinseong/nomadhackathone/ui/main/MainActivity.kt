package com.example.leejinseong.nomadhackathone.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.Movie
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.helper.PrefHelper
import com.example.leejinseong.nomadhackathone.model.Money
import com.example.leejinseong.nomadhackathone.ui.AddMoneyActivity
import com.example.leejinseong.nomadhackathone.ui.calendar.CalendarActivity
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() , MainAdapter.ItemClickListener {

    companion object {

        val REQUEST_CODE = 100
        val RESULT_CODE = 101

    }

    internal val realm by lazy {
        Realm.getDefaultInstance()
    }

    internal val pref by lazy {
        PrefHelper.getInstanceOf(this)
    }

    internal val adapter by lazy {
        MainAdapter().apply { setItemClickListener(this@MainActivity) }
    }

    internal lateinit var datas : RealmResults<Money>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initButton()

    }

    override fun onDestroy() {
        super.onDestroy()

        datas?.removeAllChangeListeners()
        realm.close()

    }

    override fun onItemClick(money: Money?) {
        MainDialog(this, money).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Dlog.d("requestCode : $requestCode , resultCode : $resultCode")
        if(resultCode == RESULT_CODE) {
            initView()
        }
    }

    private fun initView() {

        ivActivityMainCalendar.setColorFilter(Color.parseColor("#ffffff"))

        with(rvActivityMain) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        val perDay = pref.getValue(PrefHelper.PER_DAY, 1)

        tvActivityMainTitle.text = "$perDay 일 차"

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

        val perDayMoney = pref.getValue(PrefHelper.PER_DAY_MONEY, -1)

        if(perDayMoney == -1) {

            tvActivityMainOneDayMoney.text = "하루살이"
            tvActivityMainRemainMoneyExplain.text = "하루 동안 살\n금액을 입력해 주세요"
            tvActivityMainRemainMoney.visibility = View.GONE

        } else {

            tvActivityMainRemainMoneyExplain.text = "남은 금액"
            tvActivityMainOneDayMoney.text = "하루 $perDayMoney 살기"

            datas.removeAllChangeListeners()
            datas.addChangeListener({ money ->
                Dlog.w("size : " + datas.size + " , money : " + money)

                if(datas.size == 0) {

                    tvActivityMainDefault.visibility = View.VISIBLE
                    tvActivityMainRemainMoney.text = perDayMoney.toString() + "원"
                    tvActivityMainRemainMoney.visibility = View.VISIBLE

                } else {

                    tvActivityMainDefault.visibility = View.GONE

                    var tempPerDayMoney = perDayMoney

                    for(money in datas) {

                        tempPerDayMoney -= money.money!!
                    }

                    tvActivityMainRemainMoney.text = tempPerDayMoney.toString() + "원"
                    tvActivityMainRemainMoney.visibility = View.VISIBLE

                }

                adapter.notifyDataSetChanged() // UI를 갱신합니다.
            })

        }




    }


    private fun initButton() {

        rlActivityMainCalendar.setOnClickListener {
            startActivity(Intent(this@MainActivity, CalendarActivity::class.java))
        }

        /**
         *  1 -> 하루 금액 입력
         *  2 -> 가계부 내역 입력
         */

        cvActivityMainLeftMoney.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, AddMoneyActivity::class.java)
                    .putExtra("type", 1), REQUEST_CODE)
        }

        fabActivityMain.setOnClickListener {

            val perDayMoney = pref.getValue(PrefHelper.PER_DAY_MONEY, -1)

            if(perDayMoney == -1) {

                showToast("하루 금액을 먼저 입력해 주세요")

            } else {

                startActivityForResult(Intent(this@MainActivity, AddMoneyActivity::class.java)
                        .putExtra("type", 2), REQUEST_CODE)

            }

        }

        // 전날 다음날 이동
        ivActivityMainNext.setOnClickListener {
            var perDay = pref.getValue(PrefHelper.PER_DAY, 1)
            pref.put(PrefHelper.PER_DAY, (perDay + 1))

            initView()
        }

        ivActivityMainBefore.setOnClickListener {
            var perDay = pref.getValue(PrefHelper.PER_DAY, 1)

            if(perDay > 1) {

                pref.put(PrefHelper.PER_DAY, (perDay - 1))

                initView()

            } else {

                showToast("perDay == 1")

            }

        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
