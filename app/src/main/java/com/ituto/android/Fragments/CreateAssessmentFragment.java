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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Adapters.QuestionsAdapter;
import com.ituto.android.Models.Question;
import com.ituto.android.Models.Tutor;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;

public class CreateAssessmentFragment extends Fragment {

    private View view;
    private ExtendedFloatingActionButton btnAddQuestion;
    private RecyclerView recyclerQuestions;
    private ArrayList<Question> questionArrayList;

    private QuestionsAdapter questionsAdapter;
    private Dialog addQuestionDialog;

    private TextInputEditText txtQuestion, txtChoiceA, txtChoiceB, txtChoiceC, txtChoiceD;
    private RadioGroup rdgChoices;
    private RadioButton rdbA, rdbB, rdbC, rdbD;
    private MaterialButton btnAdd;

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

        questionArrayList = new ArrayList<>();
        questionsAdapter = new QuestionsAdapter(getContext(), questionArrayList);
        recyclerQuestions.setAdapter(questionsAdapter);

        addQuestionDialog = new Dialog(getContext());

        addQuestionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        addQuestionDialog.getWindow().getAttributes().windowAnimations = R.style.AddQuestionDialogAnimation;
        addQuestionDialog.setContentView(R.layout.layout_dialog_add_question);

        txtQuestion = addQuestionDialog.findViewById(R.id.txtQuestion);
        txtChoiceA = addQuestionDialog.findViewById(R.id.txtChoiceA);
        txtChoiceB = addQuestionDialog.findViewById(R.id.txtChoiceB);
        txtChoiceC = addQuestionDialog.findViewById(R.id.txtChoiceC);
        txtChoiceD = addQuestionDialog.findViewById(R.id.txtChoiceD);
        rdgChoices = addQuestionDialog.findViewById(R.id.rdgChoices);
        rdbA = addQuestionDialog.findViewById(R.id.rdbA);
        rdbB = addQuestionDialog.findViewById(R.id.rdbB);
        rdbC = addQuestionDialog.findViewById(R.id.rdbC);
        rdbD = addQuestionDialog.findViewById(R.id.rdbD);
        btnAdd = addQuestionDialog.findViewById(R.id.btnAdd);

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestionDialog.show();
            }
        });

        btnAdd.setOnClickListener(view -> {
            if (validateDialog()) {
                Question q = new Question();
                ArrayList<String> choices = new ArrayList();

                q.setQuestion(txtQuestion.getText().toString().trim());
                choices.add(txtChoiceA.getText().toString().trim());
                choices.add(txtChoiceB.getText().toString().trim());
                choices.add(txtChoiceC.getText().toString().trim());
                choices.add(txtChoiceD.getText().toString().trim());

                q.setChoices(choices);

                q.setAnswer(getAnswer());
                questionArrayList.add(q);

                recyclerQuestions.getAdapter().notifyDataSetChanged();

                addQuestionDialog.dismiss();
            }
        });

    }

    private boolean validateDialog() {

        if (txtQuestion.getText().toString().isEmpty()) {
            StyleableToast.makeText(getContext(), "Please input a question", R.style.CustomToast).show();
            return false;
        }

        if (txtChoiceA.getText().toString().isEmpty()) {
            StyleableToast.makeText(getContext(), "Please input choice A", R.style.CustomToast).show();
            return false;
        }

        if (txtChoiceB.getText().toString().isEmpty()) {
            StyleableToast.makeText(getContext(), "Please input choice B", R.style.CustomToast).show();
            return false;
        }

        if (txtChoiceC.getText().toString().isEmpty()) {
            StyleableToast.makeText(getContext(), "Please input choice C", R.style.CustomToast).show();
            return false;
        }

        if (txtChoiceD.getText().toString().isEmpty()) {
            StyleableToast.makeText(getContext(), "Please input choice D", R.style.CustomToast).show();
            return false;
        }

        if (rdgChoices.getCheckedRadioButtonId() == -1) {
            StyleableToast.makeText(getContext(), "Please select an answer", R.style.CustomToast).show();
            return false;
        }

        return true;
    }

    private String getAnswer() {
        String answer = "";

        if (rdbA.isChecked()) {
            answer = txtChoiceA.getText().toString().trim();
        }

        if (rdbB.isChecked()) {
            answer = txtChoiceB.getText().toString().trim();
        }

        if (rdbC.isChecked()) {
            answer = txtChoiceC.getText().toString().trim();
        }

        if (rdbD.isChecked()) {
            answer = txtChoiceD.getText().toString().trim();
        }

        return answer;
    }
}