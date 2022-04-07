package com.ituto.android.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ituto.android.Constant;
import com.ituto.android.ConversationActivity;
import com.ituto.android.Fragments.TutorProfileFragment;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.Time;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorsAdapter extends RecyclerView.Adapter<TutorsAdapter.TutorHolder> {
    private Context context;
    private ArrayList<Tutor> tutorArrayList;
    private OnItemListener onItemListener;
    private SharedPreferences sharedPreferences;
    private Tutor tutor;

    public TutorsAdapter(Context context, ArrayList<Tutor> tutorArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.tutorArrayList = tutorArrayList;
        this.onItemListener = onItemListener;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TutorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tutor, parent, false);
        return new TutorHolder(view, onItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TutorHolder holder, @SuppressLint("RecyclerView") int position) {
        Tutor tutor = tutorArrayList.get(position);
        ArrayList<String> days = tutor.getDaysArrayList();
        ArrayList<Time> timeArrayList = tutor.getTimeArrayList();
        ArrayList<String> subjects = tutor.getSubjects();
        Glide.with(context).load(tutor.getAvatar()).override(500, 500).placeholder(R.drawable.blank_avatar).into(holder.imgTutor);
        holder.txtName.setText(tutor.getFirstname() + " " + tutor.getLastname());
        holder.txtCourse.setText(tutor.getCourse());
        holder.txtYearLevel.setText(tutor.getYearLevel() + " Year");
        holder.btnMessage.setOnClickListener(v -> message(holder, position));
//        if (sharedPreferences.getString("loggedInAs", "").equals("TUTOR")) {
//            holder.llyButtons.setVisibility(View.GONE);
//        }

        holder.txtDays.setText("");
        for (int d = 0; d < days.size(); d++) {
            if (d == 0) {
                holder.txtDays.setText("");
                holder.txtDays.setText(holder.txtDays.getText() + days.get(d));
                continue;
            }
            holder.txtDays.setText(holder.txtDays.getText() + ", " + days.get(d));
        }

        holder.txtTime.setText("");
        for (int t = 0; t < timeArrayList.size(); t++) {
            Time time = timeArrayList.get(t);
            if (t == 0) {
                holder.txtTime.setText("");
                holder.txtTime.setText(holder.txtTime.getText() + time.getMin() + " - " + time.getMax());
                continue;
            }
            holder.txtTime.setText(holder.txtTime.getText() + ", " + time.getMin() + " - " + time.getMax());
        }

        holder.txtSubjects.setText("");
        for (int s = 0; s < subjects.size(); s++) {
            if (s == 0) {
                holder.txtSubjects.setText("");
                holder.txtSubjects.setText(holder.txtSubjects.getText() + subjects.get(s));
                continue;
            }
            holder.txtSubjects.setText(holder.txtSubjects.getText() + ", " + subjects.get(s));
        }

//        holder.btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    private void message(TutorHolder holder, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CONVERSATION, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject conversationObject = object.getJSONObject("conversation");

                    Intent i = new Intent(((HomeActivity)context), ConversationActivity.class);
                    i.putExtra("conversationID", conversationObject.getString("_id"));
                    i.putExtra("name", tutorArrayList.get(position).getFirstname() + " " + tutorArrayList.get(position).getLastname());
                    i.putExtra("avatar", tutorArrayList.get(position).getAvatar());
                    i.putExtra("users", conversationObject.getJSONArray("users").toString());
                    context.startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("userId", tutorArrayList.get(position).getUserID());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(holder.itemView.getContext());
        queue.add(request);
    }

    class TutorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtName, txtCourse, txtYearLevel, txtDays, txtTime, txtSubjects;
        private CircleImageView imgTutor;
        private LinearLayout llyButtons;
        private Button btnProfile, btnMessage;

        public TutorHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            txtYearLevel = itemView.findViewById(R.id.txtYearLevel);
            txtDays = itemView.findViewById(R.id.txtDays);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtSubjects = itemView.findViewById(R.id.txtSubjects);
            imgTutor = itemView.findViewById(R.id.imgTutor);
            llyButtons = itemView.findViewById(R.id.llyButtons);
            btnProfile = itemView.findViewById(R.id.btnProfile);
            btnMessage = itemView.findViewById(R.id.btnMessage);

            btnProfile.setOnClickListener(this);

            itemView.setClickable(true);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return tutorArrayList.size();
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
