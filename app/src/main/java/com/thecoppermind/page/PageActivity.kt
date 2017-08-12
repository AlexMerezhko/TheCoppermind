package com.thecoppermind.page

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.thecoppermind.App
import com.thecoppermind.R
import com.thecoppermind.recyclerView.RecyclerAdapter
import com.thecoppermind.recyclerView.RecyclerHelper
import com.thecoppermind.recyclerView.refactor.RecyclerHolder
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface
import kotlinx.android.synthetic.main.page_ac.*
import kotlinx.android.synthetic.main.page_ac_content.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PageActivity : AppCompatActivity() {

    companion object {
        const val extras_key_page_id = "extras_key_page_id"
        const val extras_key_scroll_to_heading = "extras_key_scroll_to_heading"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_ac)
        setSupportActionBar(page_ac_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val linkClickListener = object : OnTextItemClickListener {
            override fun onLinkClick(link: PageTextLink) {
                startPage(link)
            }
        }
        page_ac_recycler_view.adapter = object : RecyclerAdapter() {
            override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
                return when (viewType) {
                    PageHeadingListItem.viewType -> PageHeadingView(parent, linkClickListener)
                    PageTextListItem.viewType -> PageTextView(parent, linkClickListener)
                    else -> super.getViewHolder(parent, viewType)
                }
            }
        }
        page_ac_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        updatePageTitle(getString(R.string.title_default_before_loading_complete))
        requestPageInfo(intent.extras.getString(extras_key_page_id))
    }


    // ----- Обновление экрана -----

    fun updatePageContent(pageData: PageData) {
        updatePageTitle(pageData.title)
        updatePageContent(pageData.parts)
    }

    fun updatePageTitle(pageTitle: String) {
        title = pageTitle
    }

    fun updatePageContent(parts: ArrayList<PageTextInterface>) {
        val headingToScroll = intent.extras.getString(extras_key_scroll_to_heading, "")
        val views: ArrayList<RecyclerItemViewInterface> = ArrayList()
        val blocks: ArrayList<Pair<PageTextHeading?, ArrayList<PageTextInterface>>> = ArrayList()

        var positionToScroll = 0

        var groupedParts: ArrayList<PageTextInterface> = ArrayList()
        blocks.add(Pair(null, groupedParts))
        for (textPart in parts) {
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
        RecyclerHelper.updateDataInUniversalRecyclerView(page_ac_recycler_view, views)
        page_ac_recycler_view.scrollToPosition(positionToScroll)
    }


    // ----- Загрузка данных -----

    fun requestPageInfo(pageId: String) {
        App.mainApi.requestPageById(pageId).enqueue(object : Callback<PageData> {

            override fun onFailure(call: Call<PageData>?, t: Throwable?) {
                toast("Свалился запрос")
            }

            override fun onResponse(call: Call<PageData>?, response: Response<PageData>?) {
                response?.body()?.let {
                    updatePageContent(it)
                }
            }
        })
    }

    // ----- Переходы на другие экраны  -----

    fun startPage(link: PageTextLink) {
        val intent = intentFor<PageActivity>(Pair(PageActivity.extras_key_page_id, link.pageId))
        if (link.heading.isNotEmpty()) {
            intent.putExtra(PageActivity.extras_key_scroll_to_heading, link.heading)
        }
        startActivity(intent)
    }
}