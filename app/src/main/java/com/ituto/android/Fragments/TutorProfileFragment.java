package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.ituto.android.Constant;
import com.ituto.android.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorProfileFragment extends Fragment {

    private View view;

    private ImageView imgBackButton;
    private CircleImageView imgUserProfile;
    private LinearLayout llyAboutMe, llySubjects, llyContactInfo, llyEmail, llyPhone, llyMorning, llyAfternoon, llyEvening;
    private TextView txtTutorName, txtCourse, txtNumberOfStars, txtNumberOfReviews, txtAboutMe, txtSubjects, txtDays, timeMorning, timeAfternoon, timeEvening, txtEmail, txtPhone;
    private Button btnRequestSchedule;

    private Dialog dialog;
    private String tutorID;

    private SharedPreferences sharedPreferences;
    private String loggedInAs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutor_profile, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");

        imgBackButton = view.findViewById(R.id.imgBackButton);
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
        txtDays = view.findViewById(R.id.txtDays);
        llyMorning = view.findViewById(R.id.llyMorning);
        llyAfternoon = view.findViewById(R.id.llyAfternoon);
        llyEvening = view.findViewById(R.id.llyEvening);
        timeMorning = view.findViewById(R.id.timeMorning);
        timeAfternoon = view.findViewById(R.id.timeAfternoon);
        timeEvening = view.findViewById(R.id.timeEvening);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        btnRequestSchedule = view.findViewById(R.id.btnRequestSchedule);

        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.setCancelable(false);
        dialog.show();

        Log.d("TAG", String.valueOf(dialog.getWindow().getAttributes().height));

        tutorID = getArguments().getString("_id");

        getTutorProfile();

        if (loggedInAs.equals("TUTOR")) {
            btnRequestSchedule.setVisibility(View.GONE);
        }

        imgBackButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        btnRequestSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                RequestScheduleFragment requestScheduleFragment = new RequestScheduleFragment();
                requestScheduleFragment.setArguments(bundle);
                bundle.putString("_id", tutorID);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.slide_in,
                        0// popExit
                ).replace(R.id.fragment_container, requestScheduleFragment).commit();
            }
        });
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
                    JSONObject availabilityObject = tutorObject.getJSONObject("availability");
                    JSONArray daysJSONArray = availabilityObject.getJSONArray("days");
                    JSONArray timeJSONArray = availabilityObject.getJSONArray("time");
                    JSONArray reviewsJSONArray = tutorObject.getJSONArray("reviews");

                    Picasso.get().load(avatarObject.getString("url")).placeholder(R.drawable.blank_avatar).into(imgUserProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            dialog.dismiss();
                        }
                    });
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

                    if (daysJSONArray.length() > 0) {
                        for (int a = 0; a < daysJSONArray.length(); a++) {
                            if (a == 0) {
                                txtDays.setText("");
                                txtDays.setText(txtDays.getText() + daysJSONArray.getString(a));
                                continue;
                            }
                            txtDays.setText(txtDays.getText() + ", " + daysJSONArray.getString(a));
                        }
                    }

                    if (timeJSONArray.length() > 0) {
                        for (int a = 0; a < timeJSONArray.length(); a++) {
                            JSONObject time = timeJSONArray.getJSONObject(a);
                            if (time.get("timeOfDay").equals("Morning")) {
                                llyMorning.setVisibility(View.VISIBLE);
                                timeMorning.setText(time.getString("min") + " - " + time.getString("max"));
                            }

                            if (time.get("timeOfDay").equals("Afternoon")) {
                                llyAfternoon.setVisibility(View.VISIBLE);
                                timeAfternoon.setText(time.getString("min") + " - " + time.getString("max"));
                            }

                            if (time.get("timeOfDay").equals("Evening")) {
                                llyEvening.setVisibility(View.VISIBLE);
                                timeEvening.setText(time.getString("min") + " - " + time.getString("max"));
                            }
                        }
                    }

                    txtEmail.setText(userObject.getString("email"));
                    txtPhone.setText(userObject.has("phone") ? userObject.getString("phone") : " ");

                }

            } catch (JSONException e) {
                e.printStackTrace();
                btnRequestSchedule.setEnabled(false);
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
            btnRequestSchedule.setEnabled(false);
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