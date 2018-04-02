package com.example.leejinseong.nomadhackathone.ui.main

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.model.Money
import io.realm.Realm
import kotlinx.android.synthetic.main.dialog_main.*


/**
 * Created by iwedding on 2018. 4. 2..
 */
class MainDialog(mActivity: Activity, money: Money?) : Dialog(mActivity) {

    val imm: InputMethodManager

    val mRealm: Realm

    val mMoney: Money?

    init {

        imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        mRealm = Realm.getDefaultInstance()

        mMoney = money
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        setContentView(R.layout.dialog_main)

        //show keyboard
        val handler = Handler()
        handler.postDelayed(Runnable { imm.showSoftInput(etDialogMoney, 0) }, 100)

        //set data
        etDialogMoney.setText(mMoney?.money.toString())

        //btn
        btnDialogMainDelete.setOnClickListener {

            mRealm.beginTransaction()
            mMoney?.deleteFromRealm()
            mRealm.commitTransaction()

            dismiss()

        }

        btnDialogMainModify.setOnClickListener {

           val money =  etDialogMoney.text.toString()

            if(TextUtils.isDigitsOnly(money)) {

                mRealm.beginTransaction()

                mMoney?.money = etDialogMoney.text.toString().toInt()

                mRealm.commitTransaction()

                dismiss()

            }

        }
    }

}