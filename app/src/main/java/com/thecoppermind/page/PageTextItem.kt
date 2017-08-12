package com.thecoppermind.page

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.thecoppermind.R
import com.thecoppermind.recyclerView.refactor.RecyclerHolder
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface

data class PageTextListItem(val textInBlockNumber: Int, val textParts: ArrayList<PageTextInterface>) : RecyclerItemViewInterface {

    override val itemViewType: Int = viewType

    override fun bind(holder: RecyclerHolder) {
        (holder as PageTextViewInterface).setTextParts(textParts)
    }

    override val itemId: Any = textInBlockNumber

    override val dataToCompare: List<Any>
        get() = textParts

    companion object {
        const val viewType: Int = PageTextView.layoutId
    }
}

interface PageTextViewInterface {
    fun setTextParts(textParts: List<PageTextInterface>)

    // ----- доп функции для сбора текста из частей -----

    fun setTextPartsToTextView(textView: TextView, listener: OnTextItemClickListener, textParts: List<PageTextInterface>) {

        val builder: SpannableStringBuilder = SpannableStringBuilder()

        for (textPart in textParts) {
            when (textPart) {
                is PageTextNormal -> builder.append(textPart.text)
                is PageTextLink -> builder.append(addTextWithLink(textPart, listener))
                is PageTextBold -> builder.append(addBoldText(textPart.text))
            }
        }

        textView.text = builder
        textView.movementMethod = LinkMovementMethod.getInstance()
    }


    fun addBoldText(boldText: String): CharSequence {
        val boldSpan: StyleSpan = StyleSpan(Typeface.BOLD)
        val strBuilder: SpannableStringBuilder = SpannableStringBuilder(boldText)
        strBuilder.setSpan(boldSpan, 0, strBuilder.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return strBuilder
    }

    fun addTextWithLink(link: PageTextLink, listener: OnTextItemClickListener): CharSequence {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View?) {
                listener.onLinkClick(link)
            }

            override fun updateDrawState(ds: TextPaint?) {
                ds?.linkColor = Color.BLUE
                super.updateDrawState(ds)
            }
        }
        val strBuilder: SpannableStringBuilder = SpannableStringBuilder(link.text)
        strBuilder.setSpan(clickableSpan, 0, strBuilder.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return strBuilder
    }
}

interface OnTextItemClickListener {
    fun onLinkClick(link: PageTextLink)
}

class PageTextView(parent: ViewGroup, val listener: OnTextItemClickListener) : RecyclerHolder(parent, layoutId), PageTextViewInterface {

    // ----- инициализация полей -----
    val textView: TextView = itemView.findViewById(R.id.page_list_item_content_text)

    // ----- команды по заполнению полей от адаптера -----

    override fun setTextParts(textParts: List<PageTextInterface>) {
        setTextPartsToTextView(textView, listener, textParts)
    }

    companion object {
        // ----- файл разметки для вьюшки -----

        const val layoutId: Int = R.layout.page_list_item_content
    }
}
