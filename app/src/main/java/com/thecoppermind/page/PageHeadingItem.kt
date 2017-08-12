package com.thecoppermind.page

import android.view.ViewGroup
import android.widget.TextView
import com.thecoppermind.R
import com.thecoppermind.recyclerView.refactor.RecyclerHolder
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface

data class PageHeadingListItem(val blockNumber: Int, val heading: PageTextHeading, val isHighlightedHeading: Boolean = false) : RecyclerItemViewInterface {

    override val itemViewType: Int = viewType

    override fun bind(holder : RecyclerHolder) {
        if (heading.parts.isEmpty()) {
            (holder as PageHeadingViewInterface).setHeadingText(heading.text)
        } else {
            (holder as PageHeadingViewInterface).setTextParts(heading.parts)
        }
        if (isHighlightedHeading) {
            holder.setBackgroundResource(R.color.light_orange)
        } else {
            holder.setBackgroundResource(R.color.background_material_light)
        }

        holder.setHeadingLevelText(heading.level)
    }

    override val itemId: Any = blockNumber
    override val dataToCompare: List<Any>
        get() = listOf(heading)

    companion object {
        const val viewType: Int = PageHeadingView.layoutId
    }
}

interface PageHeadingViewInterface : PageTextViewInterface{
    fun setHeadingText(text: String)
    fun setHeadingLevelText(level: HeadingLevel)
    fun setBackgroundResource(resId: Int)
}

class PageHeadingView(parent: ViewGroup, val listener: OnTextItemClickListener) : RecyclerHolder(parent, layoutId), PageHeadingViewInterface {

    // ----- инициализация полей -----
    val textView: TextView = itemView.findViewById(R.id.page_list_item_heading_text)

    // ----- команды по заполнению полей от адаптера -----

    // -- обычный заголовок --
    override fun setHeadingText(text: String) {
        textView.text = text
    }

    override fun setBackgroundResource(resId: Int) {
        textView.setBackgroundResource(resId)
    }

    override fun setHeadingLevelText(level: HeadingLevel) {
        when (level) {
            HeadingLevel.h2 -> textView.setTextAppearance(itemView.context, R.style.Text_Medium_Black_Size_20)
            HeadingLevel.h3 -> textView.setTextAppearance(itemView.context, R.style.Text_Medium_Black_Size_18)
            else -> textView.setTextAppearance(itemView.context, R.style.Text_Medium_Black_Size_16)
        }
    }

    // -- заголовок со ссылкой внутри --
    override fun setTextParts(textParts: List<PageTextInterface>) {
        setTextPartsToTextView(textView, listener, textParts)
    }

    // ----- файл разметки для вьюшки -----
    companion object {
        const val layoutId: Int = R.layout.page_list_item_heading
    }
}
