package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitanabhavane.coachsir.Model.TrainingTimeModel;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;

public class ViewTrainingTimeAdapter extends RecyclerView.Adapter<ViewTrainingTimeAdapter.MyViewHolder> {
    Context context;
    ArrayList<TrainingTimeModel> trainingTimeModelArrayList;

    public ViewTrainingTimeAdapter(Context context, ArrayList<TrainingTimeModel> trainingTimeModelArrayList) {
        this.context = context;
        this.trainingTimeModelArrayList = trainingTimeModelArrayList;
    }

    @NonNull
    @Override
    public ViewTrainingTimeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewtrainingtime, parent, false);
        return new ViewTrainingTimeAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewTrainingTimeAdapter.MyViewHolder holder, int position) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemFrom, itemAMPM1, itemTo, itemAMPM2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemFrom = itemView.findViewById(R.id.viewFrom);
            itemAMPM1 = itemView.findViewById(R.id.viewAMPM1);
            itemTo = itemView.findViewById(R.id.viewTo);
            itemAMPM2 = itemView.findViewById(R.id.viewAMPM2);
        }
    }
}
