package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
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
import com.ituto.android.Fragments.AuthFragments.SignInFragment;
import com.ituto.android.Fragments.AuthFragments.SignUpAvailabilityFragment;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateTutorAvailabilityFragment extends Fragment {

    private View view;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;
    public static ArrayList<String> days;

    private CheckBox chkMorning, chkAfternoon, chkEvening, ckbSunday, ckbMonday, ckbTuesday, ckbWednesday, ckbThursday, ckbFriday, ckbSaturday;
    private TextInputEditText txtStartMorningTime, txtEndMorningTime, txtStartAfternoonTime, txtEndAfternoonTime, txtStartEveningTime, txtEndEveningTime;
    private Button btnUpdateAvailability;
    public static TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private Calendar calendar;
    public TextInputEditText time;
    private JSONObject availabilityObject;

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
        days = new ArrayList<String>();

        btnUpdateAvailability = view.findViewById(R.id.btnUpdateAvailability);

        txtStartMorningTime.setOnClickListener(view -> {
            if (chkMorning.isChecked()) {
                time = (TextInputEditText) txtStartMorningTime;
                TimePickerDialog timePickerDialog;
                timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 6, 0, false);
                timePickerDialog.setMinTime(6, 0, 0);
                timePickerDialog.setMaxTime(11, 59, 0);
                timePickerDialog.setTimeInterval(1, 5, 60);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "StartMorningTime");
            }
        });

        txtEndMorningTime.setOnClickListener(view -> {
            if (chkMorning.isChecked()) {
                time = (TextInputEditText) txtEndMorningTime;
                if (txtStartMorningTime.getText().toString().isEmpty()) {
                    StyleableToast.makeText(getContext(), "Set minimum time first", R.style.CustomToast).show();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(txtStartMorningTime.getText().toString());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    TimePickerDialog timePickerDialog;
                    timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 11, 55, false);
                    timePickerDialog.setMinTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
                    timePickerDialog.setMaxTime(11, 55, 0);
                    timePickerDialog.setTimeInterval(1, 5, 60);
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), "EndMorningTime");
                } catch (ParseException e) {

                }
            }
        });

        txtStartAfternoonTime.setOnClickListener(view -> {
            if (chkAfternoon.isChecked()) {
                time = (TextInputEditText) txtStartAfternoonTime;
                TimePickerDialog timePickerDialog;
                timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 12, 0, false);
                timePickerDialog.setMinTime(12, 0, 0);
                timePickerDialog.setMaxTime(17, 0, 0);
                timePickerDialog.setTimeInterval(1, 5, 60);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "StartAfternoonTime");
            }
        });

        txtEndAfternoonTime.setOnClickListener(view -> {
            if (chkAfternoon.isChecked()) {
                time = (TextInputEditText) txtEndAfternoonTime;
                if (txtStartAfternoonTime.getText().toString().isEmpty()) {
                    StyleableToast.makeText(getContext(), "Set minimum time first", R.style.CustomToast).show();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(txtStartAfternoonTime.getText().toString());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    TimePickerDialog timePickerDialog;
                    timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 17, 0, false);
                    timePickerDialog.setMinTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
                    timePickerDialog.setMaxTime(17, 0, 0);
                    timePickerDialog.setTimeInterval(1, 5, 60);
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), "EndAfternoonTime");
                } catch (ParseException e) {
                }
            }
        });

        txtStartEveningTime.setOnClickListener(view -> {
            if (chkEvening.isChecked()) {
                time = (TextInputEditText) txtStartEveningTime;
                TimePickerDialog timePickerDialog;
                timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 17, 0, false);
                timePickerDialog.setMinTime(17, 0, 0);
                timePickerDialog.setMaxTime(23, 59, 0);
                timePickerDialog.setTimeInterval(1, 5, 60);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), "StartAfternoonTime");
            }
        });

        txtEndEveningTime.setOnClickListener(view -> {
            if (chkEvening.isChecked()) {
                time = (TextInputEditText) txtEndEveningTime;
                if (txtStartEveningTime.getText().toString().isEmpty()) {
                    StyleableToast.makeText(getContext(), "Set minimum time first", R.style.CustomToast).show();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date date = dateFormat.parse(txtStartEveningTime.getText().toString());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    TimePickerDialog timePickerDialog;
                    timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, 23, 59, false);
                    timePickerDialog.setMinTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
                    timePickerDialog.setMaxTime(23, 59, 0);
                    timePickerDialog.setTimeInterval(1, 5, 60);
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), "EndAfternoonTime");
                } catch (ParseException e) {
                }
            }
        });

        onTimeSetListener = (view, hourOfDay, minute, second) -> {
            calendar = Calendar.getInstance();
            calendar.set(0, 0, 0, hourOfDay, minute);
            time.setText(DateFormat.format("hh:mm aa", calendar));
        };

        ckbSunday.setOnClickListener(this::onDayCheckboxClicked);

        ckbMonday.setOnClickListener(this::onDayCheckboxClicked);

        ckbTuesday.setOnClickListener(this::onDayCheckboxClicked);

        ckbWednesday.setOnClickListener(this::onDayCheckboxClicked);

        ckbThursday.setOnClickListener(this::onDayCheckboxClicked);

        ckbFriday.setOnClickListener(this::onDayCheckboxClicked);

        ckbSaturday.setOnClickListener(this::onDayCheckboxClicked);

        getCurrentTutor();

        btnUpdateAvailability.setOnClickListener(v -> {
            if (validate()) {
                availabilityObject = new JSONObject();
                JSONArray daysJSONArray = new JSONArray();
                JSONArray timeJSONArray = new JSONArray();
                for (int a = 0; a < days.size(); a++) {
                    daysJSONArray.put(days.get(a));
                }
                try {
                    availabilityObject.put("days", daysJSONArray);

                    if (chkMorning.isChecked()) {
                        JSONObject morning = new JSONObject();
                        morning.put("timeOfDay", "Morning");
                        morning.put("min", txtStartMorningTime.getText().toString().trim());
                        morning.put("max", txtEndMorningTime.getText().toString().trim());
                        timeJSONArray.put(morning);
                    }

                    if (chkAfternoon.isChecked()) {
                        JSONObject afternoon = new JSONObject();
                        afternoon.put("timeOfDay", "Afternoon");
                        afternoon.put("min", txtStartAfternoonTime.getText().toString().trim());
                        afternoon.put("max", txtEndAfternoonTime.getText().toString().trim());
                        timeJSONArray.put(afternoon);
                    }

                    if (chkEvening.isChecked()) {
                        JSONObject evening = new JSONObject();
                        evening.put("timeOfDay", "Evening");
                        evening.put("min", txtStartEveningTime.getText().toString().trim());
                        evening.put("max", txtEndEveningTime.getText().toString().trim());
                        timeJSONArray.put(evening);
                    }

                    availabilityObject.put("time", timeJSONArray);

                    updateAvailability();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
        days.add(day);
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

    private void checkTime(@NonNull String timeOfDay, String min, String max) {
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

        if (timeOfDay.equals("Evening")) {
            chkEvening.setChecked(true);
            txtStartEveningTime.setText(min);
            txtEndEveningTime.setText(max);
        }
    }

    private boolean validate() {

        if (!(days.size() > 0)) {
            StyleableToast.makeText(getContext(), "Please select days on when you are available", R.style.CustomToast).show();
            return false;
        }

        if (!(chkMorning.isChecked() || chkAfternoon.isChecked() || chkEvening.isChecked())) {
            StyleableToast.makeText(getContext(), "Please select select and input what time you are available", R.style.CustomToast).show();
            return false;
        }

        if (chkMorning.isChecked() && (txtStartMorningTime.getText().toString().trim().isEmpty() || txtEndMorningTime.getText().toString().trim().isEmpty())) {
            StyleableToast.makeText(getContext(), "Please complete your morning schedule for availability", R.style.CustomToast).show();
            return false;
        }

        if (chkAfternoon.isChecked() && (txtStartAfternoonTime.getText().toString().trim().isEmpty() || txtEndAfternoonTime.getText().toString().trim().isEmpty())) {
            StyleableToast.makeText(getContext(), "Please complete your afternoon schedule  for availability", R.style.CustomToast).show();
            return false;
        }

        if (chkEvening.isChecked() && (txtStartEveningTime.getText().toString().trim().isEmpty() || txtEndEveningTime.getText().toString().trim().isEmpty())) {
            StyleableToast.makeText(getContext(), "Please complete your evening schedule  for availability", R.style.CustomToast).show();
            return false;
        }

        return true;
    }

    public void onDayCheckboxClicked(@NonNull View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.ckbSunday:
                if (checked) {
                    days.add("Sunday");
                } else {
                    days.remove("Sunday");
                }
                break;

            case R.id.ckbMonday:
                if (checked) {
                    days.add("Monday");
                } else {
                    days.remove("Monday");
                }
                break;

            case R.id.ckbTuesday:
                if (checked) {
                    days.add("Tuesday");
                } else {
                    days.remove("Tuesday");
                }
                break;

            case R.id.ckbWednesday:
                if (checked) {
                    days.add("Wednesday");
                } else {
                    days.remove("Wednesday");
                }
                break;

            case R.id.ckbThursday:
                if (checked) {
                    days.add("Thursday");
                } else {
                    days.remove("Thursday");
                }
                break;

            case R.id.ckbFriday:
                if (checked) {
                    days.add("Friday");
                } else {
                    days.remove("Friday");
                }
                break;

            case R.id.ckbSaturday:
                if (checked) {
                    days.add("Saturday");
                } else {
                    days.remove("Saturday");
                }
                break;
        }
    }

    private void updateAvailability() {
        StringRequest request = new StringRequest(Request.Method.PUT, Constant.UPDATE_AVAILABILITY, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    StyleableToast.makeText(getContext(), "Availability Updated Successfully!", R.style.CustomToast).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "Availability Updated Unsuccessfully!", R.style.CustomToast).show();
            }

        }, error -> {
            StyleableToast.makeText(getContext(), "Availability Updated Unsuccessfully!", R.style.CustomToast).show();
            error.printStackTrace();
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
                map.put("availability",  availabilityObject.toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}