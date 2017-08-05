package com.thecoppermind.recyclerView

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface
import com.thecoppermind.recyclerView.refactor.RecyclerHolder
import java.util.*

abstract class RecyclerAdapter : RecyclerView.Adapter<RecyclerHolder>() {

    private var data: ArrayList<RecyclerItemViewInterface> = ArrayList()

    fun setData(data: ArrayList<RecyclerItemViewInterface>): ArrayList<RecyclerItemViewInterface> {
        val oldData = ArrayList(this.data)
        this.data = data
        return oldData
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].itemViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        data[position].bind(holder)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int, payloads: List<Any>) {
        if (!payloads.isEmpty()) {
            data[position].bindWithPayload(holder, payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    /**
     * Если вернёться null, значит не всем элементам, которые были добавлены в адаптер, были указаны способы получения соответствующим им холдерам
     */
    open fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        TODO("Переопределить и указать холдеры для всех доступных адаптеру viewType")
    }


    // ----- Для GridLayoutManager -----

    open fun getTopSpaceHeight(viewType: Int): Int {
        return NO_EXTRA_SPACE;
    }

    open fun spanSizeForViewType(viewType: Int): Int = NO_SPANS

    open fun spansCount(): Int = NO_SPANS

    fun useSpansForItems(): Boolean = spansCount() == NO_SPANS

    companion object {
        private const val NO_EXTRA_SPACE = 0
        private const val NO_SPANS: Int = 1
    }
}
