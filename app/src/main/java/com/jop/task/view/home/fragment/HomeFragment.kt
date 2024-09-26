package com.jop.task.view.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.jop.task.R
import com.jop.task.base.ui.BaseFragment
import com.jop.task.data.model.DataCalendar
import com.jop.task.data.model.Todo
import com.jop.task.data.viewModel.TaskViewModel
import com.jop.task.data.viewModel.factory.TaskViewModelFactory
import com.jop.task.databinding.FragmentHomeBinding
import com.jop.task.databinding.FragmentTaskBinding
import com.jop.task.view.adapter.CalendarAdapter
import com.jop.task.view.adapter.ParentTaskAdapter
import com.jop.task.view.task.ManageTaskActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.Calendar
import java.util.Date

class HomeFragment : BaseFragment(), KodeinAware, ParentTaskAdapter.ParentTaskAdapterListener {
    override val kodein by kodein()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ParentTaskAdapter
    private lateinit var taskVM: TaskViewModel
    private val taskVMF: TaskViewModelFactory by instance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ParentTaskAdapter(this)
        taskVM = ViewModelProvider(requireActivity(), taskVMF).get(TaskViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            rcTodo.adapter = adapter
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClickTask(todo: Todo) {
        startActivity(ManageTaskActivity.newInstance(requireContext(), todo))
    }

    override fun onStart() {
        taskVM.getTodoLocal().observe(requireActivity()){ data ->
            val groupByStatus = data.groupBy { it.status }
            val dataGroup : MutableList<Pair<String, MutableList<Todo>>> = mutableListOf()

            groupByStatus.forEach { group ->
                dataGroup.add(Pair(group.key, group.value.take(3).toMutableList()))
            }

            adapter.setData(dataGroup)
        }

        super.onStart()
    }
}