package com.thecoppermind.page.interfaces

import com.thecoppermind.page.PageData

interface PagePresenterInterface {
    fun onGetPageData(pageData : PageData)
}