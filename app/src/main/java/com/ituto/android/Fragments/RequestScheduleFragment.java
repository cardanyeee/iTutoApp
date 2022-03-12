package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarView;

import com.ituto.android.Constant;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Availability;
import com.ituto.android.Models.Subject;
import com.ituto.android.R;
import com.ituto.android.TutorSubjectsActivity;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestScheduleFragment extends Fragment {

    private View view;

    private CircleImageView imgTutorProfile;
    private TextView txtName, txtCourse;
    private AutoCompleteTextView txtSubject;
    private CalendarView calendarTutorSchedule;
    private Button btnConfirmSchedule;

    private Dialog dialog;
    private String tutorID;

    private ArrayList<Subject> subjectArrayList;
    private ArrayList<String> stringSubjectArrayList;
    private ArrayList<Integer> disabledDays;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_schedule, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        imgTutorProfile = view.findViewById(R.id.imgTutorProfile);
        txtName = view.findViewById(R.id.txtName);
        txtCourse = view.findViewById(R.id.txtCourse);
        txtSubject = view.findViewById(R.id.txtSubject);
        calendarTutorSchedule = view.findViewById(R.id.calendarTutorSchedule);
        calendarTutorSchedule.setMinimumDate(Calendar.getInstance());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        calendarTutorSchedule.setMaximumDate(calendar);
        btnConfirmSchedule = view.findViewById(R.id.btnConfirmSchedule);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        tutorID = getArguments().getString("_id");

        getTutorProfile();

        btnConfirmSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void setDisableDaysInCalendar(ArrayList<Integer> disabledDays) {

        Calendar date;
        List<Calendar> days = new ArrayList<>();
        int weeks = 52;

        for (int a = 0; a < disabledDays.size(); a++) {
            Log.d("TASADSADSA", disabledDays.get(a).toString());
            for (int i = -7; i < (weeks * 7) ; i = i + 7) {
                date = Calendar.getInstance();
                date.add(Calendar.DAY_OF_YEAR, (disabledDays.get(a) - date.get(Calendar.DAY_OF_WEEK) + 7 + i));
                // saturday = Calendar.getInstance();
                // saturday.add(Calendar.DAY_OF_YEAR, (Calendar.SATURDAY - saturday.get(Calendar.DAY_OF_WEEK) + i));
                // weekends.add(saturday);
                days.add(date);
            }
        }

        calendarTutorSchedule.setDisabledDays(days);

    }

    private void getTutorProfile() {
        stringSubjectArrayList = new ArrayList<>();
        subjectArrayList = new ArrayList<>();
        disabledDays = new ArrayList<>();

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

                    for (int i = 0; i < subjectsJSONArray.length(); i++) {
                        JSONObject subjectObject = subjectsJSONArray.getJSONObject(i);

                        if (subjectObject.getBoolean("active")) {
                            Subject subject = new Subject();

                            subject.setId(subjectObject.getString("_id"));
                            subject.setCode(subjectObject.getString("code"));
                            subject.setName(subjectObject.getString("name"));

                            stringSubjectArrayList.add(subjectObject.getString("name"));
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getContext(),
                                    R.layout.item_dropdown,
                                    R.id.txtDropdownItem,
                                    stringSubjectArrayList
                            );
                            subjectArrayList.add(subject);
                            txtSubject.setAdapter(arrayAdapter);
                        }

                    }


                    for (int i = 0; i < availabilityJSONArray.length(); i++) {
                        JSONObject availabilityObject = availabilityJSONArray.getJSONObject(i);
                        Availability availability = new Availability();
                        disabledDays.add(parseDayString(availabilityObject.getString("day")));
                    }

                    Picasso.get().load(avatarObject.getString("url")).resize(500, 0).into(imgTutorProfile);
                    txtName.setText(userObject.getString("firstname") + " " + userObject.getString("lastname"));
                    txtCourse.setText(courseObject.getString("name"));

                    setDisableDaysInCalendar(disabledDays);

                }

                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                btnConfirmSchedule.setEnabled(false);
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
            btnConfirmSchedule.setEnabled(false);
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

//    private void confirmSchedule() {
//        StringRequest request = new StringRequest(Request.Method.POST, Constant., response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")) {
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                StyleableToast.makeText(getContext(), "Unsuccessful", R.style.CustomToast).show();
//            }
//
//        }, error -> {
//            StyleableToast.makeText(getContext(), "Unable to request schedule", R.style.CustomToast).show();
//            error.printStackTrace();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = sharedPreferences.getString("token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer " + token);
//                return map;
//            }
//
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
//                JSONArray jsArray = new JSONArray(tutorSubjectsArrayList);
//                map.put("subjectID", jsArray.toString());
//                return map;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        queue.add(request);
//    }

    private int parseDayString(String day) {
        switch (day) {
            case "Sunday":
                return Calendar.SUNDAY;
            case "Monday":
                return Calendar.MONDAY;
            case "Tuesday":
                return Calendar.TUESDAY;
            case "Wednesday":
                return Calendar.WEDNESDAY;
            case "Thursday":
                return Calendar.THURSDAY;
            case "Friday":
                return Calendar.FRIDAY;
            case "Saturday":
                return Calendar.SATURDAY;
        }
        return 0;
    }
}