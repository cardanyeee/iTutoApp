package com.ituto.android.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.example.movieapp.AuthActivity;
//import com.example.movieapp.Constant;
//import com.example.movieapp.HomeActivity;
import com.ituto.android.AuthActivity;
import com.ituto.android.Constant;
import com.ituto.android.R;
//import com.example.movieapp.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ituto.android.UserInfoActivity;
import com.muddzdev.styleabletoast.StyleableToast;
//import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    private View view;
    private TextInputLayout layoutFirstName, layoutLastName, layoutEmail, layoutPassword, layoutConfirm, layoutBirthdate;
    private TextInputEditText txtFirstName, txtLastName, txtEmail, txtPassword, txtConfirm, txtBithdate;
    private TextView txtSignIn;
    private Button btnSignUp;
    private ProgressDialog dialog;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private int isTutor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_up, container, false);
        init();
        return view;
    }

    private void init() {
        isTutor = getArguments().getInt("isTutor");
        StyleableToast.makeText(getContext(), String.valueOf(isTutor), R.style.CustomToast).show();

        layoutFirstName = view.findViewById(R.id.txtLayoutFirstNameSignUp);
        layoutLastName = view.findViewById(R.id.txtLayoutLastNameSignUp);
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignUp);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignUp);
        layoutConfirm = view.findViewById(R.id.txtLayoutConfirmSignUp);
        layoutBirthdate = view.findViewById(R.id.txtLayoutBirthdateSignUp);

        txtFirstName = view.findViewById(R.id.txtFirstNameSignUp);
        txtLastName = view.findViewById(R.id.txtLastNameSignUp);
        txtPassword = view.findViewById(R.id.txtPasswordSignUp);
        txtConfirm = view.findViewById(R.id.txtConfirmSignUp);
        txtEmail = view.findViewById(R.id.txtEmailSignUp);
        txtBithdate = view.findViewById(R.id.txtBirthdateSignUp);

        txtSignIn = view.findViewById(R.id.txtSignIn);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtBithdate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    R.style.DatePicker,
                    dateSetListener,
                    year, month, day);
            dialog.show();
        });

        dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = year + "-" + month + "-" + dayOfMonth;
            txtBithdate.setText(date);
        };

        txtSignIn.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.frameAuthContainer, new SignInFragment()).addToBackStack(null).commit();
        });

        btnSignUp.setOnClickListener(v -> {
            if (validate()) {
                register();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtEmail.getText().toString().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtPassword.getText().toString().length() > 7) {
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtConfirm.getText().toString().equals(txtPassword.getText().toString())) {
                    layoutConfirm.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {

        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Enter a valid e-mail");
            return false;
        }

        if (txtPassword.getText().toString().length() < 8) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Required at least 8 characters");
            return false;
        }

        if (!txtConfirm.getText().toString().equals(txtPassword.getText().toString())) {
            layoutConfirm.setErrorEnabled(true);
            layoutConfirm.setError("Password does not match");
            return false;
        }

        return true;
    }

    private void register() {
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("access_token", object.getString("access_token"));
                    editor.putString("name", user.getString("name"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent(((AuthActivity) getContext()), UserInfoActivity.class));
                    ((AuthActivity) getContext()).finish();
//                    StyleableToast.makeText(getContext(), "Register Successful", R.style.CustomToast).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                StyleableToast.makeText(getContext(), "Register Unsuccessful", R.style.CustomToast).show();
            }
            dialog.dismiss();

        }, error -> {
//            StyleableToast.makeText(getContext(), "Register Unsuccessful", R.style.CustomToast).show();
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txtEmail.getText().toString().trim());
                map.put("password", txtPassword.getText().toString());
                map.put("password_confirmation", txtConfirm.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
