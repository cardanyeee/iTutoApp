package com.ituto.android.Fragments.SessionFragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.ituto.android.Constant;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SessionRequestInfo extends Fragment {

    private View view;
    private SharedPreferences sharedPreferences;

    private ImageView imgBackButton;
    private CircleImageView imgUser;
    private TextView txtName, txtCourse, txtStartDate, txtDescription, txtSubject;
    private Chip chpMorning, chpAfternoon, chpEvening;
    private Button btnAcceptSchedule, btnDeclineSchedule;
    private MaterialButton btnCancelRequest;

    private DatePickerDialog datePickerDialog;
    private String sessionID;
    private String loggedInAs;
    private ArrayList<Integer> availableDays;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_request_info, container, false);
        init();
        return view;
    }

    private void init() {
        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        dialog.setCancelable(false);
        dialog.show();

        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        sessionID = getArguments().getString("_id");
        loggedInAs = sharedPreferences.getString("loggedInAs", "");

        imgBackButton = view.findViewById(R.id.imgBackButton);
        imgUser = view.findViewById(R.id.imgUser);
        txtName = view.findViewById(R.id.txtName);
        txtCourse = view.findViewById(R.id.txtCourse);
        txtDescription = view.findViewById(R.id.txtDescription);
        txtSubject = view.findViewById(R.id.txtSubject);
        txtStartDate = view.findViewById(R.id.txtStartDate);
        btnDeclineSchedule = view.findViewById(R.id.btnDeclineSchedule);
        btnAcceptSchedule = view.findViewById(R.id.btnAcceptSchedule);
        btnCancelRequest = view.findViewById(R.id.btnCancelRequest);

        if (loggedInAs.equals("TUTOR")) {
            btnAcceptSchedule.setVisibility(View.VISIBLE);
            btnDeclineSchedule.setVisibility(View.VISIBLE);
            btnCancelRequest.setVisibility(View.GONE);

            btnDeclineSchedule.setOnClickListener(view -> {
                Dialog declinedDialog = new Dialog(getContext());
                declinedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                declinedDialog.setContentView(R.layout.layout_dialog_decline);

                Button btnYes = declinedDialog.findViewById(R.id.btnYes);
                Button btnNo = declinedDialog.findViewById(R.id.btnNo);

                declinedDialog.show();

                btnYes.setOnClickListener(v -> {
                    declinedDialog.dismiss();
                    declineSession();
                });

                btnNo.setOnClickListener(v -> declinedDialog.cancel());
            });

            btnAcceptSchedule.setOnClickListener(view -> {
                Dialog acceptDialog = new Dialog(getContext());
                acceptDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                acceptDialog.setContentView(R.layout.layout_dialog_accept);

                Button btnYes = acceptDialog.findViewById(R.id.btnYes);
                Button btnNo = acceptDialog.findViewById(R.id.btnNo);

                acceptDialog.show();

                btnYes.setOnClickListener(v -> {
                    acceptDialog.dismiss();
                    acceptSession();
                });

                btnNo.setOnClickListener(v -> acceptDialog.cancel());
            });
        } else {
            btnAcceptSchedule.setVisibility(View.GONE);
            btnDeclineSchedule.setVisibility(View.GONE);
            btnCancelRequest.setVisibility(View.VISIBLE);

            btnCancelRequest.setOnClickListener(view -> {
                Dialog cancelDialog = new Dialog(getContext());
                cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                cancelDialog.setContentView(R.layout.layout_dialog_cancel);

                Button btnYes = cancelDialog.findViewById(R.id.btnYes);
                Button btnNo = cancelDialog.findViewById(R.id.btnNo);

                cancelDialog.show();

                btnYes.setOnClickListener(v -> {
                    cancelDialog.dismiss();
                    cancelSession();
                });

                btnNo.setOnClickListener(v -> cancelDialog.cancel());
            });
        }

        chpMorning = view.findViewById(R.id.chpMorning);
        chpAfternoon = view.findViewById(R.id.chpAfternoon);
        chpEvening = view.findViewById(R.id.chpEvening);

        imgBackButton.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());

        getSession();
    }

    private void getSession() {
        availableDays = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_SESSION + "/" + sessionID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONObject sessionObject = object.getJSONObject("session");
                    JSONObject tutorObject = sessionObject.getJSONObject("tutor");
                    JSONObject tuteeObject = sessionObject.getJSONObject("tutee");
                    JSONObject courseObject = tuteeObject.getJSONObject("course");
                    JSONObject courseTutorObject = tutorObject.getJSONObject("course");
                    JSONObject avatarObject = tuteeObject.getJSONObject("avatar");
                    JSONObject avatarTutorObject = tutorObject.getJSONObject("avatar");
                    JSONObject subjectObject = sessionObject.getJSONObject("subject");
                    JSONObject timeObject = sessionObject.getJSONObject("time");

                    JSONObject availabilityObject = object.getJSONObject("availability");
                    JSONArray days = availabilityObject.getJSONArray("days");
                    JSONArray time = availabilityObject.getJSONArray("time");

                    for (int i = 0; i < days.length(); i++) {
                        availableDays.add(parseDayString(days.getString(i)));
                    }

                    if (time.length() > 0) {
                        for (int a = 0; a < time.length(); a++) {
                            JSONObject t = time.getJSONObject(a);
                            if (t.get("timeOfDay").equals("Morning")) {
                                chpMorning.setVisibility(View.VISIBLE);
                                chpMorning.setText(t.getString("min") + " - " + t.getString("max"));

                                if (timeObject.get("timeOfDay").equals(t.get("timeOfDay"))) {
                                    chpMorning.setChecked(true);
                                }
                            }

                            if (t.get("timeOfDay").equals("Afternoon")) {
                                chpAfternoon.setVisibility(View.VISIBLE);
                                chpAfternoon.setText(t.getString("min") + " - " + t.getString("max"));

                                if (timeObject.get("timeOfDay").equals(t.get("timeOfDay"))) {
                                    chpAfternoon.setChecked(true);
                                }
                            }

                            if (t.get("timeOfDay").equals("Evening")) {
                                chpEvening.setVisibility(View.VISIBLE);
                                chpEvening.setText(t.getString("min") + " - " + t.getString("max"));

                                if (timeObject.get("timeOfDay").equals(t.get("timeOfDay"))) {
                                    chpEvening.setChecked(true);
                                }
                            }
                        }
                    }

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = format.parse(sessionObject.getString("startDate"));
                    String outputPattern = "yyyy-MM-dd";
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    if (loggedInAs.equals("TUTOR")) {
                        Glide.with(getContext()).load(avatarObject.getString("url")).override(500, 500).placeholder(R.drawable.blank_avatar).into(imgUser);
                        txtName.setText(tuteeObject.getString("firstname") + " " + tuteeObject.getString("lastname"));
                        txtCourse.setText(courseObject.getString("name"));
                    } else {
                        Glide.with(getContext()).load(avatarTutorObject.getString("url")).override(500, 500).placeholder(R.drawable.blank_avatar).into(imgUser);
                        txtName.setText(tutorObject.getString("firstname") + " " + tutorObject.getString("lastname"));
                        txtCourse.setText(courseTutorObject.getString("name"));
                        txtStartDate.setClickable(false);
                        chpMorning.setClickable(false);
                        chpAfternoon.setClickable(false);
                        chpEvening.setClickable(false);
                    }

                    txtStartDate.setText(outputFormat.format(date));
                    txtDescription.setText(sessionObject.getString("description"));
                    txtSubject.setText(subjectObject.getString("name"));

                    setDisableDaysInCalendar(availableDays);

                }

                dialog.dismiss();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
//                btnConfirmSchedule.setEnabled(false);
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
//            btnConfirmSchedule.setEnabled(false);
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

    private void declineSession() {
        StringRequest request = new StringRequest(Request.Method.PUT, Constant.DECLINE_SESSION + "/" + sessionID, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    StyleableToast.makeText(getContext(), "Session Declined", R.style.CustomToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "There was a problem declining the session", R.style.CustomToast).show();
            }

        }, error -> {
            error.printStackTrace();
            error.getMessage();
            StyleableToast.makeText(getContext(), "There was a problem declining the session", R.style.CustomToast).show();
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

    private void acceptSession() {
        StringRequest request = new StringRequest(Request.Method.PUT, Constant.ACCEPT_SESSION + "/" + sessionID, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    StyleableToast.makeText(getContext(), "Session Accepted", R.style.CustomToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "There was a problem accepting the session", R.style.CustomToast).show();
            }

        }, error -> {
            error.printStackTrace();
            error.getMessage();
            StyleableToast.makeText(getContext(), "There was a problem accepting the session", R.style.CustomToast).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("startDate", txtStartDate.getText().toString().trim());
                map.put("time", checkWhatTime().toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void cancelSession() {
        StringRequest request = new StringRequest(Request.Method.PUT, Constant.CANCEL_SESSION + "/" + sessionID, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    StyleableToast.makeText(getContext(), "Session Cancelled", R.style.CustomToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "There was a problem canceling the session", R.style.CustomToast).show();
            }

        }, error -> {
            error.printStackTrace();
            error.getMessage();
            StyleableToast.makeText(getContext(), "There was a problem canceling the session", R.style.CustomToast).show();
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

    private void setDisableDaysInCalendar(ArrayList<Integer> availableDays) {

        Calendar date;
        List<Calendar> disabledDays = new ArrayList<>();
        int weeks = 52;

        for (int a = 1; a < 8; a++) {
            if (!availableDays.contains(a)) {
                for (int i = -7; i < (weeks * 7); i = i + 7) {
                    date = Calendar.getInstance();
                    date.add(Calendar.DAY_OF_YEAR, (a - date.get(Calendar.DAY_OF_WEEK) + 7 + i));
                    disabledDays.add(date);
                }
            }

        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DATE, -1);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 1);


        datePickerDialog = DatePickerDialog.newInstance(
                (view, year1, monthOfYear, dayOfMonth) -> {
                    monthOfYear = monthOfYear + 1;
                    String d = year1 + "-" + monthOfYear + "-" + dayOfMonth;
                    txtStartDate.setText(d);
                },
                year,
                month,
                day);
        datePickerDialog.setDisabledDays(disabledDays.toArray(new Calendar[disabledDays.size()]));
        datePickerDialog.setMinDate(minDate);
        datePickerDialog.setMaxDate(maxDate);

        if (loggedInAs.equals("TUTOR")) {
            txtStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePickerDialog.show(getChildFragmentManager(), "");
                }
            });
        }

//        calendarTutorSchedule.setDisabledDays(disabledDays);
    }

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

    private JSONObject checkWhatTime() {
        JSONObject timeJSONObject = new JSONObject();
        try {
            if (chpMorning.isChecked()) {
                timeJSONObject.put("timeOfDay", "Morning");
                timeJSONObject.put("min", chpMorning.getText().toString().split(" - ")[0].trim());
                timeJSONObject.put("max", chpMorning.getText().toString().split(" - ")[1].trim());
            }

            if (chpAfternoon.isChecked()) {
                timeJSONObject.put("timeOfDay", "Afternoon");
                timeJSONObject.put("min", chpAfternoon.getText().toString().split(" - ")[0].trim());
                timeJSONObject.put("max", chpAfternoon.getText().toString().split(" - ")[1].trim());
            }

            if (chpEvening.isChecked()) {
                timeJSONObject.put("timeOfDay", "Evening");
                timeJSONObject.put("min", chpEvening.getText().toString().split(" - ")[0].trim());
                timeJSONObject.put("max", chpEvening.getText().toString().split(" - ")[1].trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAGTAGTAGTAG", timeJSONObject.toString());
        return timeJSONObject;
    }

}