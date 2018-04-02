package com.example.leejinseong.nomadhackathone.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.helper.PrefHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_money.*
import com.example.leejinseong.nomadhackathone.model.Money
import com.example.leejinseong.nomadhackathone.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by leejinseong on 2018. 3. 18..
 */
class AddMoneyActivity : AppCompatActivity() {

    internal val realm by lazy { Realm.getDefaultInstance() }

    internal val calendar by lazy {
        Calendar.getInstance()
    }

    internal val pref by lazy {
        PrefHelper.getInstanceOf(this)
    }

    internal val imm by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)

        if(null != intent) {

            val type = intent.getIntExtra("type", -1)

            if(type > 0) {

                /**
                 *  1 -> 하루 금액 입력
                 *  2 -> 가계부 내역 입력
                 */
                if(type == 1) {
                    tvActivityAddMoney.text = "하루 금액을 입력해 주세요"

                    btnActivityAddMoneyAdd.visibility = View.GONE
                    btnActivityAddMoneyOk.visibility = View.VISIBLE
                    btnActivityAddMoneyReset.visibility = View.VISIBLE

                    shownKeyboard()
                    initButton()

                } else if(type == 2) {
                    tvActivityAddMoney.text = "사용 금액을 입력해 주세요"

                    btnActivityAddMoneyAdd.visibility = View.VISIBLE
                    btnActivityAddMoneyOk.visibility = View.GONE
                    btnActivityAddMoneyReset.visibility = View.GONE

                    shownKeyboard()
                    initButton()

                } else {
                    throw IllegalStateException("type error")
                }

            } else {
                throw IllegalStateException("type state error")
            }

        } else {
            throw NullPointerException("This intent in null")
        }

    }

    private fun shownKeyboard() {
        val handler = Handler()
        handler.postDelayed(Runnable { (imm as InputMethodManager).showSoftInput(etActivityAddMoney, 0) }, 100)
    }

    private fun initButton() {

        rlActivityAddMoney.setOnClickListener {
            onBackPressed()
        }

        btnActivityAddMoneyAdd.setOnClickListener {

            if(TextUtils.isEmpty(etActivityAddMoney.text)) {

                showToast("please edit money")

                return@setOnClickListener
            }

            realm.executeTransaction(Realm.Transaction { realm ->

                val perDay = pref.getValue(PrefHelper.PER_DAY, 1)

                if(perDay != 1) {
                    val date = calendar.get(Calendar.DATE) + (perDay-1)
                    calendar.set(Calendar.DATE, date)
                }

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val day = calendar.get(Calendar.DATE)

                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val second = calendar.get(Calendar.SECOND)

                val money = realm.createObject(Money::class.java)

                Dlog.d("$year/$month/$day/$hour/$minute/$second 에 데이터 저장")

                money.date1 = "$year/$month/$day"
                money.date2 = "$year/$month/$day/$hour/$minute/$second"

                money.money = etActivityAddMoney.text.toString().toInt()

            })

            setResult(MainActivity.RESULT_CODE)
            finish()

        }

        btnActivityAddMoneyOk.setOnClickListener {

            if(TextUtils.isEmpty(etActivityAddMoney.text)) {

                showToast("please edit money")

                return@setOnClickListener
            }

            pref.put(PrefHelper.PER_DAY_MONEY, etActivityAddMoney.text.toString().toInt())

            setResult(MainActivity.RESULT_CODE)
            finish()
        }

        btnActivityAddMoneyReset.setOnClickListener {

            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()

            pref.deleteData()

            showToast("data reset")

            setResult(MainActivity.RESULT_CODE)
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}