package com.ituto.android.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.MainFragments.TutorsFragment;
import com.ituto.android.Models.Course;
import com.ituto.android.Models.Subject;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FilterFragment extends Fragment {

    private View view;
    private Button btnApplyFilters;

    private TextInputLayout txtLayoutCourse, txtLayoutSubject, txtLayoutAvailability;
    private TextInputEditText txtAvailability;
    private AutoCompleteTextView txtCourse, txtSubject;

    private ArrayList<Course> courseArrayList;
    private ArrayList<Subject> subjectArrayList;

    private ArrayList<String> stringCourseArrayList;
    private ArrayList<String> stringSubjectArrayList;

    private String SUBJECT_COURSES;

    private String courseID;
    private String subjectID;
    private String dayOfWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter, container, false);
        init();
        return view;
    }

    private void init() {
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);

        txtCourse = view.findViewById(R.id.txtCourse);
        txtSubject = view.findViewById(R.id.txtSubject);
        txtAvailability = view.findViewById(R.id.txtAvailability);

        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);

        txtAvailability.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        monthOfYear = monthOfYear + 1;
                        String date = year1 + "-" + monthOfYear + "-" + dayOfMonth;
                        txtAvailability.setText(date);
                        try {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            c.setTime(sdf.parse(date));// all done
                            int d = c.get(Calendar.DAY_OF_WEEK);

                            switch (d) {
                                case Calendar.SUNDAY:
                                    dayOfWeek = "Sunday";
                                    break;
                                case Calendar.MONDAY:
                                    dayOfWeek = "Monday";
                                    break;
                                case Calendar.TUESDAY:
                                    dayOfWeek = "Tuesday";
                                    break;
                                case Calendar.WEDNESDAY:
                                    dayOfWeek = "Wednesday";
                                    break;
                                case Calendar.THURSDAY:
                                    dayOfWeek = "Thursday";
                                    break;
                                case Calendar.FRIDAY:
                                    dayOfWeek = "Friday";
                                    break;
                                case Calendar.SATURDAY:
                                    dayOfWeek = "Saturday";
                                    break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    },
                    year,
                    month,
                    day);
            datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimaryLight));
            datePickerDialog.setMinDate(Calendar.getInstance());
            datePickerDialog.show(getParentFragmentManager(), "");
        });

        txtCourse.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            courseID = courseArrayList.get(stringCourseArrayList.indexOf(selected)).getId();
            SUBJECT_COURSES = Constant.SUBJECT_COURSES + "/" + courseID;
            getSubjects();
        });


        txtSubject.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            subjectID = subjectArrayList.get(stringSubjectArrayList.indexOf(selected)).getId();
            StyleableToast.makeText(getContext(), String.valueOf(subjectID), R.style.CustomToast).show();
        });

        getCourses();

        btnApplyFilters.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            TutorsFragment tutorsFragment = new TutorsFragment();
            bundle.putBoolean("filter", true);
            bundle.putString("subjects", subjectID == null ? "" : subjectID);
            bundle.putString("day", dayOfWeek == null ? "" : dayOfWeek);
            // R.id.container - the id of a view that will hold your fragment; usually a FrameLayout
            tutorsFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.fragment_container, tutorsFragment).addToBackStack(null).commit();
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

        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                                    getContext(),
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}