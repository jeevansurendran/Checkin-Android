package com.checkin.app.checkin.Waiter.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.WaiterEventAdapter;
import com.checkin.app.checkin.Waiter.WaiterTableViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WaiterTableEventFragment extends Fragment implements WaiterEventAdapter.WaiterEventInteraction {
    private Unbinder unbinder;

    private static final String KEY_WAITER_TABLE_ID = "waiter.table";

    @BindView(R.id.container_waiter_table_actions) ViewGroup containerActions;
    @BindView(R.id.rv_waiter_table_events) RecyclerView rvEvents;
    @BindView(R.id.tv_waiter_table_members_count) TextView tvMembersCount;

    private WaiterEventAdapter mAdapter;
    private WaiterTableViewModel mViewModel;

    public static WaiterTableEventFragment newInstance(long tableNumber) {
        WaiterTableEventFragment fragment = new WaiterTableEventFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_WAITER_TABLE_ID, tableNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiter_table_event, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new WaiterEventAdapter(this);
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvEvents.setAdapter(mAdapter);

        if (getArguments() == null)
            return;

        mViewModel = ViewModelProviders.of(this).get(WaiterTableViewModel.class);
        mViewModel.fetchSessionDetail(getArguments().getLong(KEY_WAITER_TABLE_ID, 0));
        mViewModel.fetchTableEvents();
        mViewModel.getTableEvents().observe(this, listResource -> {
            if (listResource == null)
                return;
            if (listResource.status == Status.SUCCESS && listResource.data != null) {
                mAdapter.setData(listResource.data);
            }
        });
        mViewModel.getSessionDetail().observe(this, resource -> {
            if (resource == null)
                return;
            if (resource.status == Status.SUCCESS && resource.data != null) {
                tvMembersCount.setText(resource.data.formatCustomerCount());
            }
        });
    }

    @Override
    public void onEventMarkDone(WaiterEventModel eventModel) {

    }

    @Override
    public void onOrderMarkDone(SessionOrderedItemModel orderedItemModel) {

    }

    @Override
    public void onOrderAccept(SessionOrderedItemModel orderedItemModel) {

    }

    @Override
    public void onOrderCancel(SessionOrderedItemModel orderedItemModel) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
