package com.thecoppermind.mvp

import android.content.Context

interface ViewInterface {

    val context : Context

    fun getStringFromRes(stringResId : Int) = context.getString(stringResId)

}