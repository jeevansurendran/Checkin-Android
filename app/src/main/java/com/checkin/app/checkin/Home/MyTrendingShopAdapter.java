package com.checkin.app.checkin.Home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.checkin.app.checkin.R;

public class MyTrendingShopAdapter extends RecyclerView.Adapter<MyTrendingShopAdapter.MyTrendingShopHolder>{
    @NonNull
    @Override
    public MyTrendingShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_child_trending_shop,parent,false);
        return new MyTrendingShopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTrendingShopHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class MyTrendingShopHolder extends RecyclerView.ViewHolder{
        MyTrendingShopHolder(View itemView) {
            super(itemView);
        }
    }
}
