package com.ituto.android.Fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.ituto.android.Adapters.QuestionsAdapter;
import com.ituto.android.Models.Question;
import com.ituto.android.Models.Tutor;
import com.ituto.android.R;

import java.util.ArrayList;

public class CreateAssessmentFragment extends Fragment {

    private View view;
    private ExtendedFloatingActionButton btnAddQuestion;
    private RecyclerView recyclerQuestions;
    private ArrayList<Question> questionArrayList;

    private QuestionsAdapter questionsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assessment_create, container, false);
        init();
        return view;
    }

    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);

        recyclerQuestions = view.findViewById(R.id.recyclerQuestions);
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        btnAddQuestion = view.findViewById(R.id.btnAddQuestion);

        Question question = new Question();
        questionArrayList = new ArrayList<>();
        question.setQuestion("How are you doing?");
        ArrayList<String> choices = new ArrayList();
        choices.add("Choice A");
        choices.add("Choice B");
        choices.add("Choice C");
        choices.add("Choice D");

        question.setChoices(choices);

        question.setAnswer("A");
        questionArrayList.add(question);

        questionsAdapter = new QuestionsAdapter(getContext(), questionArrayList);
        recyclerQuestions.setAdapter(questionsAdapter);

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
                dialog.setContentView(R.layout.layout_dialog_add_question);
                MaterialButton btnAdd = dialog.findViewById(R.id.btnAdd);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

    }
}