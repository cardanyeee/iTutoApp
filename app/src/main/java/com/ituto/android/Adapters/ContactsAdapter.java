package com.ituto.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ituto.android.ConversationActivity;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Conversation;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactHolder>{

    private Context context;
    private ArrayList<Message> messageArrayList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;
    private OnItemListener onItemListener;
    private Message message;
    private User user;

    public ContactsAdapter(Context context, ArrayList<Message> messageArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.onItemListener = onItemListener;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        message = messageArrayList.get(position);
        user = message.getUser();

        Glide.with(context).load(user.getAvatar()).override(500, 500).placeholder(R.drawable.blank_avatar).into(holder.imgUserContact);
        holder.txtUserName.setText(user.getFirstname() + " " + user.getLastname());
        holder.txtLastMessage.setText(message.getContent());

        if (message.getTimestamp() == null) {
            message.setTimestamp(" ");
        } else {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = format.parse(message.getTimestamp());
                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                outputFormat.setTimeZone(TimeZone.getDefault());
                message.setTimestamp(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.txtTime.setText(message.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View contactView;
        private TextView txtUserName, txtLastMessage, txtTime;
        private CircleImageView imgUserContact;
        OnItemListener onItemListener;

        public ContactHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            contactView = itemView;
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgUserContact = itemView.findViewById(R.id.imgUserContact);

            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            itemView.setClickable(true);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick((getBindingAdapterPosition()));
            message = messageArrayList.get(getBindingAdapterPosition());
            user = message.getUser();
            Intent i = new Intent(((HomeActivity)context), ConversationActivity.class);
            i.putExtra("conversationID", message.getConversationID());
            i.putExtra("name", message.getUser().getFirstname() + " " + message.getUser().getLastname());
            i.putExtra("avatar", message.getUser().getAvatar());
            i.putExtra("users", message.getConversation().getUserIDArrayList().toString());
            context.startActivity(i);
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}
