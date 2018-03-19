package com.example.leejinseong.nomadhackathone.ui.main

import android.content.Intent
import android.graphics.Color
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
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() , MainAdapter.ItemClickListener {

    companion object {

        val PER_DAY_MONEY = "per_day_money"

        val REQUEST_CODE = 100
        val RESULT_CODE = 101

    }

    internal var dialogMessage: String? = null

    internal val adapter by lazy {
        MainAdapter().apply { setItemClickListener(this@MainActivity) }
    }

    internal val realm by lazy {
        Realm.getDefaultInstance()
    }

    internal val datas by lazy {

        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val nowTime = sdf.format(date)

        realm.where(Money::class.java).equalTo("date1", nowTime).findAllSortedAsync("date2")
    }

    internal var perDay = 1

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
        //showToast("itemClick")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Dlog.d("requestCode : $requestCode , resultCode : $resultCode")
        if(resultCode == RESULT_CODE) {
            initPerMoney()
        }
    }

    private fun initView() {

        ivActivityMainCalendar.setColorFilter(Color.parseColor("#ffffff"))

        tvActivityMainTitle.text = "$perDay 일 차"

        initPerMoney()

        with(rvActivityMain) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        adapter.setData(datas)

        datas.addChangeListener({ money ->
            Dlog.w("money : " + money)

            if(datas.size == 0) {

                tvActivityMainDefault.visibility = View.VISIBLE

            } else {

                tvActivityMainDefault.visibility = View.GONE

            }

            adapter.notifyDataSetChanged() // UI를 갱신합니다.
        })

    }

    private fun initButton() {

        rlActivityMainCalendar.setOnClickListener {
            startActivity(Intent(this@MainActivity, CalendarActivity::class.java))
        }

        cvActivityMainLeftMoney.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, AddMoneyActivity::class.java)
                    .putExtra("type", 1), REQUEST_CODE)
        }

        rlActivityMainCharacter.setOnClickListener {
            if(dialogMessage == null) {
                MainMessageDialog(this).show()
            } else {
                MainMessageDialog(this, dialogMessage).show()
                dialogMessage = null
            }

        }

        fabActivityMain.setOnClickListener {

            val perDayMoney = PreferenceManager.getDefaultSharedPreferences(this).getString(PER_DAY_MONEY, null)

            if(null != perDayMoney) {

                startActivityForResult(Intent(this@MainActivity, AddMoneyActivity::class.java)
                        .putExtra("type", 2), REQUEST_CODE)

            } else {

                showToast("하루 금액을 먼저 입력해 주세요")

            }

        }

        ivActivityMainNext.setOnClickListener {
            perDay++

            initViewNext()
        }

        ivActivityMainBefore.setOnClickListener {
            perDay--

            rvActivityMain.visibility = View.VISIBLE
            tvActivityMainDefault.visibility = View.GONE

            dialogMessage = "오늘 치킨 사먹어서 과소비 했어요 ㅜㅠ \n님들은 그러지 마세요"

            initView()
        }

    }

    private fun initPerMoney() {

        val perDayMoney = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PER_DAY_MONEY, null)

        perDay = 1
        tvActivityMainTitle.text = "$perDay 일차"

        if(null == perDayMoney) {

            tvActivityMainOneDayMoney.text = "하루살이"
            tvActivityMainRemainMoneyExplain.text = "하루 동안 살\n금액을 입력해 주세요"
            ivActivityMainCharacter.setImageResource(R.drawable.ic_tag_faces_white_24dp)
            tvActivityMainRemainMoney.visibility = View.GONE

        } else {

            tvActivityMainOneDayMoney.text = "하루 $perDayMoney 살기"

            tvActivityMainRemainMoneyExplain.text = "남은 금액"

            var remainMoney = perDayMoney.toInt()
            for(money in  datas) {

                remainMoney -= money.money.toInt()
            }

            tvActivityMainRemainMoney.text = remainMoney.toString() + "원"

            tvActivityMainRemainMoney.visibility = View.VISIBLE

            if(remainMoney <= -30000) {
                ivActivityMainCharacter.setImageResource(R.drawable.ic_mood_bad_white_24dp)
            } else if(remainMoney < 0 && remainMoney > -30000) {
                ivActivityMainCharacter.setImageResource(R.drawable.ic_face_white_24dp)
            } else {
                ivActivityMainCharacter.setImageResource(R.drawable.ic_tag_faces_white_24dp)
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initViewNext() {

        val perDayMoney = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(PER_DAY_MONEY, null)

        tvActivityMainTitle.text = "$perDay 일차"

        var remainMoney = perDayMoney.toInt()
        tvActivityMainRemainMoney.text = remainMoney.toString() + "원"
        rvActivityMain.visibility = View.GONE
        tvActivityMainDefault.visibility = View.VISIBLE

    }

}
