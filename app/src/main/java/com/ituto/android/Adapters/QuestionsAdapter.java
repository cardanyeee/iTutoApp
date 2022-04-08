package com.ituto.android.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Models.Question;
import com.ituto.android.R;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionHolder>{

    private Context context;
    private ArrayList<Question> questionArrayList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;

    public QuestionsAdapter(Context context, ArrayList<Question> questionArrayList) {
        this.context = context;
        this.questionArrayList = questionArrayList;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_question_create, parent, false);
        return new QuestionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHolder holder, @SuppressLint("RecyclerView") int position) {
        Question question = questionArrayList.get(position);
        ArrayList<String> choices = question.getChoices();

        holder.txtItemNum.setText(String.valueOf(position + 1) + ".");
        holder.txtQuestion.setText(question.getQuestion());
        holder.txtChoiceA.setText("A. " + choices.get(0));
        holder.txtChoiceB.setText("B. " + choices.get(1));
        holder.txtChoiceC.setText("C. " + choices.get(2));
        holder.txtChoiceD.setText("D. " + choices.get(3));
        holder.txtAnswer.setText(showAnswerLetter(choices.indexOf(question.getAnswer())));
        holder.txtAnswer.setEnabled(false);

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeQuestionLayout, String.valueOf(position));
        viewBinderHelper.closeLayout(String.valueOf(position));

        Dialog editQuestionDialog;
        editQuestionDialog = new Dialog(context);

        editQuestionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editQuestionDialog.getWindow().getAttributes().windowAnimations = R.style.AddQuestionDialogAnimation;
        editQuestionDialog.setContentView(R.layout.layout_dialog_add_question);

        TextInputEditText  txtQuestionEdit = editQuestionDialog.findViewById(R.id.txtQuestion);
        TextInputEditText  txtChoiceA = editQuestionDialog.findViewById(R.id.txtChoiceA);
        TextInputEditText  txtChoiceB = editQuestionDialog.findViewById(R.id.txtChoiceB);
        TextInputEditText  txtChoiceC = editQuestionDialog.findViewById(R.id.txtChoiceC);
        TextInputEditText  txtChoiceD = editQuestionDialog.findViewById(R.id.txtChoiceD);
        RadioGroup rdgChoices = editQuestionDialog.findViewById(R.id.rdgChoices);
        RadioButton rdbA = editQuestionDialog.findViewById(R.id.rdbA);
        RadioButton rdbB = editQuestionDialog.findViewById(R.id.rdbB);
        RadioButton rdbC = editQuestionDialog.findViewById(R.id.rdbC);
        RadioButton rdbD = editQuestionDialog.findViewById(R.id.rdbD);
        MaterialButton btnAdd = editQuestionDialog.findViewById(R.id.btnAdd);

        holder.btnEditQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtQuestionEdit.setText(question.getQuestion());
                txtChoiceA.setText(question.getChoices().get(0));
                txtChoiceB.setText(question.getChoices().get(1));
                txtChoiceC.setText(question.getChoices().get(2));
                txtChoiceD.setText(question.getChoices().get(3));

                if (question.getAnswer().equals(question.getChoices().get(0))) {
                    rdbA.setChecked(true);
                } else if (question.getAnswer().equals(question.getChoices().get(1))) {
                    rdbB.setChecked(true);
                } else if (question.getAnswer().equals(question.getChoices().get(2))) {
                    rdbC.setChecked(true);
                } else if (question.getAnswer().equals(question.getChoices().get(3))) {
                    rdbD.setChecked(true);
                }

                editQuestionDialog.show();
            }
        });

        holder.btnDeleteQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionArrayList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateDialog(editQuestionDialog)) {
                    ArrayList<String> choices = new ArrayList();

                    question.setQuestion(txtQuestionEdit.getText().toString().trim());
                    choices.add(txtChoiceA.getText().toString().trim());
                    choices.add(txtChoiceB.getText().toString().trim());
                    choices.add(txtChoiceC.getText().toString().trim());
                    choices.add(txtChoiceD.getText().toString().trim());

                    question.setChoices(choices);

                    question.setAnswer(getAnswer(editQuestionDialog));

                    notifyDataSetChanged();

                    editQuestionDialog.dismiss();
                }
            }
        });
    }

    class QuestionHolder extends RecyclerView.ViewHolder {

        private SwipeRevealLayout swipeQuestionLayout;
        private EditText txtAnswer;
        private TextView txtItemNum, txtQuestion, txtChoiceA, txtChoiceB, txtChoiceC, txtChoiceD;
        private ImageView btnDeleteQuestion, btnEditQuestion;

        public QuestionHolder(@NonNull View itemView) {
            super(itemView);
            swipeQuestionLayout = itemView.findViewById(R.id.swipeQuestionLayout);
            txtAnswer = itemView.findViewById(R.id.txtAnswer);
            txtItemNum = itemView.findViewById(R.id.txtItemNum);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtChoiceA = itemView.findViewById(R.id.txtChoiceA);
            txtChoiceB = itemView.findViewById(R.id.txtChoiceB);
            txtChoiceC = itemView.findViewById(R.id.txtChoiceC);
            txtChoiceD = itemView.findViewById(R.id.txtChoiceD);

            btnEditQuestion = itemView.findViewById(R.id.btnEditQuestion);
            btnDeleteQuestion = itemView.findViewById(R.id.btnDeleteQuestion);
        }
    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    private String showAnswerLetter(int index) {
        String answer = "";

        if (index == 0) {
            answer = "A";
        }

        if (index == 1) {
            answer = "B";
        }

        if (index == 2) {
            answer = "C";
        }

        if (index == 3) {
            answer = "D";
        }

        return answer;
    }

    private boolean validateDialog(Dialog editQuestionDialog) {

        if (((TextInputEditText)editQuestionDialog.findViewById(R.id.txtQuestion)).getText().toString().isEmpty()) {
            StyleableToast.makeText(context, "Please input a question", R.style.CustomToast).show();
            return false;
        }

        if (((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceA)).getText().toString().isEmpty()) {
            StyleableToast.makeText(context, "Please input choice A", R.style.CustomToast).show();
            return false;
        }

        if (((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceB)).toString().isEmpty()) {
            StyleableToast.makeText(context, "Please input choice B", R.style.CustomToast).show();
            return false;
        }

        if (((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceC)).toString().isEmpty()) {
            StyleableToast.makeText(context, "Please input choice C", R.style.CustomToast).show();
            return false;
        }

        if (((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceD)).getText().toString().isEmpty()) {
            StyleableToast.makeText(context, "Please input choice D", R.style.CustomToast).show();
            return false;
        }

        if (((RadioGroup)editQuestionDialog.findViewById(R.id.rdgChoices)).getCheckedRadioButtonId() == -1) {
            StyleableToast.makeText(context, "Please select an answer", R.style.CustomToast).show();
            return false;
        }

        return true;
    }

    private String getAnswer(Dialog editQuestionDialog) {
        String answer = "";

        if (((RadioButton)editQuestionDialog.findViewById(R.id.rdbA)).isChecked()) {
            answer = ((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceA)).getText().toString().trim();
        }

        if (((RadioButton)editQuestionDialog.findViewById(R.id.rdbB)).isChecked()) {
            answer = ((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceB)).getText().toString().trim();
        }

        if (((RadioButton)editQuestionDialog.findViewById(R.id.rdbC)).isChecked()) {
            answer = ((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceC)).getText().toString().trim();
        }

        if (((RadioButton)editQuestionDialog.findViewById(R.id.rdbD)).isChecked()) {
            answer = ((TextInputEditText)editQuestionDialog.findViewById(R.id.txtChoiceD)).getText().toString().trim();
        }

        return answer;
    }
}
