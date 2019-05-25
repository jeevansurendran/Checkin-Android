package com.checkin.app.checkin.session.successfultransaction;

import android.os.Bundle;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionDetailFragment;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionDetailModel;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionFeedbackFragment;
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionViewModel;
import com.checkin.app.checkin.session.activesession.ActiveSessionInvoiceViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessfulTransactionDetailActivity extends AppCompatActivity {
    public static final String KEY_SESSION_DATA = "shop.invoice_session";

    @BindView(R.id.tv_invoice_session_id)
    TextView tvSessionId;
    @BindView(R.id.tv_invoice_session_date)
    TextView tvDate;
    @BindView(R.id.tv_invoice_session_item_count)
    TextView tvItemCount;
    @BindView(R.id.tv_shop_session_total_time)
    TextView tvTotalTime;
    @BindView(R.id.tv_invoice_session_waiter)
    TextView tvWaiter;
    @BindView(R.id.appbar_successful_transaction_details)
    AppBarLayout appBarLayout;

    private SuccessfulTransactionViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_transaction_details);
        ButterKnife.bind(this);

        getData();


    }

    private void getData() {
        mViewModel = ViewModelProviders.of(this).get(SuccessfulTransactionViewModel.class);
        mViewModel.fetchUserSessionDetail( getIntent().getLongExtra("sessionId", 0));
        mViewModel.getUserSessionDetail().observe(this, shopSessionDetailModelResource -> {
            if (shopSessionDetailModelResource == null)
                return;
            if (shopSessionDetailModelResource.status == Resource.Status.SUCCESS && shopSessionDetailModelResource.data != null) {
//                setupAppbarDetails(shopSessionDetailModelResource.data.);
            }
        });
    }

    private void setupAppbarDetails(RestaurantSessionModel data) {
        BriefModel host = data.getHost();
        tvWaiter.setText(String.format(Locale.ENGLISH, "Waiter : %s", host != null ? host.getDisplayName() : getResources().getString(R.string.waiter_unassigned)));

        tvSessionId.setText(data.getHashId());
        tvDate.setText(data.getFormattedDate());
        tvItemCount.setText(String.format(Locale.ENGLISH, " | %d item(s)", data.getCountOrders()));
//        tvTotalTime.setText(data.formatTotalTime());
    }



}