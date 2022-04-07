package com.ituto.android.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ituto.android.Models.Session;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;

import java.util.ArrayList;

public class RecentSessionsAdapter extends RecyclerView.Adapter<RecentSessionsAdapter.RecentSessionHolder> {

    private Context context;
    private SessionsAdapter.OnItemListener onItemListener;
    private ArrayList<Session> sessionArrayList;
    private SharedPreferences sharedPreferences;
    private String loggedInAs;

    public RecentSessionsAdapter(Context context, ArrayList<Session> sessionArrayList) {
        this.context = context;
        this.onItemListener = onItemListener;
        this.sessionArrayList = sessionArrayList;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");
    }

    @NonNull
    @Override
    public RecentSessionsAdapter.RecentSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_sessions, parent, false);
        return new RecentSessionsAdapter.RecentSessionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSessionsAdapter.RecentSessionHolder holder, int position) {
        Session session = sessionArrayList.get(position);
        Tutor tutor = session.getTutor();
        User user = session.getTutee();

        holder.txtSubject.setText(session.getSubject().getName());
        holder.txtName.setText(loggedInAs.equals("TUTEE") ? tutor.getFirstname() + " " + tutor.getLastname() : user.getFirstname() + " " + user.getLastname());
        holder.txtDate.setText(session.getDisplayDate());
        if (session.getStatus().equals("Request")) {
            holder.imgIndicator.setColorFilter(ContextCompat.getColor(context, R.color.colorPending));
        }

        if (session.getStatus().equals("Ongoing")) {
            holder.imgIndicator.setColorFilter(ContextCompat.getColor(context, R.color.colorRating));
        }

        if (session.getStatus().equals("Done")) {
            holder.imgIndicator.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        }

        if (session.getStatus().equals("Declined")) {
            holder.imgIndicator.setColorFilter(ContextCompat.getColor(context, R.color.colorError));
        }

        if (session.getStatus().equals("Cancelled")) {
            holder.imgIndicator.setColorFilter(ContextCompat.getColor(context, R.color.colorError));
        }

        holder.txtStatus.setText(session.getStatus());
    }

    @Override
    public int getItemCount() {
        return sessionArrayList.size();
    }

    class RecentSessionHolder extends RecyclerView.ViewHolder {
        private TextView txtSubject, txtName, txtDate, txtStatus;
        private ImageView imgIndicator;

        public RecentSessionHolder(@NonNull View itemView) {
            super(itemView);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgIndicator = itemView.findViewById(R.id.imgIndicator);
        }
    }
}
