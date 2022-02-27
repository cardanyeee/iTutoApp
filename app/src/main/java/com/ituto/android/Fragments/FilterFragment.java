package com.ituto.android.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ituto.android.R;

public class FilterFragment extends Fragment {

    private View view;
    private Button btnApplyFilters;

    private TextInputLayout txtLayoutCourse, txtLayoutSubject, txtLayoutAvailability;
    private TextInputEditText txtCourse, txtSubject, txtAvailability;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter, container, false);
        init();
        return view;
    }

    private void init() {
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);

        btnApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                TutorsFragment tutorsFragment = new TutorsFragment();
                bundle.putBoolean("filter", true);
                // R.id.container - the id of a view that will hold your fragment; usually a FrameLayout
                tutorsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, tutorsFragment).addToBackStack(null).commit();
            }
        });
    }



}