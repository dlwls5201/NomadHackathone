package com.example.leejinseong.nomadhackathone.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.Movie
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
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

        val PER_DAY_MONEY = "per_day_money"

        val REQUEST_CODE = 100
        val RESULT_CODE = 101

    }

    val now = System.currentTimeMillis()
    val date = Date(now)

    val sdfY = SimpleDateFormat("yyyy")
    val nowY = sdfY.format(date)

    val sdfM = SimpleDateFormat("MM")
    val nowM = sdfM.format(date)

    val sdfD = SimpleDateFormat("dd")
    val nowD = sdfD.format(date)

    internal val adapter by lazy {
        MainAdapter().apply { setItemClickListener(this@MainActivity) }
    }

    internal val realm by lazy {
        Realm.getDefaultInstance()
    }

    internal var perDay = 1

    internal lateinit var datas : RealmResults<Money>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initButton()

    }

    override fun onDestroy() {
        super.onDestroy()

        datas.removeAllChangeListeners()
        realm.close()

    }

    override fun onItemClick() {
        showToast("itemClick")
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

        tvActivityMainTitle.text = "$perDay 일 차"

        with(rvActivityMain) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        var nowTempD = nowD.toInt()

        Dlog.d("initView perDay : " + perDay + " , nowTempD : " + nowTempD)

        if(perDay > 1) {

            nowTempD += (perDay - 1)

            datas.removeAllChangeListeners()
            datas = realm.where(Money::class.java).equalTo("date1", "$nowY/$nowM/$nowTempD").findAllSortedAsync("date2")

        } else {

            datas = realm.where(Money::class.java).equalTo("date1", "$nowY/$nowM/$nowTempD").findAllSortedAsync("date2")

        }

        adapter.setData(datas)

        val perDayMoney = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PER_DAY_MONEY, null)

        if(null == perDayMoney) {

            tvActivityMainOneDayMoney.text = "하루살이"
            tvActivityMainRemainMoneyExplain.text = "하루 동안 살\n금액을 입력해 주세요"
            tvActivityMainRemainMoney.visibility = View.GONE

        } else {

            tvActivityMainRemainMoneyExplain.text = "남은 금액"
            tvActivityMainOneDayMoney.text = "하루 $perDayMoney 살기"

            datas.addChangeListener({ money ->
                Dlog.w("size : " + datas.size + " , money : " + money)

                var remainMoney = perDayMoney.toInt()

                if(datas.size == 0) {

                    tvActivityMainDefault.visibility = View.VISIBLE
                    tvActivityMainRemainMoney.text = remainMoney.toString() + "원"
                    tvActivityMainRemainMoney.visibility = View.VISIBLE

                } else {

                    tvActivityMainDefault.visibility = View.GONE

                    for(money in datas) {

                        remainMoney -= money.money!!.toInt()
                    }

                    tvActivityMainRemainMoney.text = remainMoney.toString() + "원"
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

            val perDayMoney = PreferenceManager.getDefaultSharedPreferences(this).getString(PER_DAY_MONEY, null)

            if(null != perDayMoney) {

                startActivityForResult(Intent(this@MainActivity, AddMoneyActivity::class.java)
                        .putExtra("type", 2).putExtra("perDay", perDay), REQUEST_CODE)

            } else {

                showToast("하루 금액을 먼저 입력해 주세요")

            }

        }

        // TODO
        // 전날 다음날 이동
        ivActivityMainNext.setOnClickListener {
            perDay++

            initView()
        }

        ivActivityMainBefore.setOnClickListener {
            if(perDay > 1) {

                perDay--

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
