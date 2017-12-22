package com.thecoppermind.page.interfaces

import com.thecoppermind.mvp.ViewInterface
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface

interface PageViewInterface : ViewInterface {

    fun updatePageTitle(newPageTitle : String)
    fun updatePageContent(views : ArrayList<RecyclerItemViewInterface>, positionToScroll : Int)

}