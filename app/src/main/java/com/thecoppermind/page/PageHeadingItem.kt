package com.thecoppermind.page

import android.view.ViewGroup
import android.widget.TextView
import com.thecoppermind.R
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface
import com.thecoppermind.recyclerView.refactor.RecyclerHolder

data class PageHeadingListItem(val blockNumber: Int, val text: String) : RecyclerItemViewInterface {

    override val itemViewType: Int = viewType

    override fun bind(holder: RecyclerHolder) {
        (holder as PageHeadingViewInterface).setText(text)
    }

    override val itemId: Any = blockNumber
    override val dataToCompare: List<Any>
        get() = listOf(text)

    companion object {
        const val viewType: Int = PageHeadingView.layoutId
    }
}

interface PageHeadingViewInterface {
    fun setText(text: String)
}

class PageHeadingView(parent: ViewGroup) : RecyclerHolder(parent, layoutId), PageHeadingViewInterface {

    // ----- инициализация полей -----
    val textView: TextView = itemView.findViewById(R.id.page_list_item_heading_text)

    // ----- команды по заполнению полей от адаптера -----

    override fun setText(text: String) {
        textView.text = text
    }

    // ----- файл разметки для вьюшки -----
    companion object {
        const val layoutId: Int = R.layout.page_list_item_heading
    }
}
