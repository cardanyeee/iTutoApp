package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Constant;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateTutorAvailabilityFragment extends Fragment {
    
    private View view;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;

    private CheckBox chkMorning, chkAfternoon, chkEvening, ckbSunday, ckbMonday, ckbTuesday, ckbWednesday, ckbThursday, ckbFriday, ckbSaturday;
    private TextInputEditText txtStartMorningTime, txtEndMorningTime, txtStartAfternoonTime, txtEndAfternoonTime, txtStartEveningTime, txtEndEveningTime;
    private Button btnUpdateAvailability;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_tutor_availability, container, false);
        init();
        return view;
    }

    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        RelativeLayout dialogLayout = dialog.findViewById(R.id.rllDialog);
        dialog.show();

        chkMorning = view.findViewById(R.id.chkMorning);
        chkAfternoon = view.findViewById(R.id.chkAfternoon);
        chkEvening = view.findViewById(R.id.chkEvening);

        txtStartMorningTime = view.findViewById(R.id.txtStartMorningTime);
        txtEndMorningTime = view.findViewById(R.id.txtEndMorningTime);
        txtStartAfternoonTime = view.findViewById(R.id.txtStartAfternoonTime);
        txtEndAfternoonTime = view.findViewById(R.id.txtEndAfternoonTime);
        txtStartEveningTime = view.findViewById(R.id.txtStartEveningTime);
        txtEndEveningTime = view.findViewById(R.id.txtEndEveningTime);

        ckbSunday = view.findViewById(R.id.ckbSunday);
        ckbMonday = view.findViewById(R.id.ckbMonday);
        ckbTuesday = view.findViewById(R.id.ckbTuesday);
        ckbWednesday = view.findViewById(R.id.ckbWednesday);
        ckbThursday = view.findViewById(R.id.ckbThursday);
        ckbFriday = view.findViewById(R.id.ckbFriday);
        ckbSaturday = view.findViewById(R.id.ckbSaturday);

        btnUpdateAvailability = view.findViewById(R.id.btnUpdateAvailability);

        getCurrentTutor();

    }

    private void getCurrentTutor() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.CURRENT_TUTOR, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject tutorObject = object.getJSONObject("tutor");
                    JSONObject availability = tutorObject.getJSONObject("availability");
                    JSONArray daysArray = availability.getJSONArray("days");
                    JSONArray timeArray = availability.getJSONArray("time");

                    for (int d = 0; d < daysArray.length(); d++) {
                        checkDays(daysArray.getString(d));
                    }

                    for (int t = 0; t < timeArray.length(); t++) {
                        JSONObject timeObject = timeArray.getJSONObject(t);
                        checkTime(timeObject.getString("timeOfDay"), timeObject.getString("min"), timeObject.getString("max"));
                    }
                }
                dialog.dismiss();
            } catch (JSONException e) {
                dialog.dismiss();
                e.printStackTrace();
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

    private void checkDays(String day) {
        if (day.equals("Sunday")) {
            ckbSunday.setChecked(true);
        }

        if (day.equals("Monday")) {
            ckbMonday.setChecked(true);
        }

        if (day.equals("Tuesday")) {
            ckbTuesday.setChecked(true);
        }

        if (day.equals("Wednesday")) {
            ckbWednesday.setChecked(true);
        }

        if (day.equals("Thursday")) {
            ckbThursday.setChecked(true);
        }

        if (day.equals("Friday")) {
            ckbFriday.setChecked(true);
        }

        if (day.equals("Saturday")) {
            ckbSaturday.setChecked(true);
        }
    }

    private void checkTime(String timeOfDay, String min, String max) {
        if (timeOfDay.equals("Morning")) {
            chkMorning.setChecked(true);
            txtStartMorningTime.setText(min);
            txtEndMorningTime.setText(max);
        }

        if (timeOfDay.equals("Afternoon")) {
            chkAfternoon.setChecked(true);
            txtStartAfternoonTime.setText(min);
            txtEndAfternoonTime.setText(max);
        }

        if (timeOfDay.equals("Morning")) {
            chkEvening.setChecked(true);
            txtStartEveningTime.setText(min);
            txtEndEveningTime.setText(max);
        }
    }

}