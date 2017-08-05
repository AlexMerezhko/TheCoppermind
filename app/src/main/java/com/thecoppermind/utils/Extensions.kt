package com.thecoppermind.utils

import android.content.Context

fun Context.getColorForResource(colorResId : Int) : Int{
    return resources.getColor(colorResId)
}