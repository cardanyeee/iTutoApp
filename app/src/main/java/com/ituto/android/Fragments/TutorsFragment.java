package com.ituto.android.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Adapters.ContactsAdapter;
import com.ituto.android.Adapters.TutorsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Conversation;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorsFragment extends Fragment {

    private View view;
    private EditText searchTutor;
    private String TUTORS = Constant.TUTORS;

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
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        searchTutor = view.findViewById(R.id.searchTutor);
        recyclerTutor = view.findViewById(R.id.recyclerTutor);
        recyclerTutor.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeTutor = view.findViewById(R.id.swipeTutor);

        getTutors();

        swipeTutor.setOnRefreshListener(() -> getTutors());

        searchTutor.setOnEditorActionListener((v, actionId, event) -> {
            TUTORS = "";
            TUTORS = Constant.TUTORS + "?keyword=" + searchTutor.getText().toString();
            StyleableToast.makeText(getContext(), TUTORS, R.style.CustomToast).show();
            getTutors();
            return true;
        });
    }

    private void getTutors() {
        tutorArrayList = new ArrayList<>();
        swipeTutor.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, TUTORS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray resultArray = new JSONArray(object.getString("tutors"));

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject tutorObject = resultArray.getJSONObject(i);
                        JSONObject avatar = tutorObject.getJSONObject("avatar");

                        Tutor tutor = new Tutor();
                        tutor.setFirstname(tutorObject.getString("firstname"));
                        tutor.setLastname(tutorObject.getString("lastname"));
                        tutor.setAvatar(avatar.getString("url"));

                        tutorArrayList.add(tutor);
                    }

                    tutorsAdapter = new TutorsAdapter(getContext(), tutorArrayList);
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
}