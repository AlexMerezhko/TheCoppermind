package com.thecoppermind.menu

import android.support.annotation.StringRes
import android.view.ViewGroup
import android.widget.TextView
import com.thecoppermind.R
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface
import com.thecoppermind.recyclerView.refactor.RecyclerHolder
import kotlin.properties.Delegates

// данные, как они приходят из модели
data class NavigationMenuData(val id: String, @StringRes val name: Int)

// даныне, как они преобразовываются презентереом
data class NavigationMenuListItem(val id: String, @StringRes val name: Int) : RecyclerItemViewInterface {

    override val itemViewType: Int = viewType

    override fun bind(holder: RecyclerHolder) {
        (holder as NavigationMenuViewInterface).setMenuItem(id, name)
    }

    override val itemId: Any = id
    override val dataToCompare = listOf(name)

    companion object {
        const val viewType: Int = NavigationMenuView.layoutId
    }
}

// интерфейс для передачи команд вьюшке (холдеру)
interface NavigationMenuViewInterface {
    fun setMenuItem(id: String, nameResId: Int)
    fun changeMenuColor(color: Int)
    fun sendToast(testResId: Int)
}

// слушатель событий на вьюшке
interface OnNavigationMenuListener {
    fun onClick(id: String)
    fun onSendToast(textResId: Int)
}

//sealed class Payloads
//data class NewColor(val color: Int) : Payloads()
//data class ShowToastText(val textResId: Int) : Payloads()
//

// сама вьющка
class NavigationMenuView(parent: ViewGroup, val listener: OnNavigationMenuListener) : RecyclerHolder(parent, layoutId), NavigationMenuViewInterface {

    // переменные, которые относятся к текущим данным (для передачи в слушатели)
    private var menuDataId by Delegates.notNull<String>()

    // ----- инициализация полей -----
    val nameTextFiled: TextView = itemView.findViewById(R.id.navigation_drawer_list_item_name)

    // ----- первоначальная инициализация вьюшки -----

    init {
        nameTextFiled.setOnClickListener { if (canHandleClick()) listener.onClick(menuDataId) }
    }

    // ----- команды по заполнению полей от адаптера -----

    override fun setMenuItem(id: String, nameResId: Int) {
        menuDataId = id
        nameTextFiled.setText(nameResId)
    }

    override fun changeMenuColor(color: Int) {
        nameTextFiled.setTextColor(color)
    }

    override fun sendToast(testResId: Int) {
        if (canHandleClick()) {
            listener.onSendToast(testResId)
        }
    }

    // ----- файл разметки для вьюшки -----
    companion object {
        const val layoutId: Int = R.layout.navigation_drawer_list_item
    }
}
