package com.thecoppermind

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.thecoppermind.menu.NavigationMenuAdapter
import com.thecoppermind.menu.NavigationMenuData
import com.thecoppermind.menu.NavigationMenuListItem
import com.thecoppermind.menu.OnNavigationMenuListener
import com.thecoppermind.page.PageActivity
import com.thecoppermind.recyclerView.RecyclerHelper
import com.thecoppermind.recyclerView.refactor.RecyclerItemViewInterface
import kotlinx.android.synthetic.main.main_ac.*
import kotlinx.android.synthetic.main.main_ac_app_bar.*
import kotlinx.android.synthetic.main.main_ac_content.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_ac)
        setSupportActionBar(main_ac_toolbar)

        main_ac_content_btn_holstep.setOnClickListener { startPage("Holstep") } // TODO некорректная вырезка \n из-за ссылок
        main_ac_content_btn_skaa.setOnClickListener { startPage("Skaa_rebellion") }
        main_ac_content_btn_vin.setOnClickListener { startPage("Vin") }
        main_ac_content_btn_elend.setOnClickListener { startPage("Elend Venture") }
        main_ac_content_btn_rashek.setOnClickListener { startPage("Rashek") } // TODO цитаты

        main_ac_fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(this, main_ac_drawer_layout, main_ac_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        main_ac_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val menuItems: ArrayList<NavigationMenuData> = ArrayList()
        menuItems.add(NavigationMenuData("categorise", R.string.menu_categories))
        menuItems.add(NavigationMenuData("community", R.string.menu_community))
        menuItems.add(NavigationMenuData("tools", R.string.menu_tools))

        main_ac_nav_view_recycler_view.adapter = NavigationMenuAdapter(object : OnNavigationMenuListener {
            override fun onSendToast(textResId: Int) {
                toast(textResId)
            }

            override fun onClick(menuId: String) {

            }
        })

        val menuViews: ArrayList<RecyclerItemViewInterface> = ArrayList()
        menuItems.mapTo(menuViews) { NavigationMenuListItem(it.id, it.name) }

        main_ac_nav_view_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        RecyclerHelper.updateDataInUniversalRecyclerView(main_ac_nav_view_recycler_view, menuViews)
    }

    fun startPage(pageId: String) {
        startActivity<PageActivity>(Pair(PageActivity.extras_key_page_id, pageId))
    }

    override fun onBackPressed() {
        if (main_ac_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            main_ac_drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.menu_categories -> {
//                startActivity<CategoriesActivity>()
            }

            R.id.menu_community -> {
                toast("community (in progress)")

            }
            R.id.menu_tools -> {
                toast("tools (in progress)")
            }
        }

        main_ac_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}


//    https://coppermind.net/wiki/Holstep
//    https://coppermind.net/w/api.php?action=parse&pageid=3851&format=json&prop=text

//    https://coppermind.net/wiki/Skaa_rebellion
//    https://coppermind.net/w/api.php?action=parse&pageid=1198&format=json&prop=text
//    https://coppermind.net/w/api.php?action=parse&pageid=1198&prop=wikitext&format=json


//    https://coppermind.net/w/api.php?action=parse&format=json&prop=wikitext&page=Kelsier


//    https://coppermind.net/w/api.php?action=query&titles=Holstep&prop=revisions&rvprop=content&format=json

//    https://coppermind.net/w/api.php?action=parse&title=Holstep&prop=text&format=json
//    https://coppermind.net/w/api.php?action=query&pageids=1198&prop=revisions&rvprop=content&format=json
//    https://coppermind.net/w/api.php?action=parse&pageid=1198&prop=text&format=json

//    https://coppermind.net/w/api.php?action=query&titles=Vin&prop=revisions&rvprop=content&format=json
//    https://www.mediawiki.org/wiki/API:Main_page/ru
//    https://www.mediawiki.org/w/api.php?action=help&modules=main

//    https://coppermind.net/w/api.php?action=query&prop=revisions&rvprop=ids&format=json&rvprop=content&pageids=3851
//    https://coppermind.net/w/api.php?action=parse&pageid=3851&format=json&prop=wikitext