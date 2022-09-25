package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitanabhavane.coachsir.Model.TrainingTimeModel;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;

public class TrainingTimeAdapter extends RecyclerView.Adapter<TrainingTimeAdapter.MyViewHolder> {
    Context context;
    ArrayList<TrainingTimeModel> trainingTimeModelArrayList;
    private OnTrainingTimeDeleteListener mOnTrainingTimeDeleteListener;

    public TrainingTimeAdapter(Context context, ArrayList<TrainingTimeModel> trainingTimeModelArrayList, OnTrainingTimeDeleteListener mOnTrainingTimeDeleteListener) {
        this.context = context;
        this.trainingTimeModelArrayList = trainingTimeModelArrayList;
        this.mOnTrainingTimeDeleteListener = mOnTrainingTimeDeleteListener;
    }

    @NonNull
    @Override
    public TrainingTimeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trainingtimeitem, parent, false);
        return new TrainingTimeAdapter.MyViewHolder(view, mOnTrainingTimeDeleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingTimeAdapter.MyViewHolder holder, int position) {
        TrainingTimeModel trainingTimeModel = trainingTimeModelArrayList.get(position);
        holder.itemFrom.setText(trainingTimeModel.getTrainingTimeFrom());
        holder.itemAMPM1.setText(trainingTimeModel.getTrainingTimeAMPM1());
        holder.itemTo.setText(trainingTimeModel.getTrainingTimeTo());
        holder.itemAMPM2.setText(trainingTimeModel.getTrainingTimeAMPM2());

    }

    @Override
    public int getItemCount() {
        return trainingTimeModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemFrom, itemAMPM1, itemTo, itemAMPM2;
        ImageView imgTrainingTimeDelete;
        OnTrainingTimeDeleteListener onTrainingTimeDeleteListener;

        public MyViewHolder(@NonNull View itemView, TrainingTimeAdapter.OnTrainingTimeDeleteListener onTrainingTimeDeleteListener) {
            super(itemView);
            itemFrom = itemView.findViewById(R.id.itemFrom);
            itemAMPM1 = itemView.findViewById(R.id.itemAMPM1);
            itemTo = itemView.findViewById(R.id.itemTo);
            itemAMPM2 = itemView.findViewById(R.id.itemAMPM2);
            imgTrainingTimeDelete = itemView.findViewById(R.id.imgTrainingTimeDelete);

            this.onTrainingTimeDeleteListener = onTrainingTimeDeleteListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onTrainingTimeDeleteListener.onTrainingTimeClick(getAdapterPosition());

        }
    }

    public interface OnTrainingTimeDeleteListener {
        void onTrainingTimeClick(int position);
    }
}
