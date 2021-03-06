package com.checkin.app.checkin.Shop.Private;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.utility.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopCoverAdapter extends RecyclerView.Adapter<ShopCoverAdapter.ViewHolder> {
    private String[] mCoverUrls;
    private ShopCoverInteraction mListener;

    public ShopCoverAdapter(String[] coverUrls, ShopCoverInteraction listener) {
        mCoverUrls = coverUrls;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position + 1);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_shop_cover;
    }

    @Override
    public int getItemCount() {
        return mCoverUrls.length;
    }

    public interface ShopCoverInteraction {
        void onCoverRemove(int index);

        void onCoverChange(int index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_sc_cover)
        ImageView imCover;
        @BindView(R.id.tv_sc_index)
        TextView tvIndex;
        @BindView(R.id.btn_sc_remove)
        ImageView btnRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int index) {
            Drawable drawable = Utils.getSingleDrawable(
                    itemView.getContext(), ((LayerDrawable) itemView.getResources().getDrawable(R.drawable.card_image_add)));
            Utils.loadImageOrDefault(imCover, mCoverUrls[index - 1], drawable);
            tvIndex.setText(String.valueOf(index));
            btnRemove.setOnClickListener(v -> mListener.onCoverRemove(index));
            imCover.setOnClickListener(v -> mListener.onCoverChange(index));
        }
    }
}
