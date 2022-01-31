package com.ituto.android.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ituto.android.Constant;
import com.ituto.android.Models.Course;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeCoursesAdapter extends RecyclerView.Adapter<HomeCoursesAdapter.HomeCourseHolder> {

    private Context context;
    private ArrayList<Course> list;
    private OnItemListener onItemListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;

    public HomeCoursesAdapter(Context context, ArrayList<Course> list, OnItemListener onItemListener) {
        this.context = context;
        this.list = list;
        this.onItemListener = onItemListener;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public HomeCoursesAdapter.HomeCourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_course, parent, false);
        return new HomeCoursesAdapter.HomeCourseHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCoursesAdapter.HomeCourseHolder holder, int position) {
        Course course = list.get(position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeCourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txtProducerName, txtProducerEmail, txtProducerWebsite;
        private ImageView btnEditProducer, btnDeleteProducer;
        OnItemListener onItemListener;
        private SwipeRevealLayout swipeRevealLayout;

        public HomeCourseHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            itemView.setClickable(true);

        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}
