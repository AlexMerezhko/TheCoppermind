package com.thecoppermind.recyclerView

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface

class RecyclerHelper {

    companion object {

        fun updateDataInUniversalRecyclerView(recyclerView: RecyclerView, newData: ArrayList<RecyclerItemViewInterface>) {
            updateDataInUniversalAdapter(recyclerView.adapter as RecyclerAdapter, newData)
        }

        fun updateDataInUniversalAdapter(adapter: RecyclerAdapter, newData: ArrayList<RecyclerItemViewInterface>) {
            val oldData = adapter.setData(newData)
            getDiffResultUniversal(oldData, newData).dispatchUpdatesTo(adapter)
        }

        private fun getDiffResultUniversal(oldList: List<RecyclerItemViewInterface>, newList: ArrayList<RecyclerItemViewInterface>): DiffUtil.DiffResult {
            val cb = object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldList.size

                override fun getNewListSize(): Int = newList.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return when {
                        oldList[oldItemPosition] === newList[newItemPosition] -> true
                        oldList[oldItemPosition]::class == newList[newItemPosition]::class -> oldList[oldItemPosition].areItemsTheSame(newList[newItemPosition])
                        else -> false
                    }
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return when {
                        oldList[oldItemPosition] === newList[newItemPosition] -> return true
                        else -> oldList[oldItemPosition].areContentsTheSame(newList[newItemPosition])
                    }
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
                    return oldList[oldItemPosition].getPayload(newList[newItemPosition])
                }
            }
            return DiffUtil.calculateDiff(cb)
        }
    }
}
