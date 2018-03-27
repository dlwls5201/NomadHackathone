package com.example.leejinseong.nomadhackathone.ui

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.leejinseong.nomadhackathone.Dlog
import com.example.leejinseong.nomadhackathone.R
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_money.*
import com.example.leejinseong.nomadhackathone.model.Money
import com.example.leejinseong.nomadhackathone.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by leejinseong on 2018. 3. 18..
 */
class AddMoneyActivity : AppCompatActivity() {

    val realm by lazy { Realm.getDefaultInstance() }

    var perDay : Int? = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)

        if(null != intent) {
            val type = intent.getIntExtra("type", -1)

            perDay = intent.getIntExtra("perDay", 1)

            //Dlog.d("type : " + type)
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

                } else if(type == 2) {
                    tvActivityAddMoney.text = "사용 금액을 입력해 주세요"

                    btnActivityAddMoneyAdd.visibility = View.VISIBLE
                    btnActivityAddMoneyOk.visibility = View.GONE
                    btnActivityAddMoneyReset.visibility = View.GONE

                }

            } else {
                throw IllegalStateException("type state error")
            }

        } else {

            throw NullPointerException("This intent in null")
        }

        rlActivityAddMoney.setOnClickListener {
            onBackPressed()
        }

        btnActivityAddMoneyAdd.setOnClickListener {

            if(TextUtils.isEmpty(etActivityAddMoney.text)) {

                showToast("please edit money")

                return@setOnClickListener
            }

            realm.executeTransaction(Realm.Transaction { realm ->

                val now = System.currentTimeMillis()
                val date = Date(now)

               /* val sdf = SimpleDateFormat("yyyy/MM/dd")
                val nowTime = sdf.format(date)

                val sdf2 = SimpleDateFormat("yyyy/MM/dd/HH/mm/ss")
                val nowTime2 = sdf2.format(date)*/

                val sdfY = SimpleDateFormat("yyyy")
                val nowY = sdfY.format(date)

                val sdfM = SimpleDateFormat("MM")
                val nowM = sdfM.format(date)

                val sdfD = SimpleDateFormat("dd")
                val nowD = sdfD.format(date)

                var nowTempD = nowD.toInt()

                if(perDay != 1) {

                    nowTempD += (perDay!! -1)

                }

                val sdfTime = SimpleDateFormat("HH/mm/ss")
                val nowTime = sdfTime.format(date)

                val money = realm.createObject(Money::class.java)

                Dlog.d("$nowY/$nowM/$nowTempD/$nowTime 에 데이터 저장")

                money.date1 = "$nowY/$nowM/$nowTempD"
                money.date2 = "$nowY/$nowM/$nowTempD/$nowTime"
                money.money = etActivityAddMoney.text.toString()

            })

            setResult(MainActivity.RESULT_CODE)
            finish()

        }

        btnActivityAddMoneyOk.setOnClickListener {

            if(TextUtils.isEmpty(etActivityAddMoney.text)) {

                showToast("please edit money")

                return@setOnClickListener
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(MainActivity.PER_DAY_MONEY, etActivityAddMoney.text.toString())
                    .apply()

            setResult(MainActivity.RESULT_CODE)
            finish()
        }

        btnActivityAddMoneyReset.setOnClickListener {

            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .clear().apply()

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