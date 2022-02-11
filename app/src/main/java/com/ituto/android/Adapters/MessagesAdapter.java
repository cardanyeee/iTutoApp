package com.ituto.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ituto.android.Components.HolderMe;
import com.ituto.android.Components.HolderYou;
import com.ituto.android.Constant;
import com.ituto.android.ConversationActivity;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Message> messageArrayList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;
    private Message message;
    private final int DATE = 0, YOU = 1, ME = 2;

    private String signedUserID;

    public MessagesAdapter(Context context, ArrayList<Message> messageArrayList, String signedUserID) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.signedUserID = signedUserID;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_holder_you, parent, false);
                viewHolder = new HolderYou(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.layout_holder_me, parent, false);
                viewHolder = new HolderMe(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        User user = message.getUser();
        String messageSenderID = user.getUserID();
//        Log.d("IDIDIDIDIDID" + position, signedUserID + " " + messageSenderID);
        if (signedUserID.equals(messageSenderID)) {
            return ME;
        } else if (!(signedUserID.equals(messageSenderID))) {
            return YOU;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case YOU:
                HolderYou vh2 = (HolderYou) holder;
                configureViewHolder2(vh2, position);
                break;
            default:
                HolderMe vh = (HolderMe) holder;
                configureViewHolder3(vh, position);
                break;
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }


    private void configureViewHolder3(HolderMe vh1, int position) {
        vh1.getChatText().setText(messageArrayList.get(position).getContent());
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
        vh1.getChatText().setText(messageArrayList.get(position).getContent());
        User user = messageArrayList.get(position).getUser();
        Picasso.get().load(user.getAvatar()).fit().centerCrop().into(vh1.getImgYou());
    }
}
