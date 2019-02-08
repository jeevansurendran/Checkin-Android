package com.checkin.app.checkin.Menu;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Menu.Adapter.MenuCartAdapter;
import com.checkin.app.checkin.Menu.Fragment.ItemCustomizationFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuFilterFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.SESSION_STATUS;
import com.checkin.app.checkin.Menu.Fragment.MenuInfoFragment;
import com.checkin.app.checkin.Menu.Fragment.MenuItemSearchFragment;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.EndDrawerToggle;
import com.checkin.app.checkin.Utility.Utils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.KEY_SESSION_STATUS;

public class SessionMenuActivity extends BaseActivity implements
        MenuItemInteraction, ItemCustomizationFragment.ItemCustomizationInteraction,
        MenuCartAdapter.MenuCartInteraction, MenuFilterFragment.MenuFilterInteraction {
    private static final String TAG = SessionMenuActivity.class.getSimpleName();

    private static final String KEY_RESTAURANT_PK = "menu.shop_pk";
    private static final String KEY_SESSION_PK = "menu.session_pk";

    @BindView(R.id.view_menu_search)
    MaterialSearchView vMenuSearch;
    @BindView(R.id.rv_menu_cart)
    RecyclerView rvCart;
    @BindView(R.id.tv_menu_count_ordered_items)
    TextView tvCountItems;
    @BindView(R.id.tv_menu_subtotal)
    TextView tvCartSubtotal;

    private MenuGroupsFragment mMenuFragment;
    private MenuItemSearchFragment mSearchFragment;
    private MenuFilterFragment mFilterFragment;
    private MenuViewModel mViewModel;
    private MenuCartAdapter mCartAdapter;
    private SESSION_STATUS mSessionStatus;

    private static final String SESSION_ARG = "session_arg";

    public static void withSession(Context context, Long restaurantPk, @Nullable Long sessionPk) {
        Intent intent = new Intent(context, SessionMenuActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(KEY_SESSION_STATUS, SESSION_STATUS.ACTIVE);
        args.putLong(KEY_RESTAURANT_PK, restaurantPk);
        if (sessionPk != null)
            args.putLong(KEY_SESSION_PK, sessionPk);
        intent.putExtra(SESSION_ARG, args);
        context.startActivity(intent);
    }

    public static void withoutSession(Context context, long restaurantPk) {
        Intent intent = new Intent(context, SessionMenuActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(KEY_SESSION_STATUS, SESSION_STATUS.INACTIVE);
        args.putLong(KEY_RESTAURANT_PK, restaurantPk);
        intent.putExtra(SESSION_ARG, args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_menu);
        ButterKnife.bind(this);

        Bundle args = getIntent().getBundleExtra(SESSION_ARG);
        mSessionStatus = (SESSION_STATUS) args.getSerializable(KEY_SESSION_STATUS);

        mViewModel = ViewModelProviders.of(this).get(MenuViewModel.class);
        mViewModel.fetchAvailableMenu(args.getLong(KEY_RESTAURANT_PK));
        long sessionPk = args.getLong(KEY_SESSION_PK, 0L);
        if (sessionPk > 0L)
            mViewModel.manageSession(sessionPk);

        mSearchFragment = MenuItemSearchFragment.newInstance(SessionMenuActivity.this, isSessionActive());

        init(R.id.container_menu_fragment, true);
        setupUiStuff();
        setupMenuFragment();
        setupFilter();
        setupSearch();

        if (isSessionActive())
            setupCart();
    }

    private void setupFilter() {
        mFilterFragment = MenuFilterFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, mFilterFragment)
                .commit();
    }

    private void setupMenuFragment() {
        mMenuFragment = MenuGroupsFragment.newInstance(mSessionStatus, this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu, mMenuFragment)
                .commit();
    }

    private AlertDialog setupRemarksDialog(OrderedItemModel item) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(item.getRemarks());

        return new AlertDialog.Builder(this)
                .setTitle("Enter Remarks")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    item.setRemarks(input.getText().toString());
                    item.setChangeCount(0);
                    mViewModel.setCurrentItem(item);
                    mViewModel.orderItem();
                    input.setText("");
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    input.setText("");
                })
                .create();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUiStuff() {
        Toolbar toolbar = findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0);
        }

        if (isSessionActive()) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_menu);
            EndDrawerToggle endToggle = new EndDrawerToggle(
                    this, drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close, R.drawable.ic_cart_white);
            drawerLayout.addDrawerListener(endToggle);
            endToggle.syncState();
        } else {
            findViewById(R.id.nav_menu_cart).setVisibility(View.GONE);
        }
    }

    private void setupCart() {
        mCartAdapter = new MenuCartAdapter(this);
        rvCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCart.setAdapter(mCartAdapter);

        mViewModel.getOrderedItems().observe(this, mCartAdapter::setOrderedItems);
        mViewModel.getTotalOrderedCount().observe(this, count -> {
            if (count == null)
                return;
            if (count > 0) {
                tvCountItems.setText(Utils.formatCount(count));
                tvCountItems.setVisibility(View.VISIBLE);
            } else {
                tvCountItems.setVisibility(View.GONE);
            }
        });
        mViewModel.getOrderedSubTotal().observe(this, subtotal -> {
            if (subtotal == null)
                return;
            tvCartSubtotal.setText(String.format(
                    Locale.ENGLISH, "Subtotal: " + Utils.getCurrencyFormat(this) + " *", subtotal));
        });

        mViewModel.getServerOrderedItems().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS) {
                Utils.toast(this, "Confirmed orders!");
                finish();
            } else {
                Log.e(TAG, "MSG: " + resource.message);
            }
        });
    }

    private void setupSearch() {
        vMenuSearch.setVoiceSearch(true);
        vMenuSearch.setStartFromRight(false);
        vMenuSearch.setCursorDrawable(R.drawable.color_cursor_white);

        vMenuSearch.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.searchMenuItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mViewModel.searchMenuItems(query);
                return true;
            }

            @Override
            public boolean onQueryClear() {
                mViewModel.resetMenuItems();
                return true;
            }
        });
        vMenuSearch.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                mViewModel.resetMenuItems();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_menu_fragment, mSearchFragment, "search")
                        .commit();
            }

            @Override
            public void onSearchViewClosed() {
                getSupportFragmentManager().beginTransaction()
                        .remove(mSearchFragment)
                        .commit();
            }
        });
    }

    @OnClick(R.id.btn_menu_search)
    public void onSearchClicked(View view) {
        vMenuSearch.showSearch();
    }

    @OnClick(R.id.btn_menu_cart_proceed)
    public void onProceedBtnClicked(View view) {
        if (mCartAdapter.getItemCount() > 0) {
            mViewModel.confirmOrder();
        } else {
            Utils.toast(this, "Order something before proceeding!");
        }
    }

    @Override
    public void onBackPressed() {
        Fragment customizationFragment = getSupportFragmentManager().findFragmentByTag("item_customization");
        Fragment infoFragment = getSupportFragmentManager().findFragmentByTag("item_info");
        if (infoFragment != null) {
            ((MenuInfoFragment) infoFragment).onBackPressed();
        } else if (customizationFragment != null) {
            ((ItemCustomizationFragment) customizationFragment).onBackPressed();
        } else if (vMenuSearch.isSearchOpen()) {
            closeSearch();
        } else if (!mFilterFragment.onBackPressed() && !mMenuFragment.onBackPressed()) {
            super.onBackPressed();
        }
        mViewModel.clearFilters();
    }

    public void closeSearch() {
        vMenuSearch.closeSearch();
    }

    private boolean isSessionActive() {
        return mSessionStatus == SESSION_STATUS.ACTIVE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    vMenuSearch.setQuery(searchWrd, true);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onItemInteraction(MenuItemModel item, int count) {
        if (item.isComplexItem()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_menu_fragment, ItemCustomizationFragment.newInstance(item, this), "item_customization")
                    .commit();
        } else {
            mViewModel.orderItem();
        }
    }

    @Override
    public void onOrderedItemRemoved(OrderedItemModel item) {
        mViewModel.removeItem(item);
    }

    @Override
    public void onOrderedItemChanged(OrderedItemModel item, int count) {
        item.setQuantity(count);
        mViewModel.setCurrentItem(item);
        mViewModel.orderItem();
    }

    @Override
    public void onOrderedItemRemark(OrderedItemModel item) {
        setupRemarksDialog(item).show();
    }

    @Override
    public boolean onMenuItemAdded(MenuItemModel item) {
        if (isSessionActive()) {
            mViewModel.newOrderedItem(item);
            this.onItemInteraction(item, 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemChanged(MenuItemModel item, int count) {
        if (isSessionActive()) {
            if (mViewModel.updateOrderedItem(item, count)) {
                this.onItemInteraction(item, count);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onMenuItemShowInfo(MenuItemModel item) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_menu_fragment, MenuInfoFragment.newInstance(item), "item_info")
                .commit();
    }

    @Override
    public int getItemOrderedCount(MenuItemModel item) {
        return mViewModel.getOrderedCount(item);
    }

    @Override
    public void onCustomizationDone() {
        mViewModel.orderItem();
    }

    @Override
    public void onCustomizationCancel() {
        mViewModel.cancelItem();
    }

    @Override
    public void onShowFilter() {

    }

    @Override
    public void onHideFilter() {

    }

    @Override
    public void filterByCategory(String category) {
        mMenuFragment.scrollToCategory(category);
    }

    @Override
    public void sortItems() {
        vMenuSearch.showSearch(true);
    }

    @Override
    public void resetFilters() {
        vMenuSearch.closeSearch();
    }
}
