package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rohitanabhavane.coachsir.Model.RatingModel;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {
    Context context;
    ArrayList<RatingModel> ratingModelArrayList;

    public RatingAdapter(Context context, ArrayList<RatingModel> ratingModelArrayList) {
        this.context = context;
        this.ratingModelArrayList = ratingModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customerrating, parent, false);
        return new RatingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RatingModel ratingModel = ratingModelArrayList.get(position);
        holder.customerReviewFullName.setText(ratingModel.getFullName());
        holder.customerReviewComment.setText(ratingModel.getComment());
        holder.customerReviewDateTime.setText(ratingModel.getRatingDateTime());

        Glide.with(context).load(ratingModel.getProfileImgLink()).into(holder.customerReviewProfile);

        int rating = Integer.parseInt(ratingModel.getRating());
        if (rating == 1){
            Glide.with(context).load(R.drawable.review1).into(holder.customerReviewStar);
        }else if (rating == 2){
            Glide.with(context).load(R.drawable.reveiw2).into(holder.customerReviewStar);
        }else if (rating == 3){
            Glide.with(context).load(R.drawable.reveiw3).into(holder.customerReviewStar);
        }else if (rating == 4){
            Glide.with(context).load(R.drawable.reveiw4).into(holder.customerReviewStar);
        }else if (rating == 5){
            Glide.with(context).load(R.drawable.reveiw5).into(holder.customerReviewStar);
        }

    }

    @Override
    public int getItemCount() {
        return ratingModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView customerReviewFullName, customerReviewComment, customerReviewDateTime;
        ImageView customerReviewProfile, customerReviewStar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            customerReviewFullName = itemView.findViewById(R.id.customerReviewFullName);
            customerReviewComment = itemView.findViewById(R.id.customerReviewComment);
            customerReviewDateTime = itemView.findViewById(R.id.customerReviewDateTime);
            customerReviewProfile = itemView.findViewById(R.id.customerReviewProfile);
            customerReviewStar = itemView.findViewById(R.id.customerReviewStar);

        }
    }
}
