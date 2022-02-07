package com.ituto.android.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ituto.android.Adapters.HomeCoursesAdapter;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Course;
import com.ituto.android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements HomeCoursesAdapter.OnItemListener {
    private static final String TAG = "MovieFragment";
    private View view;
    public static RecyclerView recyclerView, movieCasts;
    public static ArrayList<Course> arrayList;

    public static SwipeRefreshLayout refreshLayout;
    private HomeCoursesAdapter homeCoursesAdapter;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton btnAddMovie;
    private static final int GALLERY_ADD_POST = 2;
    private Dialog dialog;
    private TextView txtDialogMovieTitle, txtDialogMovieStory;
    private ImageView imgDialogMoviePoster;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tutee_home, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
//        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_POST && resultCode== Activity.RESULT_OK){

        }
    }

    @Override
    public void onItemClick(int position) {

    }


}
