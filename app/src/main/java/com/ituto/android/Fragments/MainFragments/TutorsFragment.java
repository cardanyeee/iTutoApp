package com.ituto.android.Fragments.MainFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.ituto.android.Adapters.TutorsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.FilterFragment;
import com.ituto.android.Fragments.TutorProfileFragment;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Availability;
import com.ituto.android.Models.Time;
import com.ituto.android.Models.Tutor;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorsFragment extends Fragment implements TutorsAdapter.OnItemListener {

    private View view;
    private String TUTORS;
    private String KEYWORD_LINK = "?keyword=";
    private String SUBJECTS_LINK = "&subjects=";
    private String TIME_LINK = "&time=";
    private String DAY_LINK = "&day=";
    private String KEYWORD = "";
    private String SUBJECTS = "";
    private String DAY = "";
    private String TIME = "";

    private LinearLayout llyPlaceholder;
    public static EditText searchTutor;
    private ImageView btnFilters;
    public static SwipeRefreshLayout swipeTutor;
    public static RecyclerView recyclerTutor;
    private ArrayList<Tutor> tutorArrayList;

    private SharedPreferences sharedPreferences;
    private TutorsAdapter tutorsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutors, container, false);
        init();
        return view;
    }

    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.VISIBLE);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        searchTutor = view.findViewById(R.id.searchTutor);
        btnFilters = view.findViewById(R.id.btnFilters);
        recyclerTutor = view.findViewById(R.id.recyclerTutor);
        recyclerTutor.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeTutor = view.findViewById(R.id.swipeTutor);
        llyPlaceholder = view.findViewById(R.id.llyPlaceholder);

        if (HomeActivity.clicked) {
            searchTutor.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        HomeActivity.clicked = false;
        getTutors();

        swipeTutor.setOnRefreshListener(() -> getTutors());

        searchTutor.setOnEditorActionListener((v, actionId, event) -> {
            KEYWORD = searchTutor.getText().toString();
//            StyleableToast.makeText(getContext(), TUTORS, R.style.CustomToast).show();
            getTutors();
            return true;
        });

        btnFilters.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            FilterFragment filterFragment = new FilterFragment();
            bundle.putBoolean("filter", true);
            filterFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.fragment_container, filterFragment).addToBackStack(null).commit();
        });
    }

    private void getTutors() {
        TUTORS = "";
        tutorArrayList = new ArrayList<>();
        swipeTutor.setRefreshing(true);

        if (!(getArguments() == null)) {
            if (getArguments().getBoolean("filter")) {
                SUBJECTS = getArguments().getString("subjects");
                DAY = getArguments().getString("day");
                TIME = getArguments().getString("time");
            }
        }

        TUTORS = Constant.TUTORS + KEYWORD_LINK + KEYWORD + SUBJECTS_LINK + SUBJECTS + DAY_LINK + DAY + TIME_LINK + TIME;
        StringRequest request = new StringRequest(Request.Method.GET, TUTORS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray resultArray = new JSONArray(object.getString("tutors"));

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject tutorObject = resultArray.getJSONObject(i);
                        JSONObject userObject = tutorObject.getJSONObject("userID");
                        JSONObject courseObject = userObject.getJSONObject("course");
                        JSONObject avatar = userObject.getJSONObject("avatar");
                        JSONObject availabilityObject = tutorObject.getJSONObject("availability");
                        JSONArray days = availabilityObject.getJSONArray("days");
                        JSONArray timeArray = availabilityObject.getJSONArray("time");
                        JSONArray subjectsArray = tutorObject.getJSONArray("subjects");

                        Tutor tutor = new Tutor();
                        tutor.setTutorID(tutorObject.getString("_id"));
                        tutor.setUserID(userObject.getString("_id"));
                        tutor.setFirstname(userObject.getString("firstname"));
                        tutor.setLastname(userObject.getString("lastname"));
                        tutor.setYearLevel(userObject.getString("yearLevel"));
                        tutor.setCourse(courseObject.getString("name"));
                        tutor.setAvatar(avatar.getString("url"));

                        ArrayList<String> daysArray = new ArrayList<>();
                        ArrayList<Time> timeArrayList = new ArrayList<>();
                        ArrayList<String> subjectArrayList = new ArrayList<>();

                        for (int d = 0; d < days.length(); d++) {
                            daysArray.add(days.getString(d));
                        }

                        for (int t = 0; t < timeArray.length(); t++) {
                            Time time = new Time();
                            JSONObject timeObject = timeArray.getJSONObject(t);

                            time.setTimeOfDay(timeObject.getString("timeOfDay"));
                            time.setMin(timeObject.getString("min"));
                            time.setMax(timeObject.getString("max"));

                            timeArrayList.add(time);
                        }

                        for (int s = 0; s < subjectsArray.length(); s++) {
                            JSONObject subject = subjectsArray.getJSONObject(s);
                            subjectArrayList.add(subject.getString("name"));
                        }

                        tutor.setDaysArrayList(daysArray);
                        tutor.setTimeArrayList(timeArrayList);
                        tutor.setSubjects(subjectArrayList);

                        tutorArrayList.add(tutor);
                    }

                    if (tutorArrayList.isEmpty()) {
                        recyclerTutor.setVisibility(View.GONE);
                        llyPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        recyclerTutor.setVisibility(View.VISIBLE);
                        llyPlaceholder.setVisibility(View.GONE);
                    }

                    tutorsAdapter = new TutorsAdapter(getContext(), tutorArrayList, this);
                    recyclerTutor.setAdapter(tutorsAdapter);
                }

                swipeTutor.setRefreshing(false);

            } catch (JSONException e) {
                e.printStackTrace();
                swipeTutor.setRefreshing(false);
            }
            swipeTutor.setRefreshing(false);
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

    @Override
    public void onItemClick(int position) {
        try {
            Bundle bundle = new Bundle();
            TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
            Tutor tutor = tutorArrayList.get(position);
            bundle.putString("_id", tutor.getTutorID());
            tutorProfileFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.fragment_container, tutorProfileFragment).addToBackStack(null).commit();
        } catch (IndexOutOfBoundsException e) {
            StyleableToast.makeText(getContext(), "Tutors are still loading. Please wait and try again.", R.style.CustomToast).show();
        }
    }
}