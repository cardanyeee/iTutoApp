package com.ituto.android.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.ituto.android.Adapters.MessagesAdapter;
import com.ituto.android.Constant;
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

public class TutorProfileFragment extends Fragment {

    private View view;

    private CircleImageView imgUserProfile;
    private LinearLayout llyAboutMe, llySubjects, llyContactInfo, llyEmail, llyPhone;
    private TextView txtTutorName, txtCourse, txtNumberOfStars, txtNumberOfReviews, txtAboutMe, txtSubjects, txtEmail, txtPhone;

    private Dialog dialog;
    private String tutorID;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutor_profile, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        imgUserProfile = view.findViewById(R.id.imgUserProfile);
        llyAboutMe = view.findViewById(R.id.llyAboutMe);
        llyContactInfo = view.findViewById(R.id.llyContactInfo);
        llyEmail = view.findViewById(R.id.llyEmail);
        llyPhone = view.findViewById(R.id.llyPhone);

        txtTutorName = view.findViewById(R.id.txtTutorName);
        txtCourse = view.findViewById(R.id.txtCourse);
        txtNumberOfStars = view.findViewById(R.id.txtNumberOfStars);
        txtNumberOfReviews = view.findViewById(R.id.txtNumberOfReviews);
        txtAboutMe = view.findViewById(R.id.txtAboutMe);
        txtSubjects = view.findViewById(R.id.txtSubjects);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);

        tutorID = getArguments().getString("_id");

        getTutorProfile();
    }

    private void getTutorProfile() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.TUTOR_PROFILE + "/" + tutorID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONObject tutorObject = object.getJSONObject("tutor");
                    JSONObject userObject = tutorObject.getJSONObject("userID");
                    JSONObject avatarObject = userObject.getJSONObject("avatar");
                    JSONObject courseObject = userObject.getJSONObject("course");
                    JSONArray subjectsJSONArray = tutorObject.getJSONArray("subjects");
                    JSONArray availabilityJSONArray = tutorObject.getJSONArray("availability");
                    JSONArray reviewsJSONArray = tutorObject.getJSONArray("reviews");

                    Picasso.get().load(avatarObject.getString("url")).resize(500, 0).into(imgUserProfile);
                    txtTutorName.setText(userObject.getString("firstname") + " " + userObject.getString("lastname"));
                    txtCourse.setText(courseObject.getString("name"));

                    if (subjectsJSONArray.length() > 0) {
                        for (int a = 0; a < subjectsJSONArray.length(); a++) {
                            JSONObject subjectObject = subjectsJSONArray.getJSONObject(a);
                            if (a == 0) {
                                txtSubjects.setText("");
                                txtSubjects.setText(txtSubjects.getText() + subjectObject.getString("name"));
                                continue;
                            }
                            txtSubjects.setText(txtSubjects.getText() + "\n" + subjectObject.getString("name"));
                        }
                    }

                    txtEmail.setText(userObject.getString("email"));

                }

                dialog.dismiss();
            } catch (JSONException e) {
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