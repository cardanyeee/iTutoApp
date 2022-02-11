package com.ituto.android.Components;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ituto.android.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderYou extends RecyclerView.ViewHolder {

    private TextView time, txtMessageContent;
    private CircleImageView imgYou;

    public HolderYou(View v) {
        super(v);
        txtMessageContent = (TextView) v.findViewById(R.id.txtMessageContent);
        imgYou = (CircleImageView) v.findViewById(R.id.imgYou);
    }

    public TextView getTime() {
        return time;
    }

    public void setTime(TextView time) {
        this.time = time;
    }

    public TextView getChatText() {
        return txtMessageContent;
    }

    public void setChatText(TextView chatText) {
        this.txtMessageContent = chatText;
    }

    public ImageView getImgYou() {
        return imgYou;
    }

    public void setImgYou(CircleImageView imgYou) {
        this.imgYou = imgYou;
    }
}