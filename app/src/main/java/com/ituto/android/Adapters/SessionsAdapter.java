package com.ituto.android.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ituto.android.Models.Session;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionHolder> {
    private Context context;
    private OnItemListener onItemListener;
    private ArrayList<Session> sessionArrayList;
    private SharedPreferences sharedPreferences;
    private Session session;
    private String loggedInAs;

    public SessionsAdapter(Context context, ArrayList<Session> sessionArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.onItemListener = onItemListener;
        this.sessionArrayList = sessionArrayList;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");
    }

    @NonNull
    @Override
    public SessionsAdapter.SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_session, parent, false);
        return new SessionsAdapter.SessionHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.SessionHolder holder, int position) {
        session = sessionArrayList.get(position);
        Tutor tutor = session.getTutor();
        User user = session.getTutee();

        try {
            holder.txtSubjectName.setText(session.getSubject().getName());
            holder.txtTime.setText(session.getMinTime() + " - " + session.getMaxTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = format.parse(session.getStartDate());
            String outputPattern = "MMMM dd, yyyy";
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            holder.txtStartDate.setText(outputFormat.format(date));
            holder.txtTutorTutee.setText(loggedInAs.equals("TUTEE") ? tutor.getFirstname() + " " + tutor.getLastname() : user.getFirstname() + " " + user.getLastname());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    class SessionHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        OnItemListener onItemListener;
        private TextView txtSubjectName, txtStartDate, txtTime, txtTutorTutee;
        private ImageView imgMore;
        private MaterialCardView sessionCardView;

        public SessionHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            sessionCardView = itemView.findViewById(R.id.sessionCardView);
            txtSubjectName = itemView.findViewById(R.id.txtSubjectName);
            txtStartDate = itemView.findViewById(R.id.txtStartDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTutorTutee = itemView.findViewById(R.id.txtTutorTutee);

            this.onItemListener = onItemListener;

            sessionCardView.setOnClickListener(this);

            itemView.setClickable(true);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }

    }

    @Override
    public int getItemCount() {
        return sessionArrayList.size();
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
