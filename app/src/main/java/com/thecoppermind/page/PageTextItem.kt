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
    fun setTextParts(textParts: ArrayList<PageTextInterface>)
}

interface OnTextItemClickListener {
    fun onLinkClick(link : String)
}

class PageTextView(parent: ViewGroup, val listener : OnTextItemClickListener) : RecyclerHolder(parent, layoutId), PageTextViewInterface {

    // ----- инициализация полей -----
    val textView: TextView = itemView.findViewById(R.id.page_list_item_content_text)

    // ----- команды по заполнению полей от адаптера -----

    override fun setTextParts(textParts: ArrayList<PageTextInterface>) {

        val builder : SpannableStringBuilder = SpannableStringBuilder()

        for (textPart in textParts) {
            when(textPart){
                is PageTextNormal -> builder.append(textPart.text)
                is PageTextLink -> builder.append(addTextWithLink(textPart.text, textPart.pageId))
                is PageTextBold -> builder.append(addBoldText(textPart.text))
            }
        }

        textView.text = builder
        textView.movementMethod = LinkMovementMethod.getInstance();
    }

    // ----- файл разметки для вьюшки -----
    companion object {
        const val layoutId: Int = R.layout.page_list_item_content
    }

    // ----- доп функции -----

    fun addBoldText(boldText: String): CharSequence {
        val boldSpan : StyleSpan = StyleSpan(Typeface.BOLD)
        val strBuilder: SpannableStringBuilder = SpannableStringBuilder(boldText)
        strBuilder.setSpan(boldSpan, 0, strBuilder.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return strBuilder
    }

    fun addTextWithLink(textWithLink: String, pageId : String): CharSequence {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View?) {
                listener.onLinkClick(pageId)
            }
            override fun updateDrawState(ds: TextPaint?) {
                ds?.linkColor = Color.BLUE
                super.updateDrawState(ds)
            }
        }
        val strBuilder: SpannableStringBuilder = SpannableStringBuilder(textWithLink)
        strBuilder.setSpan(clickableSpan, 0, strBuilder.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return strBuilder
    }

}
