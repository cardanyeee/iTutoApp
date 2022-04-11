package com.ituto.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.ituto.android.Fragments.AuthFragments.MainAuthFragment;
import com.ituto.android.Fragments.AuthFragments.SignInFragment;
import com.ituto.android.Fragments.AuthFragments.SignUpAvailabilityFragment;
import com.ituto.android.Services.SocketIOService;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        stopService(new Intent(getBaseContext(), SocketIOService.class));
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new MainAuthFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void onDayCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.ckbSunday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Sunday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Sunday");
                }
                break;

            case R.id.ckbMonday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Monday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Monday");
                }
                break;

            case R.id.ckbTuesday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Tuesday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Tuesday");
                }
                break;

            case R.id.ckbWednesday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Wednesday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Wednesday");
                }
                break;

            case R.id.ckbThursday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Thursday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Thursday");
                }
                break;

            case R.id.ckbFriday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Friday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Friday");
                }
                break;

            case R.id.ckbSaturday:
                if (checked) {
                    SignUpAvailabilityFragment.days.add("Saturday");
                } else {
                    SignUpAvailabilityFragment.days.remove("Saturday");
                }
                break;
        }
    }
}