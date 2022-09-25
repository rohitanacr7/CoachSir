package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rohitanabhavane.coachsir.Model.BannerModel;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    Context context;
    ArrayList<BannerModel> bannerModelArrayList;
    public Boolean showShimmer = true;

    public BannerAdapter(Context context, ArrayList<BannerModel> bannerModelArrayList) {
        this.context = context;
        this.bannerModelArrayList = bannerModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer();
        } else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);

            BannerModel bannerModel = bannerModelArrayList.get(position);
            holder.imgBanner.setVisibility(View.VISIBLE);
            Glide.with(context).load(bannerModel.getBannerImgLink()).into(holder.imgBanner);
        }

    }

    @Override
    public int getItemCount() {
        int SHIMMER_ITEM_NUMBER = 10;
        return showShimmer ? SHIMMER_ITEM_NUMBER : bannerModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ShimmerFrameLayout shimmerFrameLayout;
        ImageView imgBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }
}
