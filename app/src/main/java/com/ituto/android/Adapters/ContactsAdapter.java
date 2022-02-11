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

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ituto.android.ConversationActivity;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Conversation;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

        Picasso.get().load(user.getAvatar()).resize(500, 0).into(holder.imgUserContact);
        holder.txtUserName.setText(user.getFirstname() + " " + user.getLastname());
        holder.txtLastMessage.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View contactView;
        private TextView txtUserName, txtLastMessage;
        private CircleImageView imgUserContact;
        OnItemListener onItemListener;

        public ContactHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            contactView = itemView;
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
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
            Log.d("TAGTAGTAGTAGTAGTAGTAGTAGTAGTAGTAG", message.getConversationID());
            Intent i = new Intent(((HomeActivity)context), ConversationActivity.class);
            i.putExtra("conversationID", message.getConversationID());
            i.putExtra("name", message.getUser().getFirstname() + " " + message.getUser().getLastname());
            i.putExtra("avatar", message.getUser().getAvatar());
            context.startActivity(i);
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}
