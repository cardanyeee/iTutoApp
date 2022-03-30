package com.ituto.android.Fragments;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.ituto.android.Adapters.AnswerQuestionsAdapter;
import com.ituto.android.Constant;
import com.ituto.android.Models.Question;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnswerAssessmentFragment extends Fragment {

    private View view;

    private RecyclerView recyclerQuestions;
    private MaterialButton btnSubmitAnswers;
    private ImageView imgBackButton;
    private String loggedInAs;
    private TextView txtAssessmentName, txtSubject;

    private ArrayList<Question> questionArrayList;
    private AnswerQuestionsAdapter answerQuestionsAdapter;
    private SharedPreferences sharedPreferences;
    private String assessmentID;
    private Dialog dialog;

    private JSONArray answerArray;

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

        txtAssessmentName = view.findViewById(R.id.txtAssessmentName);
        txtSubject = view.findViewById(R.id.txtSubject);

        imgBackButton = view.findViewById(R.id.imgBackButton);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        dialog.setCancelable(false);
        dialog.show();

        StyleableToast.makeText(getContext(), assessmentID, R.style.CustomToast).show();
        recyclerQuestions = view.findViewById(R.id.recyclerQuestions);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        loggedInAs = sharedPreferences.getString("loggedInAs", "");

        btnSubmitAnswers = view.findViewById(R.id.btnSubmitAnswers);

        if (loggedInAs.equals("TUTOR")) {
            btnSubmitAnswers.setVisibility(View.GONE);
        }

        imgBackButton.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        btnSubmitAnswers.setOnClickListener(view -> {
            if (validateAnswers()) {
                submitAnswers();
            }
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
                        question.setTuteeAnswer("");

                        ArrayList<String> choiceArrayList = new ArrayList<>();
                        for (int c = 0; c < choices.length(); c++) {
                            JSONObject choice = choices.getJSONObject(c);
                            choiceArrayList.add(choice.getString("choice"));
                        }
                        question.setChoices(choiceArrayList);

                        questionArrayList.add(question);
                    }

                    answerQuestionsAdapter = new AnswerQuestionsAdapter(getContext(), questionArrayList);
                    recyclerQuestions.setAdapter(answerQuestionsAdapter);
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

    private Boolean validateAnswers() {
        answerArray = new JSONArray();
        for (int q = 0; q < questionArrayList.size(); q++) {
            if (questionArrayList.get(q).getTuteeAnswer().isEmpty()) {
                StyleableToast.makeText(getContext(), "Please answer all items. You don't have an answer on item no. " + String.valueOf(q+1), R.style.CustomToast).show();
                return false;
            }
            answerArray.put(questionArrayList.get(q).getTuteeAnswer());
        }
        return true;
    }

    @SuppressLint("LogNotTimber")
    private void submitAnswers() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.ANSWER_ASSESSMENT + "/" + assessmentID, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Dialog finishDialog = new Dialog(getContext());
                    finishDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    finishDialog.setContentView(R.layout.layout_dialog_finish);

                    TextView txtScore = finishDialog.findViewById(R.id.txtScore);
                    TextView txtTotalItems = finishDialog.findViewById(R.id.txtTotalItems);
                    Button btnYes = finishDialog.findViewById(R.id.btnYes);

                    txtScore.setText(object.getString("score"));
                    txtTotalItems.setText(object.getString("totalItems"));

                    Button btnNo = finishDialog.findViewById(R.id.btnNo);

                    finishDialog.show();

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishDialog.dismiss();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finishDialog.cancel();
                        }
                    });
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

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("answers", answerArray.toString());
                return map;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                runOnUiThread(() -> {
                    try {
                        String body;
                        body = new String(volleyError.networkResponse.data,"UTF-8");
                        JSONObject error = new JSONObject(body);
                        StyleableToast.makeText(getContext(), error.getString("message"), R.style.CustomToast).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                });
                return super.parseNetworkError(volleyError);
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }
}