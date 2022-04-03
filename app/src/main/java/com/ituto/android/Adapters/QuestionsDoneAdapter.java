package com.ituto.android.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.ituto.android.Models.Question;
import com.ituto.android.R;

import java.util.ArrayList;

public class QuestionsDoneAdapter extends RecyclerView.Adapter<QuestionsDoneAdapter.DoneQuestionHolder> {

    private Context context;
    private ArrayList<Question> questionArrayList;
    private SharedPreferences sharedPreferences;

    public QuestionsDoneAdapter(Context context, ArrayList<Question> questionArrayList) {
        this.context = context;
        this.questionArrayList = questionArrayList;
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public DoneQuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_question_done, parent, false);
        return new DoneQuestionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoneQuestionHolder holder, int position) {
        Question question = questionArrayList.get(position);
        ArrayList<String> choices = question.getChoices();
        String answer = question.getAnswer();

        holder.txtAnswer.setText(showAnswerLetter(Integer.parseInt(questionArrayList.get(position).getTuteeAnswer())));
        holder.txtAnswer.setEnabled(false);

        if (choices.get(Integer.parseInt(questionArrayList.get(position).getTuteeAnswer())).equals(answer)) {
            holder.txtAnswer.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLightTransparent));
        } else {
            holder.txtAnswer.setBackgroundColor(context.getResources().getColor(R.color.colorErrorTransparent));
        }

        holder.txtItemNum.setText(String.valueOf(position + 1) + ".");
        holder.txtQuestion.setText(question.getQuestion());
        holder.txtChoiceA.setText("A. " + choices.get(0));
        holder.txtChoiceB.setText("B. " + choices.get(1));
        holder.txtChoiceC.setText("C. " + choices.get(2));
        holder.txtChoiceD.setText("D. " + choices.get(3));

        if (choices.get(0).equals(answer)) {
            holder.txtChoiceA.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txtChoiceA.setTypeface(context.getResources().getFont(R.font.roboto_bold));
        }

        if (choices.get(1).equals(answer)) {
            holder.txtChoiceB.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txtChoiceB.setTypeface(context.getResources().getFont(R.font.roboto_bold));
        }

        if (choices.get(2).equals(answer)) {
            holder.txtChoiceC.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txtChoiceC.setTypeface(context.getResources().getFont(R.font.roboto_bold));
        }

        if (choices.get(3).equals(answer)) {
            holder.txtChoiceD.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txtChoiceD.setTypeface(context.getResources().getFont(R.font.roboto_bold));
        }
    }

    class DoneQuestionHolder extends RecyclerView.ViewHolder {

        private SwipeRevealLayout swipeQuestionLayout;
        private EditText txtAnswer;
        private TextView txtItemNum, txtQuestion, txtChoiceA, txtChoiceB, txtChoiceC, txtChoiceD;
        private ImageView btnDeleteQuestion;

        public DoneQuestionHolder(@NonNull View itemView) {
            super(itemView);
            txtAnswer = itemView.findViewById(R.id.txtAnswer);
            txtItemNum = itemView.findViewById(R.id.txtItemNum);
            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtChoiceA = itemView.findViewById(R.id.txtChoiceA);
            txtChoiceB = itemView.findViewById(R.id.txtChoiceB);
            txtChoiceC = itemView.findViewById(R.id.txtChoiceC);
            txtChoiceD = itemView.findViewById(R.id.txtChoiceD);

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
}
