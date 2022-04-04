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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.ituto.android.Adapters.QuestionsAnswerAdapter;
import com.ituto.android.Adapters.QuestionsDoneAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Question;
import com.ituto.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AssessmentDoneFragment extends Fragment {

    private View view;

    private RecyclerView recyclerQuestions;
    private ImageView imgBackButton;
    private String loggedInAs;
    private TextView txtAssessmentName, txtSubject;

    private ArrayList<Question> questionArrayList;
    private QuestionsDoneAdapter questionsDoneAdapter;
    private SharedPreferences sharedPreferences;
    private String assessmentID;
    private Dialog dialog;

    private JSONArray answerArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assessment_done, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        assessmentID = getArguments().getString("_id");

        txtAssessmentName = view.findViewById(R.id.txtAssessmentName);
        txtSubject = view.findViewById(R.id.txtSubject);

        imgBackButton = view.findViewById(R.id.imgBackButton);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        dialog.setCancelable(false);
        dialog.show();

        recyclerQuestions = view.findViewById(R.id.recyclerQuestions);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        loggedInAs = sharedPreferences.getString("loggedInAs", "");


        imgBackButton.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

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
                    JSONArray answersArray = assessmentObject.getJSONArray("answers");
                    JSONObject subjectObject = assessmentObject.getJSONObject("subject");
                    JSONObject tutorObject = assessmentObject.getJSONObject("tutor");
                    JSONObject tuteeObject = assessmentObject.getJSONObject("tutee");

                    txtAssessmentName.setText(assessmentObject.getString("name"));
                    txtSubject.setText(subjectObject.getString("name"));

                    for (int q = 0; q < questionsArray.length(); q++) {
                        Question question = new Question();
                        JSONObject questionObject = questionsArray.getJSONObject(q);
                        JSONArray choices = questionObject.getJSONArray("choices");

                        question.setQuestion(questionObject.getString("question"));
                        question.setAnswer(questionObject.getString("answer"));
                        question.setTuteeAnswer(answersArray.length() == 0 ? "4" : answersArray.getString(q));

                        ArrayList<String> choiceArrayList = new ArrayList<>();
                        for (int c = 0; c < choices.length(); c++) {
                            JSONObject choice = choices.getJSONObject(c);
                            choiceArrayList.add(choice.getString("choice"));
                        }
                        question.setChoices(choiceArrayList);

                        questionArrayList.add(question);
                    }

                    questionsDoneAdapter = new QuestionsDoneAdapter(getContext(), questionArrayList);
                    recyclerQuestions.setAdapter(questionsDoneAdapter);
                }

                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }

        }, error -> {
            dialog.dismiss();
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