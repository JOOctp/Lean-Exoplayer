package com.jop.task.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.jop.task.R
import com.jop.task.data.model.Spinner

class SpinnerAdapter(ctx: Context, spinner: ArrayList<Spinner>): ArrayAdapter<Spinner>(ctx, 0, spinner) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val role = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.item_layout_spinner, parent, false)
        val tvRole = view.findViewById<TextView>(R.id.tv_item)

        role?.let {
            tvRole.text = role.label
        }

        return view
    }
}