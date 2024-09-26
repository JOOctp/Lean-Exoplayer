package com.jop.task.view.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jop.task.R
import com.jop.task.base.ui.BaseRcAdapter
import com.jop.task.data.model.Todo
import com.jop.task.databinding.ItemLayoutTaskBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter: BaseRcAdapter<Todo, TaskAdapter.ViewHolder>() {
    private var todos: MutableList<Todo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(private val binding: ItemLayoutTaskBinding) : BaseRcAdapter.ViewHolder<Any>(binding.root) {
        private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))

        fun bind(item: Todo) {
            binding.apply {
                tvStatus.text = item.status.replace("_", " ").replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                
                tvTaskName.text = item.name
                tvTaskSubject.text = item.subject
                tvDescTask.text = item.desc
                tvDate.text = dateFormat.format(item.createdAt)

                if(item.status == "pending"){
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.accent_orange))
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.orange))
                } else if(item.status == "on_progress"){
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.accent_yellow))
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.yellow))
                } else {
                    tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.accent_green_success))
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.green_success))
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val todo: Todo? = getItem(position)
            holder.bind(todo!!)
            holder.item = todo
        }
        super.onBindViewHolder(holder, position)
    }


    override fun setData(list: MutableList<Todo>?) {
        this.todos = list!!
        super.setData(list)
    }

    fun getDataOriginal(): MutableList<Todo> {
        return this.todos
    }
}