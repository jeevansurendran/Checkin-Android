package com.checkin.app.checkin.menu.activities

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.airbnb.epoxy.EpoxyRecyclerView
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.menu.controllers.getUniqueId
import com.checkin.app.checkin.menu.fragments.MenuDishSearchFragment
import com.checkin.app.checkin.menu.fragments.MenuFragment
import com.checkin.app.checkin.menu.fragments.MenuGroupScreenInteraction
import com.checkin.app.checkin.menu.holders.ShopMenuCartModelHolder
import com.checkin.app.checkin.menu.holders.shopMenuCartModelHolder
import com.checkin.app.checkin.menu.models.OrderedItemModel
import com.checkin.app.checkin.menu.viewmodels.ActiveSessionCartViewModel
import com.checkin.app.checkin.menu.viewmodels.UserMenuViewModel
import com.checkin.app.checkin.misc.BlockingNetworkViewModel
import com.checkin.app.checkin.misc.activities.BaseActivity
import com.checkin.app.checkin.misc.fragments.NetworkBlockingFragment
import com.checkin.app.checkin.misc.views.EndDrawerToggle
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.inTransaction
import com.checkin.app.checkin.utility.isNotEmpty

class ShopMenuActivity : BaseActivity(), ShopMenuCartModelHolder.MenuCartInteraction, MenuGroupScreenInteraction {
    @BindView(R.id.epoxy_rv_shop_menu_cart)
    internal lateinit var epoxyRvCart: EpoxyRecyclerView
    @BindView(R.id.drawer_shop_menu)
    internal lateinit var drawerMenu: DrawerLayout
    @BindView(R.id.toolbar_shop_menu)
    internal lateinit var toolbar: Toolbar
    @BindView(R.id.tv_shop_menu_count_ordered_items)
    internal lateinit var tvCountOrderedItems: TextView
    @BindView(R.id.nested_sv_shop_menu)
    internal lateinit var nestedSv: NestedScrollView
    @BindView(R.id.tv_shop_menu_subtotal)
    internal lateinit var tvSubtotal: TextView

    @Suppress("UNUSED")
    private val menuViewModel: UserMenuViewModel by viewModels()

    private val cartViewModel: ActiveSessionCartViewModel by viewModels()
    private val networkViewModel: BlockingNetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_shop_session_menu)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            val drawerToggle = EndDrawerToggle(this@ShopMenuActivity, drawerMenu, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close, R.drawable.ic_cart_white)
            drawerMenu.addDrawerListener(drawerToggle)
            drawerToggle.syncState()
        }

        initUi()
        setupObservers()
    }

    private fun initUi() {
        val restaurantId = intent.getLongExtra(KEY_RESTAURANT_ID, 0L)
        val sessionId = intent.getLongExtra(KEY_SESSION_ID, 0L)

        menuViewModel.clearFilters() // Necessary to get viewmodel since its lazy property

        supportFragmentManager.inTransaction {
            add(R.id.frg_container_activity, MenuFragment.newInstanceForShop(restaurantId, sessionId), null)
            add(android.R.id.content, NetworkBlockingFragment.withBlockingLoader(), NetworkBlockingFragment.FRAGMENT_TAG)
        }
        epoxyRvCart.withModels {
            cartViewModel.orderedItems.value.takeIf { it.isNotEmpty() }?.forEach {
                shopMenuCartModelHolder {
                    id(it.getUniqueId())
                    orderData(it)
                    listener(this@ShopMenuActivity)
                }
            }
        }
    }

    private fun setupObservers() {
        cartViewModel.orderedItems.observe(this, Observer { epoxyRvCart.requestModelBuild() })
        cartViewModel.totalOrderedCount.observe(this, Observer {
            if (it > 0) {
                tvCountOrderedItems.visibility = View.VISIBLE
                tvCountOrderedItems.text = it.toString()
            } else tvCountOrderedItems.visibility = View.GONE
        })
        cartViewModel.orderedSubTotal.observe(this, Observer {
            tvSubtotal.text = Utils.formatCurrencyAmount(this, it)
        })
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

    private fun setupRemarksDialog(item: OrderedItemModel): AlertDialog {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(item.remarks)
        return AlertDialog.Builder(this)
                .setTitle("Enter Remarks")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    cartViewModel.orderItem(item.updateRemarks(input.toString()))
                    input.setText("")
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                    input.setText("")
                }
                .create()
    }

    override fun onOrderedItemRemark(item: OrderedItemModel) {
        setupRemarksDialog(item).show()
    }

    override fun onOrderedItemRemoved(item: OrderedItemModel) {
        cartViewModel.orderItem(item.updateQuantity(0))
    }

    override fun onOrderedItemChanged(item: OrderedItemModel, count: Int) {
        cartViewModel.orderItem(item.updateQuantity(count))
    }

    @OnClick(R.id.btn_shop_menu_cart_proceed)
    fun onProceedClick() {
        cartViewModel.confirmOrder()
    }

    companion object {
        private const val KEY_RESTAURANT_ID = "shop.menu.restaurant_id"
        private const val KEY_SESSION_ID = "shop.menu.session_id"
        private const val LOAD_SYNC_SERVER_ORDER = "load.sync.orders"

        fun openMenu(context: Context, restaurantId: Long, sessionId: Long = 0L) = Intent(context, ShopMenuActivity::class.java).apply {
            putExtra(KEY_RESTAURANT_ID, restaurantId)
            putExtra(KEY_SESSION_ID, sessionId)
            context.startActivity(this)
        }
    }
}