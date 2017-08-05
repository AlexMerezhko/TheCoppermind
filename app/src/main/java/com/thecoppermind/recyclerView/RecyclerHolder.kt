package com.thecoppermind.recyclerView.refactor

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class RecyclerHolder(parent: ViewGroup, layoutId: Int) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false)) {

    //    private val isOutOfRecycleView: Boolean // TODO решение для вьюшек, которые забираются из адаптера напрямую, а не в отдаются в recyclerView

//    constructor(view: View) : super(view) {
//        isOutOfRecycleView = false
//    }

//    constructor(itemView: View, isOutOfRecycleView: Boolean) : super(itemView) {
//        this.isOutOfRecycleView = isOutOfRecycleView
//    }

    fun canHandleClick(): Boolean {
//        return isOutOfRecycleView || adapterPosition != RecyclerView.NO_POSITION
        return adapterPosition != RecyclerView.NO_POSITION
    }
}