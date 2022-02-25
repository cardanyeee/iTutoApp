package com.ituto.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    private ChipGroup chpGrpSubjects;
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
        txtCourse = findViewById(R.id.txtCourseSignUp);
        txtSubject = findViewById(R.id.txtSubjectSignUp);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        chpGrpSubjects = findViewById(R.id.chpGrpSubjects);
        btnSignUpTutor = findViewById(R.id.btnSignUpTutor);

        getCourses();

        txtCourse.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            courseID = courseArrayList.get(stringCourseArrayList.indexOf(selected)).getId();
            SUBJECT_COURSES = Constant.SUBJECT_COURSES + "/" + courseID;
            getSubjects();
        });

        txtSubject.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            subjectID = subjectArrayList.get(stringSubjectArrayList.indexOf(selected)).getId();
            StyleableToast.makeText(getApplicationContext(), String.valueOf(subjectID), R.style.CustomToast).show();
        });

        btnAddSubject.setOnClickListener(v -> {
            Chip chip = new Chip(TutorSubjectsActivity.this);
            ChipDrawable drawable = ChipDrawable.createFromAttributes(TutorSubjectsActivity.this, null, 0, R.style.Widget_MaterialComponents_Chip_Entry);
            chip.setChipDrawable(drawable);

            chip.setCheckable(false);
            chip.setClickable(false);
            chip.setText(txtSubject.getText().toString());
            chip.setOnCloseIconClickListener(chipView -> chpGrpSubjects.removeView(chip));

            chpGrpSubjects.addView(chip);
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
}