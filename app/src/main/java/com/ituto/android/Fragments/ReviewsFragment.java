package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.ituto.android.Adapters.AnswerQuestionsAdapter;
import com.ituto.android.Adapters.ReviewsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Question;
import com.ituto.android.Models.Review;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class ReviewsFragment extends Fragment {

    private View view;

    private RecyclerView recyclerReviews;
    private ImageView imgBackButton;

    private ArrayList<Review> reviewArrayList;
    private ReviewsAdapter reviewsAdapter;
    private SharedPreferences sharedPreferences;
    private String tutorID;

    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reviews, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        dialog.setCancelable(false);
        dialog.show();

        tutorID = getArguments().getString("_id");

        imgBackButton = view.findViewById(R.id.imgBackButton);
        recyclerReviews = view.findViewById(R.id.recyclerReviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        getReviews();

        imgBackButton.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void getReviews() {
        reviewArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.TUTOR_REVIEWS + "/" + tutorID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONArray reviewsArray = object.getJSONArray("reviews");

                    for (int r = 0; r < reviewsArray.length(); r++) {
                        Review review = new Review();
                        JSONObject reviewObject = reviewsArray.getJSONObject(r);
                        JSONObject userObject = reviewObject.getJSONObject("tutee");
                        JSONObject subjectObject = reviewObject.getJSONObject("subject");

                        review.setFirstname(userObject.getString("firstname"));
                        review.setLastname(userObject.getString("lastname"));
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date date = format.parse(reviewObject.getString("reviewDate"));
                        String outputPattern = "MMM dd, yyyy";
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        review.setReviewDate(outputFormat.format(date));
                        review.setComment(reviewObject.getString("comment"));
                        review.setSubject(subjectObject.getString("name"));
                        review.setRating(reviewObject.getString("rating"));

                        reviewArrayList.add(review);
                    }

                    reviewsAdapter = new ReviewsAdapter(getContext(), reviewArrayList);
                    recyclerReviews.setAdapter(reviewsAdapter);
                }

                dialog.dismiss();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}