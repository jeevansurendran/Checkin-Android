package com.checkin.app.checkin.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrendingShopFragment extends AppCompatActivity {

    @BindView(R.id.rv_shop_list)
    RecyclerView rvShopList;
    @BindView(R.id.tv_trending)
    TextView tvTrending;
    @BindView(R.id.tv_near_me)
    TextView tvNearMe;
    @BindView(R.id.tv_top_rated)
    TextView tvTopRated;
    @BindView(R.id.rv_trending_user)
    RecyclerView rvTrendingUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trending_shop);
        ButterKnife.bind(this);

        MyTrendingShopAdapter myTrendingShopAdapter = new MyTrendingShopAdapter();
        rvShopList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvShopList.setAdapter(myTrendingShopAdapter);
        rvShopList.setItemAnimator(new DefaultItemAnimator());

        TrendingShopUserAdapter trendingShopUserAdapter = new TrendingShopUserAdapter();
        rvTrendingUser.setLayoutManager(new GridLayoutManager(this,3));
        rvTrendingUser.setAdapter(trendingShopUserAdapter);
        rvTrendingUser.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick({R.id.tv_trending, R.id.tv_near_me, R.id.tv_top_rated})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_trending:
                tvTrending.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_red_stroke));
                tvNearMe.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_grey_stroke));
                tvTopRated.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_grey_stroke));
                break;
            case R.id.tv_near_me:
                tvTrending.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_grey_stroke));
                tvNearMe.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_red_stroke));
                tvTopRated.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_grey_stroke));
                break;
            case R.id.tv_top_rated:
                tvTrending.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_grey_stroke));
                tvNearMe.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_grey_stroke));
                tvTopRated.setBackground(ContextCompat.getDrawable(this, R.drawable.thin_red_stroke));
                break;
        }
    }
}
/*public class TrendingShopFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trending_shop,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}*/

