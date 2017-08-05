package com.thecoppermind.menu

import android.view.ViewGroup
import com.thecoppermind.recyclerView.RecyclerAdapter
import com.thecoppermind.recyclerView.refactor.RecyclerHolder

class NavigationMenuAdapter(val navigationMenuListener: OnNavigationMenuListener) : RecyclerAdapter() {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return when(viewType){
            NavigationMenuListItem.viewType -> NavigationMenuView(parent, navigationMenuListener)
            else -> super.getViewHolder(parent, viewType)
        }
    }

    override fun spanSizeForViewType(viewType: Int): Int {
        return super.spanSizeForViewType(viewType)
    }
}
