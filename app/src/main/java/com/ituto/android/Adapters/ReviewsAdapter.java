package com.ituto.android.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.ituto.android.Models.Question;
import com.ituto.android.Models.Review;
import com.ituto.android.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private Context context;
    private ArrayList<Review> reviewArrayList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;

    public ReviewsAdapter(Context context, ArrayList<Review> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ReviewsAdapter.ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewHolder holder, int position) {
        Review review = reviewArrayList.get(position);

        holder.txtName.setText(review.getFirstname() + " " + review.getLastname());
        holder.txtDate.setText(review.getReviewDate());
        holder.txtComment.setText(review.getComment());
        holder.txtSubject.setText(review.getSubject());
        holder.rtbRating.setRating(Float.parseFloat(review.getRating()));

    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }



    class ReviewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgUser;
        private TextView txtName, txtDate, txtComment, txtSubject;
        private RatingBar rtbRating;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            rtbRating = itemView.findViewById(R.id.rtbRating);
        }
    }
}
