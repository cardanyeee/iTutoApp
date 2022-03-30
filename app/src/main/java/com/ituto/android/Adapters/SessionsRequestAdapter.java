package com.ituto.android.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.ituto.android.Models.Session;
import com.ituto.android.Models.Subject;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;

import java.util.ArrayList;

public class SessionsRequestAdapter extends RecyclerView.Adapter<SessionsRequestAdapter.RequestSessionHolder>  {

    private Context context;
    private OnItemListener onItemListener;
    private ArrayList<Session> sessionArrayList;
    private SharedPreferences sharedPreferences;
    private Session session;
    private String loggedInAs;

    public SessionsRequestAdapter(Context context, ArrayList<Session> sessionArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.onItemListener = onItemListener;
        this.sessionArrayList = sessionArrayList;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");
    }


    @NonNull
    @Override
    public SessionsRequestAdapter.RequestSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_session_request, parent, false);
        return new SessionsRequestAdapter.RequestSessionHolder(view, onItemListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SessionsRequestAdapter.RequestSessionHolder holder, int position) {
        session = sessionArrayList.get(position);
        Tutor tutor = session.getTutor();
        User user = session.getTutee();
        Subject subject = session.getSubject();
        Glide.with(context).load(user.getAvatar()).override(500, 500).placeholder(R.drawable.blank_avatar).into(holder.imgTutee);
        String name = user.getFirstname() + " " + user.getLastname();
        String s1 = " has sent you a request on tutoring the subject ";
        String subjectName = subject.getName();

        SpannableString request =  new SpannableString(name + s1 + subjectName);
        request.setSpan(new StyleSpan(Typeface.BOLD), 0,name.length(), 0);
        request.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary)), 0,name.length(), 0);

        request.setSpan(new StyleSpan(Typeface.NORMAL), name.length(), name.length() + s1.length(), 0);

        request.setSpan(new StyleSpan(Typeface.BOLD), name.length() + s1.length(), name.length() + s1.length() + subjectName.length(), 0);
        request.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondary)), name.length() + s1.length(), name.length() + s1.length() + subjectName.length(), 0);
        holder.txtRequest.setText(request);
//
//        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
//        SpannableString name = new SpannableString(user.getFirstname() + " " + user.getLastname());
//        name.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), flag);
//        Spanned subjectName = Html.fromHtml("<b>" + subject.getName() + "</b>");
//
//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(name);
//
//        holder.txtRequest.setText(builder + " sent you a request on tutoring the subject " + subjectName);

    }

    class RequestSessionHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        OnItemListener onItemListener;
        private TextView txtRequest;
        private ImageView imgTutee;
        private MaterialCardView sessionCardView;

        public RequestSessionHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            txtRequest = itemView.findViewById(R.id.txtRequest);
            imgTutee = itemView.findViewById(R.id.imgTutee);

            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            itemView.setClickable(true);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }

    }

    @Override
    public int getItemCount() {
        return sessionArrayList.size();
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
