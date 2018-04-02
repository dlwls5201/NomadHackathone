package com.example.leejinseong.nomadhackathone.ui.calendar

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.leejinseong.nomadhackathone.R
import com.example.leejinseong.nomadhackathone.model.Money
import com.example.leejinseong.nomadhackathone.ui.main.MainAdapter
import io.realm.RealmResults

/**
 * Created by leejinseong on 2018. 3. 17..
 */
class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.MoneyHolder>() {

    private var datas: RealmResults<Money>? = null

    private var listener: ItemClickListener? = null

    fun setItemClickListener(listener: ItemClickListener?) {
        this.listener = listener
    }
    fun setData(datas: RealmResults<Money>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyHolder {
        return MoneyHolder(parent)
    }

    override fun onBindViewHolder(holder: MoneyHolder, position: Int) {

        val item = datas?.get(position)

        with(holder) {

            tvMoneyItemTitle.text = "${item?.money}원 사용"
            itemView.setOnClickListener { listener?.onItemClick() }

        }

    }

    override fun getItemCount(): Int = datas?.size!!

    class MoneyHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_money_calendar, parent, false)) {

        var tvMoneyItemTitle: TextView

        init {

            tvMoneyItemTitle = itemView.findViewById(R.id.tvMoneyItemTitle)

        }
    }

    interface ItemClickListener {

        fun onItemClick()

    }

}