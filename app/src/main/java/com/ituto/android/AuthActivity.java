package com.ituto.android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Fragments.MainAuthFragment;
import com.ituto.android.Fragments.SignInFragment;
import com.ituto.android.Fragments.SignUpAvailabilityFragment;
import com.muddzdev.styleabletoast.StyleableToast;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new MainAuthFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdbTutor:
                if (checked)
                    SignInFragment.loggedInAs = "TUTOR";
                break;
            case R.id.rdbTutee:
                if (checked)
                    SignInFragment.loggedInAs = "TUTEE";
                break;
        }
    }


    @SuppressLint("NonConstantResourceId")
    public void showTimePicker(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePickerDialogStyle, SignUpAvailabilityFragment.onTimeSetListener, SignUpAvailabilityFragment.hour, SignUpAvailabilityFragment.minute, false);
        timePickerDialog.show();

//        switch (view.getId()) {
//            case R.id.txtStartMorningTime:
//                SignUpAvailabilityFragment.time = (TextInputEditText) SignUpAvailabilityFragment.view.findViewById(R.id.txtStartMorningTime);
//                break;
//
//            case R.id.txtEndMorningTime:
//                SignUpAvailabilityFragment.time = (TextInputEditText) SignUpAvailabilityFragment.view.findViewById(R.id.txtEndMorningTime);
//                break;
//
//            case R.id.txtStartAfternoonTime:
//                SignUpAvailabilityFragment.time = (TextInputEditText) SignUpAvailabilityFragment.view.findViewById(R.id.txtStartAfternoonTime);
//                break;
//
//            case R.id.txtEndAfternoonTime:
//                SignUpAvailabilityFragment.time = (TextInputEditText) SignUpAvailabilityFragment.view.findViewById(R.id.txtEndAfternoonTime);
//                break;
//
//            case R.id.txtStartEveningTime:
//                SignUpAvailabilityFragment.time = (TextInputEditText) SignUpAvailabilityFragment.view.findViewById(R.id.txtStartEveningTime);
//                break;
//
//            case R.id.txtEndEveningTime:
//                SignUpAvailabilityFragment.time = (TextInputEditText) SignUpAvailabilityFragment.view.findViewById(R.id.txtEndEveningTime);
//                break;
//        }
    }

    public void onDayCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.ckbSunday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Sunday");
                }
                break;

            case R.id.ckbMonday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Monday");
                }
                break;

            case R.id.ckbTuesday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Tuesday");
                }
                break;

            case R.id.ckbWednesday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Wednesday");
                }
                break;

            case R.id.ckbThursday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Thursday");
                }
                break;

            case R.id.ckbFriday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Friday");
                }
                break;

            case R.id.ckbSaturday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Saturday");
                }
                break;
        }
    }
}