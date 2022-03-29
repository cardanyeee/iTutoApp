package com.ituto.android.Fragments;

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
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ituto.android.Adapters.SessionsAdapter;
import com.ituto.android.Adapters.TutorsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.SessionInfoFragment;
import com.ituto.android.Fragments.TutorProfileFragment;
import com.ituto.android.Models.Session;
import com.ituto.android.Models.Subject;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionsFragment extends Fragment implements SessionsAdapter.OnItemListener {

    private View view;
    private SharedPreferences sharedPreferences;
    private ArrayList<Session> sessionArrayList;
    private SessionsAdapter sessionsAdapter;
    private SwipeRefreshLayout swipeSession;
    private RecyclerView recyclerSession;
    private LinearLayout llyPlaceholder;

    private FloatingActionButton btnAddSession;

    private String loggedInAs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sessions, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");

        swipeSession = view.findViewById(R.id.swipeSession);
        recyclerSession = view.findViewById(R.id.recyclerSession);
        llyPlaceholder = view.findViewById(R.id.llyPlaceholder);
        recyclerSession.setLayoutManager(new LinearLayoutManager(getContext()));

//        btnAddSession = view.findViewById(R.id.btnAddSession);

        swipeSession.setOnRefreshListener(() -> getSessions());

        getSessions();
    }

    private void getSessions() {
        sessionArrayList = new ArrayList<>();

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
                        recyclerSession.setVisibility(View.GONE);
                        llyPlaceholder.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSession.setVisibility(View.VISIBLE);
                        llyPlaceholder.setVisibility(View.GONE);
                    }

                    sessionsAdapter = new SessionsAdapter(getContext(), sessionArrayList, this);
                    recyclerSession.setAdapter(sessionsAdapter);
                }
                swipeSession.setRefreshing(false);

            } catch (JSONException e) {
                e.printStackTrace();
                swipeSession.setRefreshing(false);
            }
            swipeSession.setRefreshing(false);
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
        Bundle bundle = new Bundle();
        SessionInfoFragment sessionInfoFragment = new SessionInfoFragment();
        Session session = sessionArrayList.get(position);
        bundle.putString("_id", session.getSessionID());
        sessionInfoFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        ).replace(R.id.fragment_container, sessionInfoFragment).addToBackStack(null).commit();
    }
}