package com.jop.task.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jop.task.base.ui.BaseFragment
import com.jop.task.data.model.DataCalendar
import com.jop.task.data.model.Todo
import com.jop.task.data.viewModel.TaskViewModel
import com.jop.task.data.viewModel.factory.TaskViewModelFactory
import com.jop.task.databinding.FragmentTaskBinding
import com.jop.task.util.DateUtil
import com.jop.task.util.SizeUtil
import com.jop.task.util.SpacingItemColumnAdapter
import com.jop.task.view.adapter.CalendarAdapter
import com.jop.task.view.adapter.ParentTaskAdapter
import com.jop.task.view.task.ManageTaskActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskFragment : BaseFragment(), KodeinAware, CalendarAdapter.CalendarAdapterListener,
    ParentTaskAdapter.ParentTaskAdapterListener {
    override val kodein by kodein()
    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapter: CalendarAdapter
    private lateinit var adapterParentTask: ParentTaskAdapter
    private lateinit var taskVM: TaskViewModel
    private var selectedDate: DataCalendar? = null
    private val taskVMF: TaskViewModelFactory by instance()
    private val calendar: Calendar = Calendar.getInstance()
    private var listOfDay: MutableList<DataCalendar> = mutableListOf()
    private val listDayOfWeek = mutableListOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
    private val dayFormat = SimpleDateFormat("EEE", Locale("id", "ID"))
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    private var monthNow: Int = 1
    private var yearNow: Int = 2022
    private var dateNow: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTaskBinding.inflate(inflater, container, false)

        adapter = CalendarAdapter(this)
        adapterParentTask = ParentTaskAdapter(this)
        monthNow = calendar.get(Calendar.MONTH)
        yearNow = calendar.get(Calendar.YEAR)

        dateNow = dateFormat.format(Date())

        taskVM = ViewModelProvider(requireActivity(), taskVMF).get(TaskViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gridLayout  = object : GridLayoutManager(requireContext(), 7){
            override fun canScrollHorizontally(): Boolean {
                return false
            }
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        binding.apply {
            ivNext.setOnClickListener { getDayOfMonth(true) }
            ivPref.setOnClickListener { getDayOfMonth(false) }

            rcCalender.layoutManager = gridLayout
            rcCalender.addItemDecoration(SpacingItemColumnAdapter(7, SizeUtil.pxFromDp(2f), false))
            rcCalender.adapter = adapter
            rcCalender.isFocusable = false

            rcTodo.adapter = adapterParentTask

            fabAddTask.setOnClickListener {
                startActivity(ManageTaskActivity.newInstance(requireContext()))
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        getDayOfMonth(null)
    }


    private fun getDayOfMonth(isNext: Boolean?){
        listOfDay.clear()
        adapter.clear()
        adapter.getDataOriginal().clear()
        adapter.setSelected(null)

        adapterParentTask.clear()
        adapterParentTask.getDataOriginal().clear()

        if(isNext != null && isNext){
            monthNow += 1
            if(monthNow > 11){
                monthNow = 0
                yearNow += 1
            }

            calendar.set(yearNow, monthNow,1)
        } else if(!(isNext == null || isNext)){
            monthNow -= 1
            if(monthNow < 0){
                monthNow = 11
                yearNow -= 1
            }

            calendar.set(yearNow, monthNow,1)
        }

        for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            listOfDay.add(DataCalendar(date = dateFormat.format(calendar.time)))
        }

        binding.tvMonth.text = monthFormat.format(calendar.time)

        val indexOfDayOfWeek = listDayOfWeek.indexOf(DateUtil.formatDateFromAPI(listOfDay[0].date, dateFormat, dayFormat))

        for(i in 1..indexOfDayOfWeek){
            listOfDay.add(0, DataCalendar())
        }

        taskVM.getTodoLocal().observe(requireActivity()){ data ->
            val groupByDate = data.groupBy { dateFormat.format(it.createdAt) }

            groupByDate.forEach { group ->
                listOfDay.forEach{ _ ->
                    val findData = listOfDay.indexOf(DataCalendar(date = group.key))
                    if(findData >= 0){
                        listOfDay[findData].todo = group.value
                    }
                }
            }

            adapter.setData(listOfDay)

            val findData = listOfDay.find { selectedDate != null && selectedDate!!.date == it.date }

            if(findData != null) onSelectedDate(findData)
            else selectedDate = null

        }
    }

    override fun onSelectedDate(item: DataCalendar) {
        this.selectedDate = item
        adapter.setSelected(item)

        val groupByStatus = item.todo.groupBy { it.status }
        val dataGroup : MutableList<Pair<String, MutableList<Todo>>> = mutableListOf()

        groupByStatus.forEach { group ->
            dataGroup.add(Pair(group.key, group.value.toMutableList()))
        }

        adapterParentTask.clear()
        adapterParentTask.getDataOriginal().clear()
        adapterParentTask.setData(dataGroup)
    }

    override fun onClickTask(todo: Todo) {
        startActivity(ManageTaskActivity.newInstance(requireContext(), todo))
    }
}