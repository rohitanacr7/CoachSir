package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitanabhavane.coachsir.Model.SkillsModels;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;

public class ViewSkillsAdapter extends RecyclerView.Adapter<ViewSkillsAdapter.MyViewHolder> {
    Context context;
    ArrayList<SkillsModels> skillsModelArrayList;

    public ViewSkillsAdapter(Context context, ArrayList<SkillsModels> skillsModelArrayList) {
        this.context = context;
        this.skillsModelArrayList = skillsModelArrayList;
    }

    @NonNull
    @Override
    public ViewSkillsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_skills, parent, false);
        return new ViewSkillsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewSkillsAdapter.MyViewHolder holder, int position) {
        SkillsModels skillsModel = skillsModelArrayList.get(position);
        holder.txtSkills.setText(skillsModel.getSkill());
    }

    @Override
    public int getItemCount() {
        return skillsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtSkills;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSkills = itemView.findViewById(R.id.txtViewSkills);
        }

    }
}
