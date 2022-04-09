package com.ituto.android.Fragments.MainFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ituto.android.Adapters.RecentSessionsAdapter;
import com.ituto.android.Adapters.SessionsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Course;
import com.ituto.android.Models.Session;
import com.ituto.android.Models.Subject;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class HomeFragment extends Fragment {
    private static final String TAG = "MovieFragment";
    private View view;
    public static RecyclerView recyclerView;
    private ArrayList<Session> sessionArrayList;
    public static ArrayList<Course> arrayList;

    private CircleImageView imgUserProfile;
    //    private CalendarView userSchedule;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private static final int GALLERY_ADD_POST = 2;
    private Dialog dialog;
    private TextView txtFirstname, txtLoggedInAs;
    private String loggedInAs;
    private RecentSessionsAdapter recentSessionsAdapter;
    //    private List<EventDay> events;
    private SwipeRefreshLayout swipeRecentSessions;
    private RecyclerView recyclerSessions;
    private EditText txtHomeSearch;
    private BottomNavigationView bottomNavigation;
    private LinearLayout llyPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.VISIBLE);
        bottomNavigation = getActivity().findViewById(R.id.bottomNavigationView);
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");
        ((HomeActivity) getContext()).setSupportActionBar(toolbar);

        swipeRecentSessions = view.findViewById(R.id.swipeRecentSessions);
        recyclerSessions = view.findViewById(R.id.recyclerSessions);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        llyPlaceholder = view.findViewById(R.id.llyPlaceholder);

        txtFirstname = view.findViewById(R.id.txtFirstname);
        txtLoggedInAs = view.findViewById(R.id.txtLoggedInAs);
        imgUserProfile = view.findViewById(R.id.imgUserProfile);
//        userSchedule = view.findViewById(R.id.userSchedule);
        txtHomeSearch = view.findViewById(R.id.txtHomeSearch);

        Glide.with(getContext()).load(sharedPreferences.getString("avatar", "")).into(imgUserProfile);
        txtFirstname.setText(sharedPreferences.getString("firstname", ""));
        txtLoggedInAs.setText(sharedPreferences.getString("loggedInAs", "").substring(0, 1).toUpperCase() + sharedPreferences.getString("loggedInAs", "").substring(1).toLowerCase());
        getSessions();

        getUser();

        swipeRecentSessions.setOnRefreshListener(() -> getSessions());

        txtHomeSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                HomeActivity.clicked = true;
                bottomNavigation.setSelectedItemId(R.id.item_tutors);
                return true;
            }
        });

        if (loggedInAs.equals("TUTOR")) {
            getCurrentTutor();
        }
    }

    private void getUser() {

        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar");
                    JSONObject course = user.getJSONObject("course");

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

        Log.d("TAG", String.valueOf(request.getBodyContentType()));
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void getSessions() {
        sessionArrayList = new ArrayList<>();
        swipeRecentSessions.setRefreshing(true);
//        events = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, loggedInAs.equals("TUTOR") ? Constant.ALL_TUTOR_SESSIONS : Constant.ALL_TUTEE_SESSIONS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray resultArray = new JSONArray(object.getString("sessions"));
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject sessionObject = resultArray.getJSONObject(i);
                        JSONObject timeObject = sessionObject.getJSONObject("time");
                        JSONObject subjectObject = sessionObject.getJSONObject("subject");
                        Session session = new Session();
                        Tutor tutor = new Tutor();
                        User user = new User();
                        Subject subject = new Subject();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        String outputPattern = "dd MMMM, yyyy";
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        Date displayDate;

                        session.setStatus(sessionObject.getString("status"));

                        if (session.getStatus().equals("Request")) {
                            session.setStatus(loggedInAs.equals("TUTOR") ? sessionObject.getString("status") : "Pending");
                            displayDate = format.parse(sessionObject.getString("requestDate"));
                            session.setDisplayDate(outputFormat.format(displayDate));
                        }

                        if (session.getStatus().equals("Ongoing") || session.getStatus().equals("Declined") || session.getStatus().equals("Cancelled")) {
                            displayDate = format.parse(sessionObject.getString("acceptDeclineDate"));
                            session.setDisplayDate(outputFormat.format(displayDate));
                        }

                        if (session.getStatus().equals("Done")) {
                            displayDate = format.parse(sessionObject.getString("endDate"));
                            session.setDisplayDate(outputFormat.format(displayDate));
                        }

                        session.setSessionID(sessionObject.getString("_id"));
                        session.setStartDate(sessionObject.getString("startDate"));
                        session.setTimeOfDay(timeObject.getString("timeOfDay"));
                        session.setMinTime(timeObject.getString("min"));
                        session.setMaxTime(timeObject.getString("max"));
                        subject.setName(subjectObject.getString("name"));

                        session.setSubject(subject);

                        JSONObject tuteeObject = sessionObject.getJSONObject("tutee");
                        JSONObject avatarObject = tuteeObject.getJSONObject("avatar");

                        tutor.setUserID(sessionObject.getString("tutor"));

                        user.setUserID(tuteeObject.getString("_id"));
                        user.setFirstname(tuteeObject.getString("firstname"));
                        user.setLastname(tuteeObject.getString("lastname"));
                        user.setAvatar(avatarObject.getString("url"));


                        JSONObject tutorObject = sessionObject.getJSONObject("tutor");
                        JSONObject avatarObjectTutor = tutorObject.getJSONObject("avatar");

                        user.setUserID(sessionObject.getString("tutee"));

                        tutor.setUserID(tutorObject.getString("_id"));
                        tutor.setFirstname(tutorObject.getString("firstname"));
                        tutor.setLastname(tutorObject.getString("lastname"));
                        tutor.setAvatar(avatarObjectTutor.getString("url"));

                        session.setTutor(tutor);
                        session.setTutee(user);
                        sessionArrayList.add(session);
                    }

                    if (sessionArrayList.isEmpty()) {
                        recyclerSessions.setVisibility(View.GONE);
                        llyPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSessions.setVisibility(View.VISIBLE);
                        llyPlaceholder.setVisibility(View.GONE);
                    }

                    recentSessionsAdapter = new RecentSessionsAdapter(getContext(), sessionArrayList);
                    recyclerSessions.setAdapter(recentSessionsAdapter);
                }
                swipeRecentSessions.setRefreshing(false);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }, error -> {
            swipeRecentSessions.setRefreshing(false);
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

    private void getCurrentTutor() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.CURRENT_TUTOR, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject tutorObject = object.getJSONObject("tutor");
                    if (!tutorObject.has("aboutMe")) {

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
}
