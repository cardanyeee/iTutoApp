package com.ituto.android.Fragments.MainFragments;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class HomeFragment extends Fragment {
    private static final String TAG = "MovieFragment";
    private View view;
    public static RecyclerView recyclerView;
    private ArrayList<Session> sessionArrayList;
    public static ArrayList<Course> arrayList;

    private CircleImageView imgUserProfile;
    private CalendarView userSchedule;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private static final int GALLERY_ADD_POST = 2;
    private Dialog dialog;
    private TextView txtFirstname, txtLoggedInAs;
    private String loggedInAs;
    private List<EventDay> events;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.VISIBLE);
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);

        txtFirstname = view.findViewById(R.id.txtFirstname);
        txtLoggedInAs = view.findViewById(R.id.txtLoggedInAs);
        imgUserProfile = view.findViewById(R.id.imgUserProfile);
        userSchedule = view.findViewById(R.id.userSchedule);

        Glide.with(getContext()).load(sharedPreferences.getString("avatar", "")).into(imgUserProfile);
        txtFirstname.setText(sharedPreferences.getString("firstname", ""));
        txtLoggedInAs.setText(sharedPreferences.getString("loggedInAs", "").substring(0, 1).toUpperCase() + sharedPreferences.getString("loggedInAs", "").substring(1).toLowerCase());
        getSessions();

        getUser();
        getCurrentTutor();

    }

    private void getUser() {

        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar");
                    JSONObject course = user.getJSONObject("course");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer "+token);
                return map;
            }
        };

        Log.d("TAG", String.valueOf(request.getBodyContentType()));
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void getSessions() {
        sessionArrayList = new ArrayList<>();
        events = new ArrayList<>();

        String sessionsLink = loggedInAs.equals("TUTOR") ? Constant.TUTOR_SESSIONS : Constant.TUTEE_SESSIONS;

        StringRequest request = new StringRequest(Request.Method.GET, sessionsLink + "?status=Ongoing", response -> {
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
                        Date date = format.parse(sessionObject.getString("startDate"));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        events.add(new EventDay(cal, R.drawable.ic_baseline_assignment_24, Color.parseColor("#4FBD95")));
                    }
                    userSchedule.setEvents(events);
                }

            } catch (JSONException | ParseException e) {
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
