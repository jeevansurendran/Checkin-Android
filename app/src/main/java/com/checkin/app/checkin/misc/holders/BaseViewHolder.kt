package com.checkin.app.checkin.misc.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<in T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindData(data: T)
}
