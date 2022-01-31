package com.ituto.android.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Adapters.HomeCoursesAdapter;
import com.ituto.android.Constant;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Course;
import com.ituto.android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        view = inflater.inflate(R.layout.layout_home, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeHome);
        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);

        getMovies();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMovies();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_POST && resultCode== Activity.RESULT_OK){

        }
    }

    private void getMovies() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.COURSES, response -> {


        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("access_token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onItemClick(int position) {

    }


}
