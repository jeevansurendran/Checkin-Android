package com.checkin.app.checkin.Home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

public class TrendingShopUserAdapter extends RecyclerView.Adapter<TrendingShopUserAdapter.TrendingUserHolder>{

    @NonNull
    @Override
    public TrendingUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_trending_user,parent,false);
        return new TrendingUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingUserHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    class TrendingUserHolder extends RecyclerView.ViewHolder {
        TrendingUserHolder(View itemView) {
            super(itemView);
        }
    }
}
