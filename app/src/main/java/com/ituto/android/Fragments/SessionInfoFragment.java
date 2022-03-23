package com.ituto.android.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ituto.android.Adapters.AssessmentsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Assessment;
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

public class SessionInfoFragment extends Fragment implements AssessmentsAdapter.OnItemListener{
    
    private View view;
    private ImageView imgBackButton;
    private MaterialCardView crdTutee, crdDescription;
    private CircleImageView imgTutee;
    private TextView txtSubjectName, txtName, txtCourse, txtYearLevel, txtDescription;
    private RecyclerView recyclerAssessments;
    private FloatingActionButton btnAddAssessment;
    private MaterialButton btnReviewTutor;
    private Dialog dialog;

    private AssessmentsAdapter assessmentsAdapter;
    private ArrayList<Assessment> assessmentArrayList;
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
        recyclerAssessments = view.findViewById(R.id.recyclerAssessments);
        recyclerAssessments.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAddAssessment = view.findViewById(R.id.btnAddAssessment);
        imgBackButton = view.findViewById(R.id.imgBackButton);
        btnReviewTutor = view.findViewById(R.id.btnReviewTutor);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
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
            bundle.putString("subjectID", subjectID);
            bundle.putString("tuteeID", tuteeID);
            createAssessmentFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.fragment_container, createAssessmentFragment).addToBackStack(null).commit();
        });

        btnReviewTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog reviewDialog = new Dialog(getContext());

                reviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                reviewDialog.getWindow().getAttributes().windowAnimations = R.style.AddQuestionDialogAnimation;
                reviewDialog.setContentView(R.layout.layout_dialog_review);
            }
        });

    }

    private void getSession() {
        assessmentArrayList = new ArrayList<>();
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
                    JSONArray assessments = sessionObject.getJSONArray("assessments");

                    JSONObject availabilityObject = object.getJSONObject("availability");
                    JSONArray days = availabilityObject.getJSONArray("days");
                    JSONArray time = availabilityObject.getJSONArray("time");

                    tuteeID = tuteeObject.getString("_id");
                    subjectID = subjectObject.getString("_id");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = format.parse(sessionObject.getString("startDate"));
                    String outputPattern = "yyyy-MM-dd";
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    Picasso.get().load(avatarObject.getString("url")).resize(500, 0).into(imgTutee);
                    txtName.setText(tuteeObject.getString("firstname") + " " + tuteeObject.getString("lastname"));
                    txtCourse.setText(courseObject.getString("name"));
                    txtDescription.setText(sessionObject.getString("description"));

                    for (int i = 0; i < assessments.length(); i++) {
                        JSONObject assessmentObject = assessments.getJSONObject(i);
                        Assessment assessment = new Assessment();

                        assessment.setAssessmentID(assessmentObject.getString("_id"));
                        assessment.setName(assessmentObject.getString("name"));
                        assessment.setScore(assessmentObject.getInt("score"));
                        assessment.setTotalItems(assessmentObject.getJSONArray("questions").length());

                        assessmentArrayList.add(assessment);
                    }

                    assessmentsAdapter = new AssessmentsAdapter(getContext(), assessmentArrayList, this);
                    recyclerAssessments.setAdapter(assessmentsAdapter);
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

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        AnswerAssessmentFragment answerAssessmentFragment = new AnswerAssessmentFragment();
        bundle.putString("_id", assessmentArrayList.get(position).getAssessmentID());
        answerAssessmentFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        ).replace(R.id.fragment_container, answerAssessmentFragment).addToBackStack(null).commit();
    }
}