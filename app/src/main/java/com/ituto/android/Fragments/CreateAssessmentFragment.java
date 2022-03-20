package com.ituto.android.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ituto.android.R;

public class CreateAssessmentFragment extends Fragment {

    private View view;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_assessment, container, false);
        init();
        return view;
    }

    private void init() {

    }
}