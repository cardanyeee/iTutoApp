package com.ituto.android.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ituto.android.Components.HolderMe;
import com.ituto.android.Components.HolderYou;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        if (!(messageArrayList.get(position).getFilename().isEmpty())) {
            if (checkIfImage(messageArrayList.get(position).getAttachment())) {
                vh1.getCstImage().setVisibility(View.VISIBLE);
                Glide.with(context).load(messageArrayList.get(position).getAttachment()).placeholder(R.drawable.animated_loader).override(1000, 400).into(vh1.getImgAttachedImage());
                vh1.getCstImage().setOnClickListener(view -> {
                    Dialog dialog = new Dialog(vh1.itemView.getContext(), R.style.DialogTheme);
                    dialog.setContentView(R.layout.layout_dialog_image);
                    TouchImageView imgPreview = dialog.findViewById(R.id.imgPreview);
                    Glide.with(context).load(messageArrayList.get(position).getAttachment()).placeholder(R.drawable.animated_loader2).into(imgPreview);
//                        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
                    dialog.setCancelable(true);
                    dialog.show();
                });
                vh1.getLlyFile().setVisibility(View.GONE);
            } else {
                vh1.getLlyFile().setVisibility(View.VISIBLE);
                vh1.getTxtFilename().setText(messageArrayList.get(position).getFilename());
//                vh1.getTxtFilename().setLayoutParams(textOutLayoutParams);
                vh1.getTxtFilename().setText(Html.fromHtml("<a href=\"" + messageArrayList.get(position).getDownloadLink() + "\">" + messageArrayList.get(position).getFilename() + "</a>"));
                vh1.getTxtFilename().setClickable(true);
                vh1.getTxtFilename().setMovementMethod(LinkMovementMethod.getInstance());
                vh1.getCstImage().setVisibility(View.GONE);
            }
        } else {
            vh1.getCstImage().setVisibility(View.GONE);
            vh1.getLlyFile().setVisibility(View.GONE);
        }
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
        vh1.getChatText().setText(messageArrayList.get(position).getContent());
        User user = messageArrayList.get(position).getUser();
        Glide.with(context).load(user.getAvatar()).placeholder(R.drawable.blank_avatar).centerCrop().into(vh1.getImgYou());

        if (!messageArrayList.get(position).getFilename().isEmpty()) {
            if (checkIfImage(messageArrayList.get(position).getAttachment())) {
                vh1.getCstImage().setVisibility(View.VISIBLE);
                Glide.with(context).load(messageArrayList.get(position).getAttachment()).placeholder(R.drawable.animated_loader).override(1000, 400).into(vh1.getImgAttachedImage());
                vh1.getCstImage().setOnClickListener(view -> {
                    Dialog dialog = new Dialog(vh1.itemView.getContext(), R.style.DialogTheme);
                    dialog.setContentView(R.layout.layout_dialog_image);
                    TouchImageView imgPreview = dialog.findViewById(R.id.imgPreview);
                    Glide.with(context).load(messageArrayList.get(position).getAttachment()).placeholder(R.drawable.animated_loader2).into(imgPreview);
//                        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
                    dialog.setCancelable(true);
                    dialog.show();
                });
                vh1.getLlyFile().setVisibility(View.GONE);
            } else {
                vh1.getLlyFile().setVisibility(View.VISIBLE);
                vh1.getTxtFilename().setText(messageArrayList.get(position).getFilename());
//                vh1.getTxtFilename().setLayoutParams(textOutLayoutParams);
                vh1.getTxtFilename().setText(Html.fromHtml("<a href=\"" + messageArrayList.get(position).getDownloadLink() + "\">" + messageArrayList.get(position).getFilename() + "</a>"));
                vh1.getTxtFilename().setClickable(true);
                vh1.getTxtFilename().setMovementMethod(LinkMovementMethod.getInstance());
                vh1.getCstImage().setVisibility(View.GONE);
            }
        } else {
            vh1.getCstImage().setVisibility(View.GONE);
            vh1.getLlyFile().setVisibility(View.GONE);
        }
    }

    private Boolean checkIfImage(String filename) {
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];

        if (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")) {
            return true;
        }

        return false;
    }
}
