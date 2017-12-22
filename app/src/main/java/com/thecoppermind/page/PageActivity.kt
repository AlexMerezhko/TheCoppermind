package com.thecoppermind.page

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.thecoppermind.App
import com.thecoppermind.R
import com.thecoppermind.page.interfaces.PageViewInterface
import com.thecoppermind.page.presenter.PagePresenter
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

class PageActivity : AppCompatActivity(), PageViewInterface {

    override val context: Context = this

    val presenter = PagePresenter(this)

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

        presenter.onCreate(intent.extras.getString(extras_key_scroll_to_heading, ""))

        requestPageInfo(intent.extras.getString(extras_key_page_id))
    }

    // ----- Команды от презентора -----

    override fun updatePageTitle(newPageTitle: String) {
        title = newPageTitle
    }

    override fun updatePageContent(views: ArrayList<RecyclerItemViewInterface>, positionToScroll: Int) {
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

                    presenter.onGetPageData(it)
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