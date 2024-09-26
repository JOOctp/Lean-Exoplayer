package com.jop.task.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jop.task.R
import com.jop.task.base.ui.BaseRcAdapter
import com.jop.task.data.model.Todo
import com.jop.task.databinding.ItemLayoutParentGroupTaskBinding
import java.util.Locale

class ParentTaskAdapter(private val listener: ParentTaskAdapterListener): BaseRcAdapter<Pair<String, MutableList<Todo>>, ParentTaskAdapter.ViewHolder>(), BaseRcAdapter.OnItemClickListener<Todo> {
    private var todos: MutableList<Pair<String, MutableList<Todo>>> = mutableListOf()
    private lateinit var adapter: TaskAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        adapter = TaskAdapter()
        adapter.setOnClickListener(this)
        return ViewHolder(ItemLayoutParentGroupTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false), adapter)
    }

    class ViewHolder(private val binding: ItemLayoutParentGroupTaskBinding, private val adapter: TaskAdapter) : BaseRcAdapter.ViewHolder<Any>(binding.root) {
        fun bind(item: Pair<String, MutableList<Todo>>) {
            binding.apply {
                tvGroupName.text = item.first.replace("_", " ")

                if(item.first == "pending"){
                    tvGroupName.setTextColor(ContextCompat.getColor(root.context, R.color.orange))
                } else if(item.first == "on_progress"){
                    tvGroupName.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                } else {
                    tvGroupName.setTextColor(ContextCompat.getColor(root.context, R.color.green_success))
                }

                adapter.setData(item.second)
                rvTask.adapter = adapter
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val todo: Pair<String, MutableList<Todo>>? = getItem(position)
            holder.bind(todo!!)
            holder.item = todo
        }
    }


    override fun setData(list: MutableList<Pair<String, MutableList<Todo>>>?) {
        this.todos = list!!
        super.setData(list)
    }


    fun getDataOriginal(): MutableList<Pair<String, MutableList<Todo>>> {
        return this.todos
    }

    interface ParentTaskAdapterListener {
        fun onClickTask(todo: Todo)
    }

    override fun onItemClick(v: View?, position: Int, item: Todo) {
        listener.onClickTask(item)
    }
}