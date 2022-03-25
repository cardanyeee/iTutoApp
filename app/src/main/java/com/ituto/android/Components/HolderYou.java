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

public class HolderYou extends RecyclerView.ViewHolder {

    private TextView time, txtMessageContent, txtFilename;
    private CircleImageView imgYou;
    private MaterialCardView cstImage;
    private ImageView imgAttachedImage;
    private LinearLayout llyFile;

    public HolderYou(View v) {
        super(v);
        txtMessageContent = (TextView) v.findViewById(R.id.txtMessageContent);
        imgYou = (CircleImageView) v.findViewById(R.id.imgYou);
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

    public TextView getTxtMessageContent() {
        return txtMessageContent;
    }

    public void setTxtMessageContent(TextView txtMessageContent) {
        this.txtMessageContent = txtMessageContent;
    }

    public TextView getTxtFilename() {
        return txtFilename;
    }

    public void setTxtFilename(TextView txtFilename) {
        this.txtFilename = txtFilename;
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