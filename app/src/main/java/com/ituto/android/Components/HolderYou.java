package com.ituto.android.Components;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ituto.android.R;

public class HolderYou extends RecyclerView.ViewHolder {

    private TextView time, chatText;

    public HolderYou(View v) {
        super(v);
        chatText = (TextView) v.findViewById(R.id.txtMessageContent);
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public TextView getChatText() {
        return chatText;
    }

    public void setChatText(TextView chatText) {
        this.chatText = chatText;
    }
}