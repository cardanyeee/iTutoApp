package com.ituto.android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TutorSignUpActivity extends AppCompatActivity {
    private Button btnSignUpTutor;
    private ProgressDialog dialog;
    private TextView txtMinTime, txtMinTime2, txtMaxTime, txtMaxTime2, time, period;
    private ArrayList<String> days;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private int hour, minute;
    private String availability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sign_up);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        txtMinTime = findViewById(R.id.txtMinTime);
        txtMinTime2 = findViewById(R.id.txtMinTime2);
        txtMaxTime = findViewById(R.id.txtMaxTime);
        txtMaxTime2 = findViewById(R.id.txtMaxTime2);

        btnSignUpTutor = findViewById(R.id.btnSignUpTutor);

        days = new ArrayList<String>();

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                hour = hourOfDay;
                minute = minuteOfHour;
                calendar = Calendar.getInstance();
                calendar.set(0, 0, 0, hour, minute);
                time.setText(DateFormat.format("hh:mm", calendar));
                period.setText(DateFormat.format("aa", calendar));
            }
        };

        btnSignUpTutor.setOnClickListener(v -> {
            signUpTutor();
        });
    }

    private void signUpTutor() {
        dialog.setMessage("Saving");
        dialog.show();
        convertArrayToString();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_TUTOR_ACCOUNT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    startActivity(new Intent(TutorSignUpActivity.this, TutorSubjectsActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                JSONArray availabilityArray = new JSONArray();
                for ( int a = 0; a < days.size(); a++) {
                    JSONObject availabilityObject = new JSONObject();
                    try {
                        availabilityObject.put("day", days.get(a));
                        availabilityObject.put("startTime", txtMinTime.getText().toString() + txtMinTime2.getText().toString());
                        availabilityObject.put("endTime", txtMaxTime.getText().toString() + txtMaxTime2.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    availabilityArray.put(availabilityObject);
                }
                map.put("availability", availabilityArray.toString());
                return map;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void convertArrayToString() {
        Object[] daysArray = days.toArray();
        for(int i = 0; i < daysArray.length ; i++){
            availability += (String)daysArray[i] + " ";
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void showTimePicker(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePickerDialogStyle, onTimeSetListener, hour, minute, false);
        timePickerDialog.show();

        switch (view.getId()) {
            case R.id.txtMinTime:
            case R.id.txtMinTime2:
                time = (TextView) findViewById(R.id.txtMinTime);
                period = (TextView) findViewById(R.id.txtMinTime2);
                break;

            case R.id.txtMaxTime:
            case R.id.txtMaxTime2:
                time = (TextView) findViewById(R.id.txtMaxTime);
                period = (TextView) findViewById(R.id.txtMaxTime2);
                break;
        }
    }

    public void onDayCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.ckbSunday:
                if (checked) {
                    days.add("Sunday");
                }
                break;

            case R.id.ckbMonday:
                if (checked) {
                    days.add("Monday");
                }
                break;

            case R.id.ckbTuesday:
                if (checked) {
                    days.add("Tuesday");
                }
                break;

            case R.id.ckbWednesday:
                if (checked) {
                    days.add("Wednesday");
                }
                break;

            case R.id.ckbThursday:
                if (checked) {
                    days.add("Thursday");
                }
                break;

            case R.id.ckbFriday:
                if (checked) {
                    days.add("Friday");
                }
                break;

            case R.id.ckbSaturday:
                if (checked) {
                    days.add("Saturday");
                }
                break;
        }
    }
}