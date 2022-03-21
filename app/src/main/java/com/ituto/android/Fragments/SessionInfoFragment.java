package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ituto.android.Constant;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SessionInfoFragment extends Fragment {
    
    private View view;
    private ImageView imgBackButton;
    private MaterialCardView crdTutee, crdDescription;
    private CircleImageView imgTutee;
    private TextView txtSubjectName, txtName, txtCourse, txtYearLevel, txtDescription;
    private RecyclerView recyclerAssessments;
    private FloatingActionButton btnAddAssessment;
    private Dialog dialog;

    private SharedPreferences sharedPreferences;
    private String loggedInAs;

    private String sessionID, tuteeID, tutorID, subjectID;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_info, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        sessionID = getArguments().getString("_id");

        imgTutee = view.findViewById(R.id.imgTutee);
        txtName = view.findViewById(R.id.txtName);
        txtCourse = view.findViewById(R.id.txtCourse);
        txtDescription = view.findViewById(R.id.txtDescription);
        btnAddAssessment = view.findViewById(R.id.btnAddAssessment);
        imgBackButton = view.findViewById(R.id.imgBackButton);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.setCancelable(false);
        dialog.show();

        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");

        imgBackButton.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());

        if (!loggedInAs.equals("TUTOR")) {
            btnAddAssessment.setVisibility(View.GONE);
        }

        getSession();

        btnAddAssessment.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            CreateAssessmentFragment createAssessmentFragment = new CreateAssessmentFragment();
            bundle.putString("sessionID", sessionID);
            createAssessmentFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.fragment_container, createAssessmentFragment).addToBackStack(null).commit();
        });

    }

    private void getSession() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_SESSION + "/" + sessionID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONObject sessionObject = object.getJSONObject("session");
                    JSONObject tutorObject = sessionObject.getJSONObject("tutor");
                    JSONObject tuteeObject = sessionObject.getJSONObject("tutee");
                    JSONObject courseObject = tuteeObject.getJSONObject("course");
                    JSONObject avatarObject = tuteeObject.getJSONObject("avatar");
                    JSONObject subjectObject = sessionObject.getJSONObject("subject");
                    JSONObject timeObject = sessionObject.getJSONObject("time");

                    JSONObject availabilityObject = object.getJSONObject("availability");
                    JSONArray days = availabilityObject.getJSONArray("days");
                    JSONArray time = availabilityObject.getJSONArray("time");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = format.parse(sessionObject.getString("startDate"));
                    String outputPattern = "yyyy-MM-dd";
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    Picasso.get().load(avatarObject.getString("url")).resize(500, 0).into(imgTutee);
                    txtName.setText(tuteeObject.getString("firstname") + " " + tuteeObject.getString("lastname"));
                    txtCourse.setText(courseObject.getString("name"));
                    txtDescription.setText(sessionObject.getString("description"));
                }

                dialog.dismiss();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
//                btnConfirmSchedule.setEnabled(false);
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
//            btnConfirmSchedule.setEnabled(false);
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