package com.thecoppermind.page.presenter

import com.thecoppermind.R
import com.thecoppermind.page.*
import com.thecoppermind.page.interfaces.PagePresenterInterface
import com.thecoppermind.page.interfaces.PageViewInterface
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface
import kotlin.properties.Delegates

class PagePresenter(val view: PageViewInterface) : PagePresenterInterface {

    private var headingToScroll by Delegates.notNull<String>()

    fun onCreate(headingToScroll: String) {
        this.headingToScroll = headingToScroll
        this.view.updatePageTitle(view.getStringFromRes(R.string.title_default_before_loading_complete))
    }

    override fun onGetPageData(pageData: PageData) {
        val views: ArrayList<RecyclerItemViewInterface> = ArrayList()
        val blocks: ArrayList<Pair<PageTextHeading?, ArrayList<PageTextInterface>>> = ArrayList()

        var positionToScroll = 0

        var groupedParts: ArrayList<PageTextInterface> = ArrayList()
        blocks.add(Pair(null, groupedParts))
        for (textPart in pageData.parts) {
            when (textPart) {
                is PageTextHeading -> {
                    groupedParts = ArrayList()
                    blocks.add(Pair(textPart, groupedParts))
                }
                is PageTextTemplate -> {
                    // TODO
                }
                else -> {
                    groupedParts.add(textPart)
                }
            }
        }

        for ((index, headerAndTextParts) in blocks.withIndex()) {
            val heading = headerAndTextParts.first
            heading?.let {
                if (headingToScroll.isNotEmpty() && headingToScroll == heading.text) {
                    views.add(PageHeadingListItem(index, heading, true))
                    positionToScroll = views.size - 1
                } else {
                    views.add(PageHeadingListItem(index, heading))
                }
            }
            if (!headerAndTextParts.second.none()) {
                views.add(PageTextListItem(index, headerAndTextParts.second))
            }
        }

        view.updatePageTitle(pageData.title)
        view.updatePageContent(views, positionToScroll)
    }
}