package com.ituto.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.ituto.android.Fragments.MainAuthFragment;
import com.ituto.android.Fragments.SignInFragment;
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
}