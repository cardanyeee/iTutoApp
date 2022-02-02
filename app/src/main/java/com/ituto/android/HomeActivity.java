package com.ituto.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.ituto.android.Fragments.AccountFragment;
import com.ituto.android.Fragments.HomeFragment;
import com.ituto.android.Fragments.MessagesFragment;
import com.ituto.android.Fragments.TasksFragment;
import com.ituto.android.Fragments.TutorsFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigation;
    private static final int GALLERY_ADD_POST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
//        drawerLayout = findViewById(R.id.layoutHome);

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        init();
    }

    private void init() {
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
//        getUser();
    }

//    private void getUser() {
//        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")) {
//                    JSONObject user = object.getJSONObject("user");
//
//                    navUserName.setText(user.getString("name"));
//                    navUserEmail.setText(user.getString("email"));
////                    Picasso.get().load(Constant.URL+"storage/profiles/" + user.getString("photo")).fit().centerCrop().into(navUserPhoto);
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }, error -> {
//            error.printStackTrace();
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = userPref.getString("access_token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer" + token);
//                return map;
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        queue.add(request);
//    }

    @Override
    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.item_tutors:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TutorsFragment()).commit();
                break;
            case R.id.item_tasks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TasksFragment()).commit();
                break;
            case R.id.item_messages:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                break;
            case R.id.item_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
                break;
//                dialog = new Dialog(HomeActivity.this);
//                dialog.setContentView(R.layout.custom_alert_dialog);
//
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                Button btnYes = dialog.findViewById(R.id.btnYes);
//                Button btnNo = dialog.findViewById(R.id.btnNo);
//
//                dialog.show();
//
//                btnYes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        logout();
//                    }
//                });
//
//                btnNo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.cancel();
//                    }
//                });


        }
        return true;
    }

//    private void logout() {
//        dialog.dismiss();
//        StringRequest request = new StringRequest( Request.Method.POST, Constant.LOGOUT, response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")) {
//                    SharedPreferences.Editor editor = userPref.edit();
//                    editor.clear();
//                    editor.apply();
//                    startActivity( new Intent((getApplicationContext()), AuthActivity.class));
//                    this.finish();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }, error -> {
//            error.printStackTrace();
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = userPref.getString("access_token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer" + token);
//                return map;
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        queue.add(request);
//    }
}