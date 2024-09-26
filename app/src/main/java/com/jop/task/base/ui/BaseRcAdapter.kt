package com.jop.task.base.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class BaseRcAdapter<T, VH : BaseRcAdapter.ViewHolder<*>?> : RecyclerView.Adapter<VH>() {
    private var list: MutableList<T> = ArrayList()
    private var pageIndex = 1
    private var rowIndex = -1
    private lateinit var onItemClickListener: OnItemClickListener<T>
    private lateinit var onItemLongClickListener: OnItemLongClickListener<T>

    fun getItem(position: Int): T? {
        return if (list.size > position) {
            list[position]
        } else null
    }

    override fun getItemCount(): Int {
        return list.size
    }

    open fun setData(list: MutableList<T>?) {
        if (list != null) {
            this.list = list
        }
        notifyDataSetChanged()
    }

    fun setData(t: T, index: Int) {
        if (list.size > index) {
            list[index] = t
            notifyItemChanged(index)
        }
    }

    open fun removeData(index: Int) {
        if (list.size > index) {
            list.removeAt(index)
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, list.size)
        }
    }

    open fun setEditData(list: MutableList<T>?) {
        if (list != null) {
            this.list = list
        }
    }

    open fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    open fun addData(pageIndex: Int, list: List<T>?) {
        this.pageIndex = pageIndex
        addData(list)
    }

    open fun addData(list: List<T>?) {
        val listSize = this.list.size
        this.list.addAll(list!!)
        notifyItemInserted(listSize)
    }

    open fun getData(): MutableList<T>? {
        return list
    }

    override fun onBindViewHolder(holder: VH & Any, @SuppressLint("RecyclerView") position: Int) {
        holder.itemView.setOnClickListener {
            rowIndex = position
            onItemClickListener.onItemClick(holder.itemView, position, holder.item as T)
            notifyDataSetChanged()
        }
    }
//    override fun onBindViewHolder(holder: VH & Any, position: Int) {
//        holder!!.itemView.setOnClickListener {
//            rowIndex = position
//            onItemClickListener.onItemClick(holder.itemView, position, holder.item as T)
//            notifyDataSetChanged()
//        }
//    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener<T>) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnClickLongListener(onItemLongClickListener: OnItemLongClickListener<T>) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    interface OnItemClickListener<T> {
        fun onItemClick(v: View?, position: Int, item: T)
    }

    interface OnItemLongClickListener<T> {
        fun onItemLongClick(v: View?, position: Int, item: T)
    }

    abstract class ViewHolder<T>(view: View?) : RecyclerView.ViewHolder(view!!) {
        var item: T? = null
    }
}