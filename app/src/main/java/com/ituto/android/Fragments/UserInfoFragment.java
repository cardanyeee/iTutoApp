package com.ituto.android.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ituto.android.AuthActivity;
import com.ituto.android.Constant;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Course;
import com.ituto.android.R;
import com.ituto.android.TutorSignUpActivity;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserInfoFragment extends Fragment {

    private View view;

    private SharedPreferences sharedPreferences;

    private TextInputLayout layoutFirstName, layoutLastName, layoutBirthdate, layoutGender, layoutCourse;
    private TextInputEditText txtFirstName, txtLastName, txtBirthdate;
    private AutoCompleteTextView txtGender;
    private AutoCompleteTextView txtCourse;
    private Button btnSignUp;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Boolean isTutor;
    private String courseID;

    private String email, password, confirmPassword;

    private ArrayList<Course> courseArrayList;
    private ArrayList<String> stringCourseArrayList;

    private ProgressDialog dialog;

    private static final String[] GENDERS = new String[]{
            "Male", "Female", "Prefer not to say"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_info, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        isTutor = getArguments().getBoolean("isTutor");

        email = getArguments().getString("email");
        password = getArguments().getString("password");
        confirmPassword = getArguments().getString("password_confirmation");

        layoutFirstName = view.findViewById(R.id.txtLayoutFirstNameSignUp);
        layoutLastName = view.findViewById(R.id.txtLayoutLastNameSignUp);
        layoutBirthdate = view.findViewById(R.id.txtLayoutBirthdateSignUp);
        layoutGender = view.findViewById(R.id.txtLayoutGenderSignUp);
        layoutCourse = view.findViewById(R.id.txtLayoutCourseSignUp);

        txtFirstName = view.findViewById(R.id.txtFirstNameSignUp);
        txtLastName = view.findViewById(R.id.txtLastNameSignUp);
        txtBirthdate = view.findViewById(R.id.txtBirthdateSignUp);
        txtGender = view.findViewById(R.id.txtGenderSignUp);
        txtCourse = view.findViewById(R.id.txtCourseSignUp);

        btnSignUp = view.findViewById(R.id.btnSignUp);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        getCourses();

        if (isTutor) {
            btnSignUp.setText("Continue");
        } else {
            btnSignUp.setText("Register");
        }


        txtBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        monthOfYear = monthOfYear + 1;
                        String date = year1 + "-" + monthOfYear + "-" + dayOfMonth;
                        txtBirthdate.setText(date);
                    },
                    year,
                    month,
                    day);
            datePickerDialog.setAccentColor("#477B72");
            datePickerDialog.setMaxDate(Calendar.getInstance());
            datePickerDialog.show(getParentFragmentManager(), "");
        });

        dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = year + "-" + month + "-" + dayOfMonth;
            txtBirthdate.setText(date);
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_dropdown,
                R.id.txtDropdownItem,
                GENDERS
        );

        txtGender.setAdapter(arrayAdapter);


        txtCourse.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            courseID = courseArrayList.get(stringCourseArrayList.indexOf(selected)).getId();
        });

        txtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtFirstName.getText().toString().isEmpty()) {
                    layoutFirstName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtLastName.getText().toString().isEmpty()) {
                    layoutLastName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSignUp.setOnClickListener(v -> {
            if (validate()) {
                register();
            }
        });

    }

    private void getCourses() {
        stringCourseArrayList = new ArrayList<>();
        courseArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.COURSES, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    JSONArray coursesArray = new JSONArray(object.getString("courses"));

                    for (int i = 0; i < coursesArray.length(); i++) {
                        JSONObject courseObject = coursesArray.getJSONObject(i);

                        if (courseObject.getBoolean("active")) {
                            Course course = new Course();

                            course.setId(courseObject.getString("_id"));
                            course.setCode(courseObject.getString("code"));
                            course.setName(courseObject.getString("name"));

                            stringCourseArrayList.add(courseObject.getString("name"));
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getContext(),
                                    R.layout.item_dropdown,
                                    R.id.txtDropdownItem,
                                    stringCourseArrayList
                            );

                            courseArrayList.add(course);
                            txtCourse.setAdapter(arrayAdapter);
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
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

    private void register() {
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.slide_in,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out  // popExit
                    ).replace(R.id.frameAuthContainer, new SignInFragment()).addToBackStack(null).commit();
                    StyleableToast.makeText(getContext(), object.getString("msg"), R.style.CustomToast).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "Register Unsuccessful", R.style.CustomToast).show();
            }
            dialog.dismiss();

        }, error -> {
            StyleableToast.makeText(getContext(), "Register Unsuccessful", R.style.CustomToast).show();
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("firstname", txtFirstName.getText().toString().trim());
                map.put("lastname", txtLastName.getText().toString().trim());
                map.put("birthdate", txtBirthdate.getText().toString().trim());
                map.put("gender", txtGender.getText().toString().trim());
                map.put("course", courseID);
                map.put("email", email);
                map.put("password", password);
                map.put("password_confirmation", confirmPassword);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private boolean validate() {

        if (txtFirstName.getText().toString().isEmpty()) {
            layoutFirstName.setErrorEnabled(true);
            layoutFirstName.setError("Enter a valid firstname");
            return false;
        }

        if (txtLastName.getText().toString().isEmpty()) {
            layoutLastName.setErrorEnabled(true);
            layoutLastName.setError("Enter a valid lastname");
            return false;
        }

        return true;
    }

    private void redirectAuthentication() {
        if (isTutor) {
            startActivity(new Intent(((AuthActivity) getContext()), TutorSignUpActivity.class));
        } else {
            startActivity(new Intent(((AuthActivity) getContext()), HomeActivity.class));
        }
        ((AuthActivity) getContext()).finish();
    }
}