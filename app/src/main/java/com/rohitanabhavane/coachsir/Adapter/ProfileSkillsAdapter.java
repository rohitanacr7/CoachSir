package com.rohitanabhavane.coachsir.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitanabhavane.coachsir.Model.SkillsModels;
import com.rohitanabhavane.coachsir.R;

import java.util.ArrayList;

public class ProfileSkillsAdapter extends RecyclerView.Adapter<ProfileSkillsAdapter.MyViewHolder> {
    Context context;
    ArrayList<SkillsModels> skillsModelArrayList;
    private OnSkillDeleteListener mOnSkillDeleteListener;

    public ProfileSkillsAdapter(Context context, ArrayList<SkillsModels> skillsModelArrayList, OnSkillDeleteListener mOnSkillDeleteListener) {
        this.context = context;
        this.skillsModelArrayList = skillsModelArrayList;
        this.mOnSkillDeleteListener = mOnSkillDeleteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_skills_item, parent, false);
        return new ProfileSkillsAdapter.MyViewHolder(view, mOnSkillDeleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SkillsModels skillsModel = skillsModelArrayList.get(position);
        holder.txtSkills.setText(skillsModel.getSkill());

    }

    @Override
    public int getItemCount() {
        return skillsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtSkills;
        ImageView imgSkillDelete;
        OnSkillDeleteListener onSkillDeleteListener;

        public MyViewHolder(@NonNull View itemView, OnSkillDeleteListener onSkillDeleteListener) {
            super(itemView);
            txtSkills = itemView.findViewById(R.id.txtSkills);
            imgSkillDelete = itemView.findViewById(R.id.imgSkillsDelete);

            this.onSkillDeleteListener = onSkillDeleteListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onSkillDeleteListener.onSkillClick(getAdapterPosition());

        }
    }

    public interface OnSkillDeleteListener {
        void onSkillClick(int position);
    }
}

