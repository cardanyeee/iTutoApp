package com.ituto.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ituto.android.ConversationActivity;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.Tutor;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorsAdapter extends RecyclerView.Adapter<TutorsAdapter.TutorHolder> {
    private Context context;
    private ArrayList<Tutor> tutorArrayList;
    private SharedPreferences sharedPreferences;
    private Tutor tutor;

    public TutorsAdapter(Context context, ArrayList<Tutor> tutorArrayList) {
        this.context = context;
        this.tutorArrayList = tutorArrayList;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TutorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tutor, parent, false);
        return new TutorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorHolder holder, int position) {
        tutor = tutorArrayList.get(position);

        Picasso.get().load(tutor.getAvatar()).resize(500, 0).into(holder.imgTutor);
        holder.txtName.setText(tutor.getFirstname() + " " + tutor.getLastname());

    }

    class TutorHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtCourse;
        private CircleImageView imgTutor;

        public TutorHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            imgTutor = itemView.findViewById(R.id.imgTutor);

            itemView.setClickable(true);
        }

    }


    @Override
    public int getItemCount() {
        return tutorArrayList.size();
    }
}
