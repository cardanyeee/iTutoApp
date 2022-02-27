package com.ituto.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.ituto.android.Models.Course;
import com.ituto.android.Models.Subject;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorSubjectsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextInputLayout layoutCourse, layoutSubject;
    private AutoCompleteTextView txtCourse, txtSubject;
    private ImageButton btnAddSubject;
    private LinearLayout chpGrpSubjects;
    private Button btnSignUpTutor;

    private ArrayList<Course> courseArrayList;
    private ArrayList<Subject> subjectArrayList;

    private ArrayList<String> stringCourseArrayList;
    private ArrayList<String> stringSubjectArrayList;

    private ArrayList<String> tutorSubjectsArrayList;

    private String SUBJECT_COURSES;

    private String courseID;
    private String subjectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_subjects);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        txtCourse = findViewById(R.id.txtCourseSignUp);
        txtSubject = findViewById(R.id.txtSubjectSignUp);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        chpGrpSubjects = findViewById(R.id.chpGrpSubjects);
        btnSignUpTutor = findViewById(R.id.btnSignUpTutor);
        tutorSubjectsArrayList = new ArrayList<>();

        getCourses();

        txtCourse.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            courseID = courseArrayList.get(stringCourseArrayList.indexOf(selected)).getId();
            SUBJECT_COURSES = Constant.SUBJECT_COURSES + "/" + courseID;
            getSubjects();
            txtSubject.setEnabled(true);
        });

        txtSubject.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            subjectID = subjectArrayList.get(stringSubjectArrayList.indexOf(selected)).getId();
            StyleableToast.makeText(getApplicationContext(), String.valueOf(subjectID), R.style.CustomToast).show();
        });

        btnAddSubject.setOnClickListener(v -> {
            if (courseID == null || txtCourse.getText().toString().trim() == null) {
                StyleableToast.makeText(getApplicationContext(), "Please select a course first.", R.style.CustomToast).show();
            } else if (subjectID == null || txtSubject.getText().toString().trim() == null) {
                StyleableToast.makeText(getApplicationContext(), "Please select a subject first.", R.style.CustomToast).show();
            } else {
                if (!tutorSubjectsArrayList.contains(subjectID)) {
                    Chip chip = new Chip(TutorSubjectsActivity.this);
                    ChipDrawable drawable = ChipDrawable.createFromAttributes(TutorSubjectsActivity.this, null, 0, R.style.SubjectChip);
                    chip.setChipDrawable(drawable);

                    chip.setCheckable(false);
                    chip.setClickable(false);
                    chip.setText(txtSubject.getText().toString());
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chpGrpSubjects.removeView(chip);
                            tutorSubjectsArrayList.remove(subjectID);
                        }
                    });
//            chip.setChipCornerRadius(3);

                    tutorSubjectsArrayList.add(subjectID);

                    chpGrpSubjects.addView(chip);
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Please select another subject.", R.style.CustomToast).show();
                }

            }

        });

        btnSignUpTutor.setOnClickListener(v -> {
            addTutorSubjects();
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
                                    getApplicationContext(),
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

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void getSubjects() {
        stringSubjectArrayList = new ArrayList<>();
        subjectArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, SUBJECT_COURSES, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    JSONArray subjectsArray = new JSONArray(object.getString("subjects"));

                    for (int i = 0; i < subjectsArray.length(); i++) {
                        JSONObject subjectObject = subjectsArray.getJSONObject(i);

                        if (subjectObject.getBoolean("active")) {
                            Subject subject = new Subject();

                            subject.setId(subjectObject.getString("_id"));
                            subject.setCode(subjectObject.getString("code"));
                            subject.setName(subjectObject.getString("name"));

                            stringSubjectArrayList.add(subjectObject.getString("name"));
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getApplicationContext(),
                                    R.layout.item_dropdown,
                                    R.id.txtDropdownItem,
                                    stringSubjectArrayList
                            );
                            subjectArrayList.add(subject);
                            txtSubject.setAdapter(arrayAdapter);
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void addTutorSubjects() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_TUTOR_SUBJECTS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    startActivity(new Intent(TutorSubjectsActivity.this, HomeActivity.class));
                    finish();
                    StyleableToast.makeText(getApplicationContext(), "Successful", R.style.CustomToast).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getApplicationContext(), "Unsuccessful", R.style.CustomToast).show();
            }

        }, error -> {
            StyleableToast.makeText(getApplicationContext(), "Unsuccessful", R.style.CustomToast).show();
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
                JSONArray jsArray = new JSONArray(tutorSubjectsArrayList);
                map.put("subjectID", jsArray.toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}