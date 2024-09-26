package com.jop.task.base.ui

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.jop.task.R

class CustomSnackbar (parent: ViewGroup, content: CustomSnackbarView) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP

        getView().setBackgroundColor(ContextCompat.getColor(view.context, R.color.transparent))
        getView().setPadding(0, 0, 0, 0)
        this.animationMode = ANIMATION_MODE_FADE
    }

    companion object {

        fun make(view: View, message : String, duration : Int, bg_color : Int, bg_icon : Int): CustomSnackbar? {

            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                    "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try{
                val customView = LayoutInflater.from(view.context).inflate(
                        R.layout.custom_snackbar_layout,
                        parent,
                        false
                ) as CustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
                customView.ivIcon.setImageResource(bg_icon)
                customView.layRoot.setCardBackgroundColor(ContextCompat.getColor(view.context, bg_color))


                return CustomSnackbar(parent, customView).setDuration(duration)
            }catch ( e: Exception){
                Log.v("exception ",e.message!!)
            }

            return null
        }

    }
}

internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                fallback = view
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback
}