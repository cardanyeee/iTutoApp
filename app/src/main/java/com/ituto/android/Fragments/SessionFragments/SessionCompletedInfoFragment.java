package com.ituto.android.Fragments.SessionFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Adapters.AssessmentsDoneAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.AssessmentAnswerFragment;
import com.ituto.android.Fragments.AssessmentDoneFragment;
import com.ituto.android.Models.Assessment;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SessionCompletedInfoFragment extends Fragment implements AssessmentsDoneAdapter.OnItemListener {
    private View view;
    private ImageView imgBackButton;
    private MaterialCardView crdTutee, crdDescription;
    private TextView txtSubjectName, txtTime, txtName, txtCourse, txtYearLevel, txtDescription, txtTutor, txtTutee;
    private RecyclerView recyclerAssessments;
    private Dialog dialog;

    private AssessmentsDoneAdapter assessmentsDoneAdapter;
    private ArrayList<Assessment> assessmentArrayList;
    private SharedPreferences sharedPreferences;
    private String loggedInAs;

    private RatingBar rtbTutorRating;
    private TextInputEditText txtComment;

    private String sessionID, tuteeID, tutorID, subjectID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_completed_info, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        sessionID = getArguments().getString("_id");

        txtSubjectName = view.findViewById(R.id.txtSubjectName);
        txtTime = view.findViewById(R.id.txtTime);
        txtDescription = view.findViewById(R.id.txtDescription);
        txtTutor = view.findViewById(R.id.txtTutor);
        txtTutee = view.findViewById(R.id.txtTutee);
        recyclerAssessments = view.findViewById(R.id.recyclerAssessments);
        recyclerAssessments.setLayoutManager(new LinearLayoutManager(getContext()));
        imgBackButton = view.findViewById(R.id.imgBackButton);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        dialog.setCancelable(false);
        dialog.show();
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");

        imgBackButton.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStack());

        getSession();

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

                    tutorID = tutorObject.getString("_id");
                    tuteeID = tuteeObject.getString("_id");
                    subjectID = subjectObject.getString("_id");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = format.parse(sessionObject.getString("startDate"));
                    String outputPattern = "yyyy-MM-dd";
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                    txtSubjectName.setText(subjectObject.getString("name"));
                    txtTime.setText(timeObject.getString("min") + " - " + timeObject.getString("max"));
                    txtDescription.setText(sessionObject.getString("description"));
                    txtTutor.setText(tutorObject.getString("firstname") + " " + tutorObject.getString("lastname"));
                    txtTutee.setText(tuteeObject.getString("firstname") + " " + tuteeObject.getString("lastname"));

                    for (int i = 0; i < assessments.length(); i++) {
                        JSONObject assessmentObject = assessments.getJSONObject(i);
                        Assessment assessment = new Assessment();

                        assessment.setAssessmentID(assessmentObject.getString("_id"));
                        assessment.setName(assessmentObject.getString("name"));
                        assessment.setScore(assessmentObject.getInt("score"));
                        assessment.setTotalItems(assessmentObject.getJSONArray("questions").length());

                        assessmentArrayList.add(assessment);
                    }

                    assessmentsDoneAdapter = new AssessmentsDoneAdapter(getContext(), assessmentArrayList, this);
                    recyclerAssessments.setAdapter(assessmentsDoneAdapter);
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
        AssessmentDoneFragment assessmentDoneFragment = new AssessmentDoneFragment();
        bundle.putString("_id", assessmentArrayList.get(position).getAssessmentID());
        assessmentDoneFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        ).replace(R.id.fragment_container, assessmentDoneFragment).addToBackStack(null).commit();
    }
}