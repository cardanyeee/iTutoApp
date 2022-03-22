package com.ituto.android.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ituto.android.R;

public class AnswerAssessmentFragment extends Fragment {

    private View view;

    private RecyclerView recyclerQuestions;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_assessment_answer, container, false);
        init();
        return view;
    }

    private void init() {
        recyclerQuestions = view.findViewById(R.id.recyclerQuestions);
    }
}