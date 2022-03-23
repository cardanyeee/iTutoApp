package com.ituto.android.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ituto.android.Models.Assessment;
import com.ituto.android.R;

import java.util.ArrayList;

public class AssessmentsAdapter extends RecyclerView.Adapter<AssessmentsAdapter.AssessmentHolder>{

    private Context context;
    private OnItemListener onItemListener;
    private SharedPreferences sharedPreferences;
    private ArrayList<Assessment> assessmentArrayList;

    public AssessmentsAdapter(Context context, ArrayList<Assessment> assessmentArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.onItemListener = onItemListener;
        this.assessmentArrayList = assessmentArrayList;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public AssessmentsAdapter.AssessmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_assessment, parent, false);
        return new AssessmentsAdapter.AssessmentHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentsAdapter.AssessmentHolder holder, int position) {
        Assessment assessment = assessmentArrayList.get(position);

        holder.txtAssessmentName.setText(assessment.getName());
        holder.txtScore.setText(String.valueOf(assessment.getScore()));
        holder.txtTotalItems.setText(String.valueOf(assessment.getTotalItems()));
    }

    @Override
    public int getItemCount() {
        return assessmentArrayList.size();
    }

    class AssessmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        OnItemListener onItemListener;
        private TextView txtAssessmentName, txtScore, txtTotalItems;

        public AssessmentHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            txtAssessmentName = itemView.findViewById(R.id.txtAssessmentName);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtTotalItems = itemView.findViewById(R.id.txtTotalItems);


            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            itemView.setClickable(true);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
