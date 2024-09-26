package com.jop.task.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jop.task.R
import com.jop.task.base.ui.BaseRcAdapter
import com.jop.task.data.model.DataCalendar
import com.jop.task.databinding.ItemLayoutDateBinding
import com.jop.task.util.DateUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarAdapter(private val listener: CalendarAdapterListener): BaseRcAdapter<DataCalendar, CalendarAdapter.ViewHolder>() {
    private var calendarList: MutableList<DataCalendar> = mutableListOf()
    private var selectedDate: DataCalendar? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(private val binding: ItemLayoutDateBinding) : BaseRcAdapter.ViewHolder<Any>(binding.root) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        private val displayFormat = SimpleDateFormat("dd", Locale("id", "ID"))
        private val dayNameFormat = SimpleDateFormat("EEE", Locale("id", "ID"))

        fun bind(item: DataCalendar, selectedDate: DataCalendar?, listener: CalendarAdapterListener) {
            binding.apply {
                if(selectedDate != null && item.date == selectedDate.date) {
                    parent.tag = "selected"
                    tvDate.setBackgroundResource(R.drawable.bg_calender_selected)
                } else {
                    parent.tag = "none"
                    tvDate.setBackgroundResource(0)
                }

                ivBadgePending.visibility = if(item.todo.any { it.status == "pending" }) View.VISIBLE else View.GONE
                ivBadgeOnDone.visibility = if(item.todo.any { it.status == "done" }) View.VISIBLE else View.GONE
                ivBadgeOnProgress.visibility = if(item.todo.any { it.status == "on_progress" }) View.VISIBLE else View.GONE

                if(item.date.isNotEmpty()){
                    val dateNow = dateFormat.format(Calendar.getInstance().time)
                    val nameOfDay = DateUtil.formatDateFromAPI(item.date, dateFormat, dayNameFormat)
                    val numberOfDay = DateUtil.formatDateFromAPI(item.date, dateFormat, displayFormat)

                    if(nameOfDay.equals("min", true)){
                        tvDate.setTextColor(root.context.resources.getColor(R.color.red_error))
                    } else {
                        tvDate.setTextColor(root.context.resources.getColor(R.color.black))
                    }

                    if(dateNow.equals(item.date, true)){
                        tvDate.text = Html.fromHtml("<b>${numberOfDay}</b>")
                    } else {
                        tvDate.text = numberOfDay
                    }
                } else {
                    tvDate.text = ""
                    tvDate.setTextColor(root.context.resources.getColor(R.color.black))
                }

                root.setOnClickListener {
                    if(item.date.isNotEmpty()){
                        parent.tag = "selected"
                        tvDate.setBackgroundResource(R.drawable.bg_calender_selected)
                        listener.onSelectedDate(item)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val calender: DataCalendar? = getItem(position)
            holder.item = calender
            holder.setIsRecyclable(false)
            holder.bind(calender!!, selectedDate, listener)
        }
    }

    override fun setData(list: MutableList<DataCalendar>?) {
        this.calendarList = list!!
        super.setData(list)
    }

    fun getDataOriginal(): MutableList<DataCalendar> {
        return this.calendarList
    }

    fun setSelected(item: DataCalendar?) {
        this.selectedDate = item
        notifyDataSetChanged()
    }

    interface CalendarAdapterListener {
        fun onSelectedDate(item: DataCalendar)
    }
}