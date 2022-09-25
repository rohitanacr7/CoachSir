package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rohitanabhavane.coachsir.Activities.BecomeACoachActivity;
import com.rohitanabhavane.coachsir.Activities.ViewProfileActivity;
import com.rohitanabhavane.coachsir.Model.CoachesModel;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CoachesAdapter extends RecyclerView.Adapter<CoachesAdapter.MyViewHolder> {
    Context context;
    ArrayList<CoachesModel> coachesModelArrayList;
    ArrayList<CoachesModel> filteredCoachesModelArrayList;
    String loggedInEmail;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    public CoachesAdapter(Context context, ArrayList<CoachesModel> coachesModelArrayList) {
        this.context = context;
        this.coachesModelArrayList = coachesModelArrayList;
        this.filteredCoachesModelArrayList = coachesModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_users, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CoachesModel coachesModel = filteredCoachesModelArrayList.get(position);

        holder.txtUserFullName.setText(coachesModel.getFullName());
        holder.txtUserExpYears.setText(coachesModel.getExpYears());
        holder.txtExpMonths.setText(coachesModel.getExpMonths());
        holder.txtUserCity.setText(coachesModel.getCity());
        holder.txtGroupFees.setText(coachesModel.getGroupTrainingFees());
        holder.txtAge.setText(coachesModel.getAge());
        holder.txtSportsType.setText(coachesModel.getSelectSports());

        Glide.with(context).load(coachesModel.getProfileImgLink()).into(holder.imgUserProfile);
    }

    @Override
    public int getItemCount() {
        return filteredCoachesModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgUserProfile;
        TextView txtUserFullName, txtUserExpYears, txtExpMonths, txtUserCity, txtGroupFees, txtSportsType, txtAge, txtViews;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            if (mAuth.getCurrentUser() != null) {
                loggedInEmail = mAuth.getCurrentUser().getEmail();
            }

            itemView.setOnClickListener(this);

            imgUserProfile = itemView.findViewById(R.id.imgUserProfile);
            txtUserFullName = itemView.findViewById(R.id.txtUserFullName);
            txtUserExpYears = itemView.findViewById(R.id.txtUserExpYears);
            txtExpMonths = itemView.findViewById(R.id.txtExpMonths);
            txtUserCity = itemView.findViewById(R.id.txtUserCity);
            txtGroupFees = itemView.findViewById(R.id.txtGroupFees);
            txtSportsType = itemView.findViewById(R.id.txtSportsType);
            txtAge = itemView.findViewById(R.id.txtAge);
            txtViews = itemView.findViewById(R.id.txtViews);


        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            CoachesModel coachesModel = filteredCoachesModelArrayList.get(position);

            String strProfileImg = coachesModel.getProfileImgLink();
            String strFullName = coachesModel.getFullName();
            String strEmail = coachesModel.getEmail();
            String strDesc = coachesModel.getDesc();
            String strExpYear = coachesModel.getExpYears();
            String strExpMonth = coachesModel.getExpMonths();
            String strTrainingGroundDetails = coachesModel.getTrainingGroundDetails();
            String strGroupTrainingFees = coachesModel.getGroupTrainingFees();
            String strPersonalTrainingFees = coachesModel.getPersonalTrainingFees();
            String strSportType = coachesModel.getSelectSports();

            Intent intent = new Intent(context, ViewProfileActivity.class);
            intent.putExtra("ProfileImg", strProfileImg);
            intent.putExtra("Email", strEmail);
            intent.putExtra("FullName", strFullName);
            intent.putExtra("Desc", strDesc);
            intent.putExtra("ExpYear", strExpYear);
            intent.putExtra("ExpMonth", strExpMonth);
            intent.putExtra("TrainingGroundDetails", strTrainingGroundDetails);
            intent.putExtra("GroupTrainingFees", strGroupTrainingFees);
            intent.putExtra("PersonalTrainingFees", strPersonalTrainingFees);
            intent.putExtra("SportType", strSportType);

            context.startActivity(intent);
        }

    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String Key = charSequence.toString();
                if (Key.isEmpty()) {
                    filteredCoachesModelArrayList = coachesModelArrayList;
                } else {
                    ArrayList<CoachesModel> filteredArrayList = new ArrayList<>();
                    for (CoachesModel row : coachesModelArrayList) {
                        if (row.getFullName().toLowerCase().contains(Key.toLowerCase())) {
                            filteredArrayList.add(row);
                        }
                    }
                    filteredCoachesModelArrayList = filteredArrayList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredCoachesModelArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredCoachesModelArrayList = (ArrayList<CoachesModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
