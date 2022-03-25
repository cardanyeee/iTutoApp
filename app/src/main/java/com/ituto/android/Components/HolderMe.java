package com.ituto.android.Components;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ituto.android.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMe extends RecyclerView.ViewHolder {

    private TextView time, chatText, txtFilename;
    private CircleImageView imgYou;
    private MaterialCardView cstImage;
    private ImageView imgAttachedImage;
    private LinearLayout llyFile;

    public HolderMe(View v) {
        super(v);
        chatText = (TextView) v.findViewById(R.id.txtMessageContent);
        txtFilename = (TextView) v.findViewById(R.id.txtFilename);
        cstImage = (MaterialCardView) v.findViewById(R.id.cstImage);
        imgAttachedImage = (ImageView) v.findViewById(R.id.imgAttachedImage);
        llyFile = (LinearLayout) v.findViewById(R.id.llyFile);
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

    public TextView getTxtFilename() {
        return txtFilename;
    }

    public void setTxtFilename(TextView txtFilename) {
        this.txtFilename = txtFilename;
    }

    public CircleImageView getImgYou() {
        return imgYou;
    }

    public void setImgYou(CircleImageView imgYou) {
        this.imgYou = imgYou;
    }

    public MaterialCardView getCstImage() {
        return cstImage;
    }

    public void setCstImage(MaterialCardView cstImage) {
        this.cstImage = cstImage;
    }

    public ImageView getImgAttachedImage() {
        return imgAttachedImage;
    }

    public void setImgAttachedImage(ImageView imgAttachedImage) {
        this.imgAttachedImage = imgAttachedImage;
    }

    public LinearLayout getLlyFile() {
        return llyFile;
    }

    public void setLlyFile(LinearLayout llyFile) {
        this.llyFile = llyFile;
    }
}
