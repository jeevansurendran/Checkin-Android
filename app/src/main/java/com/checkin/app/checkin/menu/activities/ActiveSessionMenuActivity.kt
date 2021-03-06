package com.checkin.app.checkin.menu.activities

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.fragments.MenuDishSearchFragment
import com.checkin.app.checkin.menu.fragments.MenuFragment
import com.checkin.app.checkin.menu.fragments.MenuGroupScreenInteraction
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.menu.views.ActiveSessionCartView
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.coroutineLifecycleScope
import com.checkin.app.checkin.utility.inTransaction

class ActiveSessionMenuActivity : BaseActivity(), MenuGroupScreenInteraction {
    @BindView(R.id.view_as_menu_cart)
    internal lateinit var cartView: ActiveSessionCartView
    @BindView(R.id.nested_sv_active_session_menu)
    internal lateinit var nestedSv: NestedScrollView

    private lateinit var menuFragment: MenuFragment

    @Suppress("UNUSED")
    private val menuViewModel: UserMenuViewModel by viewModels()

    private val cartViewModel: ActiveSessionCartViewModel by viewModels()
    private val networkViewModel: BlockingNetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_active_session_menu)
        ButterKnife.bind(this)

        supportActionBar?.run {
            title = "Menu"
            setDisplayHomeAsUpEnabled(true)
        }

        setupObservers()
        initUi()
    }

    private fun setupObservers() {
        cartViewModel.serverOrders.observe(this, Observer {
            it?.let { resource ->
                networkViewModel.updateStatus(resource, LOAD_SYNC_SERVER_ORDER)
                if (resource.status == Resource.Status.SUCCESS) {
                    Utils.toast(this, "Confirmed orders!")
                    finish()
                }
            }
        })
        networkViewModel.shouldTryAgain {
            if (it == LOAD_SYNC_SERVER_ORDER) cartViewModel.confirmOrder()
        }
    }

    private fun initUi() {
        val restaurantId = intent.getLongExtra(KEY_RESTAURANT_ID, 0L)

        menuViewModel.clearFilters() // Necessary to get viewmodel since its lazy property

        menuFragment = MenuFragment.newInstanceForActiveSession(restaurantId)

        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, menuFragment, null)
            add(android.R.id.content, NetworkBlockingFragment.withBlockingLoader(), NetworkBlockingFragment.FRAGMENT_TAG)
        }

        val itemPk = intent.getLongExtra(KEY_PRE_SELECTED_ITEM, 0L)
        if (itemPk > 0L) {
            coroutineLifecycleScope.launchWhenStarted {
                // Busy waiting for data to come... Since it's on another thread no issues.
                while (menuFragment.menuViewModel.originalMenuGroups.value?.data?.isEmpty() != false) {
                }
                menuFragment.menuViewModel.getMenuItemById(itemPk)?.let {
                    menuFragment.onMenuItemAdded(it)
                    if (!it.isComplexItem)
                        cartView.show()
                }
            }
        }
    }

    override fun onExpandGroupView(view: View) {
        val outRect = Rect()
        view.getDrawingRect(outRect)
        nestedSv.offsetDescendantRectToMyCoords(view, outRect)
        nestedSv.smoothScrollTo(0, outRect.top)
    }

    override fun onListBuilt() {
    }

    override fun onOpenSearch() {
        supportFragmentManager.inTransaction {
            add(R.id.frg_container_menu_search, MenuDishSearchFragment.withAsCart(), MenuDishSearchFragment.FRAGMENT_TAG)
            addToBackStack(null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val KEY_RESTAURANT_ID = "as.menu.restaurant_id"
        private const val KEY_PRE_SELECTED_ITEM = "as.menu.pre_selected.item_id"
        private const val LOAD_SYNC_SERVER_ORDER = "load.sync.orders"

        @JvmOverloads
        fun openMenu(context: Context, restaurantId: Long, itemPk: Long = 0L) = context.startActivity(withRestaurantIntent(context, restaurantId, itemPk))

        fun withRestaurantIntent(context: Context, restaurantId: Long, itemPk: Long = 0L) = Intent(context, ActiveSessionMenuActivity::class.java).apply {
            putExtra(KEY_RESTAURANT_ID, restaurantId)
            putExtra(KEY_PRE_SELECTED_ITEM, itemPk)
        }
    }
}