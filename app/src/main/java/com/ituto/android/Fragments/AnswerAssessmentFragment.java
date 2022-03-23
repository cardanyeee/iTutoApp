package com.ituto.android.Fragments;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.ituto.android.Adapters.AnswerQuestionsAdapter;
import com.ituto.android.Adapters.AssessmentsAdapter;
import com.ituto.android.Adapters.QuestionsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Assessment;
import com.ituto.android.Models.Question;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;
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

public class AnswerAssessmentFragment extends Fragment {

    private View view;

    private RecyclerView recyclerQuestions;

    private ArrayList<Question> questionArrayList;
    private AnswerQuestionsAdapter answerQuestionsAdapter;
    private SharedPreferences sharedPreferences;
    private String assessmentID;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assessment_answer, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        assessmentID = getArguments().getString("_id");

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.setCancelable(false);
        dialog.show();

        StyleableToast.makeText(getContext(), assessmentID, R.style.CustomToast).show();
        recyclerQuestions = view.findViewById(R.id.recyclerQuestions);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        getAssessment();
    }

    private void getAssessment() {
        questionArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_ASSESSMENT + "/" + assessmentID, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    JSONObject assessmentObject = object.getJSONObject("assessment");
                    JSONArray questionsArray = assessmentObject.getJSONArray("questions");

                    for (int q = 0; q < questionsArray.length(); q++) {
                        Question question = new Question();
                        JSONObject questionObject = questionsArray.getJSONObject(q);

                        question.setQuestion(questionObject.getString("question"));

                        questionArrayList.add(question);
                    }

                    JSONObject subjectObject = assessmentObject.getJSONObject("subject");
                    JSONObject tutorObject = assessmentObject.getJSONObject("tutor");
                    JSONObject tuteeObject = assessmentObject.getJSONObject("tutee");

                    answerQuestionsAdapter = new AnswerQuestionsAdapter(getContext(), questionArrayList);
                    recyclerQuestions.setAdapter(answerQuestionsAdapter);
                }

                dialog.dismiss();
            } catch (JSONException e) {
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