package com.ituto.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ituto.android.ConversationActivity;
import com.ituto.android.HomeActivity;
import com.ituto.android.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorsAdapter extends RecyclerView.Adapter<TutorsAdapter.TutorHolder> {
    private Context context;
    private SharedPreferences sharedPreferences;

    @NonNull
    @Override
    public TutorsAdapter.TutorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TutorsAdapter.TutorHolder holder, int position) {

    }

    class TutorHolder extends RecyclerView.ViewHolder {

        private View contactView;
        private TextView txtUserName, txtLastMessage;
        private CircleImageView imgUserContact;
        ContactsAdapter.OnItemListener onItemListener;

        public TutorHolder(@NonNull View itemView, ContactsAdapter.OnItemListener onItemListener) {
            super(itemView);
            contactView = itemView;
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            imgUserContact = itemView.findViewById(R.id.imgUserContact);

            this.onItemListener = onItemListener;

            itemView.setClickable(true);
        }

    }


    @Override
    public int getItemCount() {
        return 0;
    }
}
